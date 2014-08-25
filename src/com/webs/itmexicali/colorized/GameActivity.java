package com.webs.itmexicali.colorized;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.webs.itmexicali.colorized.gamestates.BaseState;
import com.webs.itmexicali.colorized.gamestates.StateMachine;
import com.webs.itmexicali.colorized.gamestates.GameState.GameFinishedListener;
import com.webs.itmexicali.colorized.util.Const;
import com.webs.itmexicali.colorized.util.ProgNPrefs;

import ProtectedInt.ProtectedInt;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

public class GameActivity extends BaseGameActivity implements GameFinishedListener{
	
	//AdMob Advertising
    /** The view to show the ad. */
    private AdView adView = null;
     
    private InterstitialAd interstitial =  null;
	
	/** This activity instance, to access its members from other classes*/
    public static GameActivity instance;
    
    /**Media player to play sounds of user interactions & background music.*/
    private MediaPlayer soundPlayer = null, musicPlayer = null;
    //private SoundType previousSound = SoundType.NONE;
    public static enum SoundType{ NONE, TOUCH, WIN, LOSE};
    
    
	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(GameActivity.class.getSimpleName(),"onCreate()");
		enableDebugLog(Const.D);
		instance = this;
		
		//run on FullScreen with no Action and Navigation Bars
		Const.setFullScreen(this);
		
		//Setup ProtectedInt once
		if(!ProtectedInt.isSetup())
			ProtectedInt.setup();
		
		//Init Preferences once 
		ProgNPrefs.initPreferences(this);
		
		//Init StateMachine
		if(!StateMachine.isSetUp())
			StateMachine.setup(this);

		//set the game screen
		setContentView(R.layout.game_screen);

