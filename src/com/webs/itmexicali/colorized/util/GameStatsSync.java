package com.webs.itmexicali.colorized.util;

import java.util.concurrent.TimeUnit;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.android.gms.games.achievement.Achievements;
import com.google.android.gms.games.achievement.Achievements.LoadAchievementsResult;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.android.gms.games.leaderboard.Leaderboards.LoadPlayerScoreResult;
import com.webs.itmexicali.colorized.GameActivity;
import com.webs.itmexicali.colorized.R;

public class GameStatsSync {
	
	/** Compare achievements stored in google game services to unlock them within
	 * the app and store them locally*/
	public void syncAchievements(GoogleApiClient gApiClient)  {

    	boolean fullLoad = false;  // set to 'true' to reload all achievements (ignoring cache)
    	long waitTime = 60;    // seconds to wait for achievements to load before timing out

	   // load achievements
	   PendingResult<LoadAchievementsResult> p = Games.Achievements.load(gApiClient, fullLoad );
	   //if p.awaits() is not declared for PendingResult, check the IMPORTS TO BE CORRECT
       Achievements.LoadAchievementsResult r = (Achievements.LoadAchievementsResult)p.await(waitTime, TimeUnit.SECONDS);
    	   
        int status = r.getStatus().getStatusCode();
        if ( status != GamesStatusCodes.STATUS_OK )  {
           r.release();
           return;           // Error Occured
        }

        // cache the loaded achievements
        AchievementBuffer buf = r.getAchievements();
        int bufSize = buf.getCount();
        for ( int i = 0; i < bufSize; i++ )  {
           Achievement ach = buf.get( i );

           // here you now have access to the achievement's data
           String id = ach.getName()+ach.getDescription()+"\t ";  // the achievement ID string
           boolean unlocked = ach.getState() == Achievement.STATE_UNLOCKED;  // is unlocked
           boolean incremental = ach.getType() == Achievement.TYPE_INCREMENTAL;  // is incremental
           int steps = 0;
           if ( incremental )
              steps = ach.getCurrentSteps();  // current incremental steps
           Log.d(GameStatsSync.class.getSimpleName(),"Ach:"+id+",unlocked?"+unlocked+",incremental?"+incremental+(incremental?" steps: "+steps:""));
        }
        buf.close();
        r.release();
     }
    
    
	/** Compare Leaderboards scores stored in google game services to sync them
	 * within the app's ones and store the greater ones locally*/
    public void syncLeaderboardsScores(GoogleApiClient gApiClient, boolean byCallback)  {
    	long time = System.currentTimeMillis();
    	
    	int[] ids={R.string.leaderboard_small_board,R.string.leaderboard_medium_board,R.string.leaderboard_large_board,R.string.leaderboard_all_board_sizes};
    	
    	for(int i =0; i<ids.length;i++){
    		
    		final String lboardID = GameActivity.instance.getString(ids[i]);
    		final int lboardNumber = i;
    		// load leaderboards
 		   	final PendingResult<LoadPlayerScoreResult> p = Games.Leaderboards.loadCurrentPlayerLeaderboardScore(gApiClient,
 		   		lboardID, LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC);
    		
    		if(byCallback){
    			loadLeaderboardByCallback(p,lboardNumber,lboardID);
    		}
    		else{
    			new Thread(new Runnable(){public void run(){loadLeaderboardByWaiting(p,lboardNumber,lboardID);}}).start();
    		}
		   
    	}
    	Log.d(GameStatsSync.class.getSimpleName(),"Tiempo en ejecucion TOTAL: "+(System.currentTimeMillis()-time));
     }
    
    /** Set a callback to the PendingResult containing the loadPlayerScoreResult, once the PendingResult is ready
     * it will trigger the onResult method to do certain action with the leaderboardScore info
     * @param p PendingResult to set a callback
     * @param bNumber the leaderboard number in the loop
     * @param bID the ID of the leaderboard*/
    private void loadLeaderboardByCallback(	PendingResult<LoadPlayerScoreResult> p, final int bNumber, final String bID){
    	final long tempTime = System.currentTimeMillis();
    	p.setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
            public void onResult(Leaderboards.LoadPlayerScoreResult r) {
            	doWhatYouWantWithYourLeaderboardInfo(r,bNumber,bID,tempTime);
            }
        });
    }
    
    /** Wait for the PendingResult containing the loadPlayerScoreResult blocking current thread (KEEP IT IN MIND!!!),
     * once the PendingResult is ready it will return the leaderboardScore info to do certain action with it
     * @param p PendingResult to await for it to finish or timeout
     * @param bNumber the leaderboard number in the loop
     * @param bID the ID of the leaderboard*/
    private void loadLeaderboardByWaiting(	PendingResult<LoadPlayerScoreResult> p,  int bNumber,  String bID){
    	long tempTime = System.currentTimeMillis();
    	long waitTime = 10;    // seconds to wait for achievements to load before timing out
    	//if p.awaits() is not declared for PendingResult, check the IMPORTS TO BE CORRECT
    	Leaderboards.LoadPlayerScoreResult r = (Leaderboards.LoadPlayerScoreResult)p.await(waitTime, TimeUnit.SECONDS);
    	doWhatYouWantWithYourLeaderboardInfo(r,bNumber,bID,tempTime);
    }
    
    /** Excecute this method which processes the LeaderboardScore info once the result is returned
     * @param r LoadPlayerScoreResult containing all the info from the leaderboard
     * @param bNumber the leaderboard number in the loop
     * @param bID the ID of the leaderboard
     * @param tTime - TEMPORAL PARAMETER*/
    private void doWhatYouWantWithYourLeaderboardInfo(Leaderboards.LoadPlayerScoreResult r,  int bNumber,
    		 String bID, long  tTime){
    	//TODO - correct this method and stop playing(LEARNING) around
    	if ( r.getStatus().getStatusCode() != GamesStatusCodes.STATUS_OK )  {
			   Log.d(GameStatsSync.class.getSimpleName(),"Esto no jalo! -Tiempo en ejecucion "+bNumber+": "+(System.currentTimeMillis()-tTime));
		       return;           // Error Occured
		   }
		   // cache the loaded achievements
		   LeaderboardScore lScore = r.getScore();
		   Log.d(GameStatsSync.class.getSimpleName(),"Leaderboard '"+bID+"' Score:"+lScore.getDisplayScore()+"("+lScore.getRawScore()+")");
		   Log.d(GameStatsSync.class.getSimpleName(),"Tiempo en ejecucion "+bNumber+": "+(System.currentTimeMillis()-tTime));
    }

}