	    //Keep screen on
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		/*************************************	ADS ADMOB	*********************************************/
		// Create an ad.
        adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(Const.ADVIEW_AD_UNIT_ID);
        adView.setVisibility(View.GONE);
        adView.setAdListener(new AdListener() {
        	@Override
    		public void onAdOpened() {// Save app state before going to the ad overlay.
    			Log.d(Const.TAG,"AdView - Opened");
    			adView.setVisibility(View.GONE);
    		}
    		@Override
    		public void onAdFailedToLoad(int errorCode){
    			Log.d(Const.TAG,"AdView - FailedToLoad = "+errorCode);
    			adView.setVisibility(View.GONE);
    		}
    		@Override
    		public void onAdLoaded(){
    			adView.setVisibility(View.VISIBLE);
    		}
    	});		
        
        
        // Add the AdView to the view hierarchy. The view will have no size
        // until the ad is loaded.
        LinearLayout layout = (LinearLayout) findViewById(R.id.LayMain);
        /*
          
         When the game_Screen.xml root tag used to be RelativeLayout, the ad was just
         OVER the GameView, now they share the screen size
          
        if(adView != null){
        	layout.removeView(adView); // remove the existing adView
        	adView.destroy();
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    	params.addRule(RelativeLayout.CENTER_HORIZONTAL);
    	layout.addView(adView, params);
    	*/
		layout.addView(adView);
		
        
        // Start loading the ad in the background.
        adView.loadAd(createAdRequest());
        
        
        // Create the INTERSTTIAL.
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(Const.INTERSTITIAL_AD_UNIT_ID);
        // Begin loading your interstitial.
        interstitial.loadAd(createAdRequest());
        interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
              interstitial.loadAd(createAdRequest());
            }
        });
        
        /*************************************	ADS ADMOB	*********************************************/
         
	}
	
	/*************************************	ADS ADMOB	*********************************************/
	/** Create an ad request. Check logcat output for the hashed device ID to
     *	get test ads on a physical device.*/
	private AdRequest createAdRequest(){
		return new AdRequest.Builder()
	            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	            .addTestDevice("584586A082596B5844C4E301E1285E95") //My Nexus 4
	            .build();
	}
	

	// Invoke displayInterstitial() when you are ready to display an interstitial.
	public void displayInterstitial() {
		Log.v(this.getLocalClassName(),"ShowingInterstitials ");
		if (interstitial.isLoaded()) {
			interstitial.show();
		}
	}
	  
	/*************************************	ADS ADMOB	*********************************************/
	
	
	@Override
	public void onStart(){
		super.onStart();
		Log.v(GameActivity.class.getSimpleName(),"onStart()");
		
		startMusicPlayer();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		Log.v(GameActivity.class.getSimpleName(),"onResume()");
		
		//keep sticky full immersive screen in case we lost it
		Const.setFullScreen(this);
		
		//ADMOB Advertising
		if (adView != null) {
  	      adView.resume();
  		}
		//start Music playing
		playMusic(true);
	}
	
	
	
	@Override
	public void onPause(){
		super.onPause();
		Log.v(GameActivity.class.getSimpleName(),"onPause()");
		
		//ADMOB Advertising
		if (adView != null) {
			adView.pause();
  		}
		
		playMusic(false);		
	}
	
	@Override
	public void onStop(){
		super.onStop();
		//release resources of the media player and delete it
		Log.v(GameActivity.class.getSimpleName(),"onStop()");
		if (soundPlayer != null){
			soundPlayer.stop();
			soundPlayer.release();
			soundPlayer = null;
		}
		
		if (musicPlayer != null){
			musicPlayer.stop();
			musicPlayer.release();
			musicPlayer = null;
		}
	}
	
	public void onDestroy(){
		super.onDestroy();
		//release resources of the media player and delete it
		Log.v(GameActivity.class.getSimpleName(),"onDestroy()");
	}
	
	
	/** Display an exit confirmation dialog to prevent accidentally quitting the game*/
	public void showExitDialog(){
		//Use the Builder class for convenient dialog construction
		new AlertDialog.Builder(GameActivity.this)
		.setMessage(R.string.exit_confirmation)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) { // Exit the game
               GameActivity.this.finish();
           }
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {// dismiss menu
               Const.setFullScreen(GameActivity.this);
               playMusic(true);
           }
		})
		.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				Const.setFullScreen(GameActivity.this);
	            playMusic(true);
			}
		})
		.create()
		.show();
	}
	
	
	
	@Override
	public void onBackPressed(){
		if(StateMachine.getIns().onBackPressed())
			return;
		
		showExitDialog();
		super.onBackPressed();
	}
	
	/** Play sounds when user touch the controls
	 * @param SoundType to select type of sound to play*/ 
	public void playSound(SoundType s){
		//if we have an instance of the player and the user wants to play sounds
		if(ProgNPrefs.getIns().playSFX()){
			//if(s != previousSound){
				//previousSound = s;
	
				//if it was playing, stop it to restart it
				if(soundPlayer != null){
					soundPlayer.stop();
					soundPlayer.release();
				}
				
				//get the new sound
				switch(s){
					case TOUCH:	soundPlayer = MediaPlayer.create(this, R.raw.confirm); break;
					case WIN:	soundPlayer = MediaPlayer.create(this, R.raw.win); break;
					case LOSE:	soundPlayer = MediaPlayer.create(this, R.raw.lose); break;
					default:	break;
				}				
			//}
		
			soundPlayer.start();
		}
			
	}
	
	/** Play/Stop Background music
	 * @param play true to play music, false to pause it*/ 
	public void playMusic(boolean play){
		if(musicPlayer == null)
			startMusicPlayer();
		
		//if we have an instance of the player and the user wants to play sounds
		if (play && ProgNPrefs.getIns().playMusic()){
			if(!musicPlayer.isPlaying())
				musicPlayer.start();
		}else
			if(musicPlayer.isPlaying()){
				musicPlayer.pause();
			}
	}
	
	private void startMusicPlayer(){
		//start Player with a resource
		if( musicPlayer == null){
			musicPlayer=MediaPlayer.create(this, R.raw.music);
			musicPlayer.setLooping(true);
		}
	}
	
	/*------------------------ LEADERBOARDS and ACHIEVEMENTS METHODS ----------------------------------*/
	

	@Override
	public void onSignInFailed() {
		// Show sign-in button on main menu
		
	}

	@Override
    public void onSignInSucceeded() {
        // Show sign-out button on main menu

        // Show "you are signed in" message on win screen, with no sign in button.

        // Set the greeting appropriately on main menu
        Player p = Games.Players.getCurrentPlayer(getApiClient());
        String displayName;
        if (p == null) {
            displayName = "unknown_user";
        } else {
            displayName = p.getDisplayName();
        }
        Log.d(GameActivity.class.getSimpleName(),"User: "+displayName);
        //mMainMenuFragment.setGreeting("Hello, " + displayName);


        // if we have accomplishments to push, push them
        // Check progress!! - if (!mOutbox.isEmpty())
        {
            pushAccomplishments();
            Toast.makeText(this, getString(R.string.your_progress_will_be_uploaded),
                    Toast.LENGTH_LONG).show();
        }
    }

	/** Try to show Achievements activity, if not signed in, show message
	 * @return true if Achievements were shown*/
	public boolean onShowAchievementsRequested() {
        if (isSignedIn()) {        	
            startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()),
                    Const.RC_UNUSED);
            return true;
        } else {
            showAlert(getString(R.string.achievements_not_available));

			StateMachine.getIns().pushState(BaseState.statesIDs.LEADER);
        }
        return false;
    }

	/** Try to show Leaderboards activity, if not signed in, show message
	 * @return true if Leaderboards were shown*/
    public boolean onShowLeaderboardsRequested() {
        if (isSignedIn()) {        	
            startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(getApiClient()),
                    Const.RC_UNUSED);
            return true;
        } else {
            showAlert(getString(R.string.leaderboards_not_available));

			StateMachine.getIns().pushState(BaseState.statesIDs.LEADER);
        }
        return false;
    }
    
    /** Once the game is over saved all the info locally and try
     * to push it to Google Games Services*/
    public void onGameOver(boolean win, int moves, int gameMode, int boardSize) {
    	ProgNPrefs.getIns().updateGameFinished(boardSize, gameMode, win);
		
    	//Each 2 games, show Interstitial.
		if(ProgNPrefs.getIns().getGamesFinished(Const.TOTAL_SIZES)%2 == 0)
			GameActivity.instance.displayInterstitial();
    	
		
		// check for achievements
		// checkForAchievements(requestedScore, finalScore);

        // update leaderboards
        //updateLeaderboards(finalScore);

        // push those accomplishments to the cloud, if signed in
        //pushAccomplishments();
    }
    
    /* Check for achievements and unlock the appropriate ones.
    *
    * @param requestedScore the score the user requested.
    * @param finalScore the score the user got.
    */
   void checkForAchievements(int requestedScore, int finalScore) {
	   /*
       // Check if each condition is met; if so, unlock the corresponding
       // achievement.
       if (isPrime(finalScore)) {
           mOutbox.mPrimeAchievement = true;
           achievementToast(getString(R.string.achievement_prime_toast_text));
       }
       if (requestedScore == 9999) {
           mOutbox.mArrogantAchievement = true;
           achievementToast(getString(R.string.achievement_arrogant_toast_text));
       }
       if (requestedScore == 0) {
           mOutbox.mHumbleAchievement = true;
           achievementToast(getString(R.string.achievement_humble_toast_text));
       }
       if (finalScore == 1337) {
           mOutbox.mLeetAchievement = true;
           achievementToast(getString(R.string.achievement_leet_toast_text));
       }
       mOutbox.mBoredSteps++;
       */
   }

   void unlockAchievement(int achievementId, String fallbackString) {
       if (isSignedIn()) {
           Games.Achievements.unlock(getApiClient(), getString(achievementId));
       } else {
    	   achievementToast(fallbackString);
       }
   }

   /** Display an achievement toast, only if not signed in. If signed in,
    * the standard google games toast will appear, so we don't show our own.*/
   void achievementToast(String achievement) {
       if (!isSignedIn()) {
           Toast.makeText(this, getString(R.string.achievement_unlocked)+
        		   achievement,Toast.LENGTH_LONG).show();
       }
   }

   void pushAccomplishments() {
       /*
	   if (!isSignedIn()) {
           // can't push to the cloud, so save locally
           mOutbox.saveLocal(this);
           return;
       }
       if (mOutbox.mPrimeAchievement) {
           Games.Achievements.unlock(getApiClient(), getString(R.string.achievement_prime));
           mOutbox.mPrimeAchievement = false;
       }
       if (mOutbox.mArrogantAchievement) {
           Games.Achievements.unlock(getApiClient(), getString(R.string.achievement_dont_get_cocky_kid));
           mOutbox.mArrogantAchievement = false;
       }
       if (mOutbox.mHumbleAchievement) {
           Games.Achievements.unlock(getApiClient(), getString(R.string.achievement_humble));
           mOutbox.mHumbleAchievement = false;
       }
       if (mOutbox.mLeetAchievement) {
           Games.Achievements.unlock(getApiClient(), getString(R.string.achievement_omg_u_r_teh_uber_leet));
           mOutbox.mLeetAchievement = false;
       }
       if (mOutbox.mBoredSteps > 0) {
           Games.Achievements.increment(getApiClient(), getString(R.string.achievement_really_really_bored),
                   mOutbox.mBoredSteps);
           Games.Achievements.increment(getApiClient(), getString(R.string.achievement_bored),
                   mOutbox.mBoredSteps);

       }
       if (mOutbox.mEasyModeScore >= 0) {
           Games.Leaderboards.submitScore(getApiClient(), getString(R.string.leaderboard_easy),
                   mOutbox.mEasyModeScore);
           mOutbox.mEasyModeScore = -1;
       }
       if (mOutbox.mHardModeScore >= 0) {
           Games.Leaderboards.submitScore(getApiClient(), getString(R.string.leaderboard_hard),
                   mOutbox.mHardModeScore);
           mOutbox.mHardModeScore = -1;
       }
       mOutbox.saveLocal(this);*/
   }

   /**
    * Update leaderboards with the user's score.
    *
    * @param finalScore The score the user got.
    */
   void updateLeaderboards(int boardSize) {
       /*
	   if (mHardMode && mOutbox.mHardModeScore < finalScore) {
           mOutbox.mHardModeScore = finalScore;
       } else if (!mHardMode && mOutbox.mEasyModeScore < finalScore) {
           mOutbox.mEasyModeScore = finalScore;
       }*/
   }
   
   
   /** Start sign-in flow by user interaction*/
   public void onSignInButtonClicked() {
       beginUserInitiatedSignIn();
   }

   
   /** Start sign-out flow by user interaction*/
   public void onSignOutButtonClicked() {
       signOut();
   }
	
}
