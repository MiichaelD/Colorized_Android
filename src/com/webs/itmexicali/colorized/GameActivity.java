package com.webs.itmexicali.colorized;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.webs.itmexicali.colorized.GameView.SurfaceListener;
import com.webs.itmexicali.colorized.ads.Advertising;
import com.webs.itmexicali.colorized.gamestates.BaseState;
import com.webs.itmexicali.colorized.gamestates.GameState;
import com.webs.itmexicali.colorized.gamestates.GameState.GameFinishedListener;
import com.webs.itmexicali.colorized.gamestates.StateMachine;
import com.webs.itmexicali.colorized.util.Const;
import com.webs.itmexicali.colorized.util.Notifier;
import com.webs.itmexicali.colorized.util.ProgNPrefs;
import com.webs.itmexicali.colorized.util.PushNotificationHelper;
import com.webs.itmexicali.colorized.util.Tracking;

public class GameActivity extends BaseGameActivity implements GameFinishedListener, SurfaceListener{
	
	private Advertising pAds = null;    
	
	//facebook sharing helper
	private UiLifecycleHelper uiHelper;
    
    // true if the game finished is the first one since the app being launched
    private boolean firstGameFinished = true;    
	
	/** This activity instance, to access its members from other classes*/
    public static GameActivity instance; //TODO: make it private and refactor everywhere using direct access to it
    
    public static GameActivity getActivity(){
    	return instance;
    }
    
    /**Media player to play sounds of user interactions & background music.*/
    private MediaPlayer soundPlayer = null, musicPlayer = null;
    //private SoundType previousSound = SoundType.NONE;
    public static enum SoundType{ NONE, TOUCH, WIN, LOSE};
    
    private String mPlayerName = null;
    
    private boolean p_suscribeForPush = false;

    
	@SuppressLint({ "InlinedApi", "NewApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Const.v(GameActivity.class.getSimpleName(),"onCreate()");
		enableDebugLog(Const.D);
		instance = this;
		
		//run on FullScreen with no Action and Navigation Bars
		Const.setFullScreen(this);
		
		//Keep screen on
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
	    //keep current version on tracking info
        Tracking.shared().updateVersion(Const.getVersionName(), Const.getVersionCode());
                
		// INCREMENT THE APP OPENED COUNTER if onCreate has no savedState ONLY
		// AFTER FINISHING THE FIRST GAME since app launch
		if(savedInstanceState == null){
			firstGameFinished = true;
		}
		
		//Init StateMachine
		if(!StateMachine.isSetUp())
			StateMachine.setup(this);

		//set the game screen
		setContentView(R.layout.game_screen);
		
		//set surface listener, to reposition Sign-In Button
		((GameView)findViewById(R.id.gameView)).setSurfaceListener(this);
		
		 uiHelper = new UiLifecycleHelper(this, null);
		 uiHelper.onCreate(savedInstanceState);
		
		initAds();
		
		Notifier.getInstance(this).clearAll();
	}
	
	public void onSurfaceChanged(float width, float height){
        //buttonSI.setPadding((int)(8*width/16), (int)(38*height/48), 0, 0);
		if(getIntent().getExtras() != null && getIntent().getExtras().getBoolean(Notifier.DIRECT_TO_GAME)){
			Const.i("GameActivity", Notifier.DIRECT_TO_GAME + " from getIntent().getExtras()");
			GameState gameState = (GameState) BaseState.stateFactory(BaseState.statesIDs.GAME);
			gameState.setAskToPlaySavedGame(false);
			StateMachine.getIns().pushState(gameState);
			getIntent().getExtras().remove(Notifier.DIRECT_TO_GAME);
		}
	}
	
	
	/** Load ads if they are enabled, depending on the ads service using load
	 * the corresponding one AirPush or AdMob*/
	private void initAds(){
		
		/* Now the ads object is just created once, not destroying it and loading it if 
		 * it already exists, to change dad, remove the pAds == null validation from next
		 * if clause*/		
		if(Advertising.SHOW_ADS && pAds == null){
			LinearLayout layout = (LinearLayout) findViewById(R.id.LayMain);
			
			/* If we want to create a new banner, destroy and remove current if it exists*/
			if (pAds != null && pAds.getBanner() != null){
				layout.removeView(pAds.getBanner());
				pAds.destroyBanner();
			}
			
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			params.gravity = android.view.Gravity.CENTER_HORIZONTAL;
			
			pAds = Advertising.instantiate(this);
			pAds.getBanner().setLayoutParams(params);
			
			/* When the game_Screen.xml root tag used to be RelativeLayout, the ad was just
	         OVER the GameView, now the ad shares the screen size with GameView
	        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
	        		RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	    	params.addRule(RelativeLayout.CENTER_HORIZONTAL);
	    	layout.addView(adView, params);	    	*/
			
			// Add the AdView to the view hierarchy. The view will have no size
	        // until the ad is loaded.
			layout.addView(pAds.getBanner());
		}
		
	}
	
	public View getBannerView(){
		return pAds.getBanner();
	}
	
	

	/** Load Interstitial ads, some ads-services like airPush
	 * needs to call SmartWallAds before showing them*/
	public void loadInterstitial(){
		Const.d(this.getLocalClassName(),"Loading interstitial");
		if(pAds != null)
			pAds.loadInterstitial();
	}
	
	

	/** Invoke displayInterstitial() when you are ready to display an interstitial.*/
	public void displayInterstitial() {
		Const.d(this.getLocalClassName(),"Displaying interstitial");
		if(pAds != null)
			pAds.showInterstitial();
	}
	
	
	@Override
	public void onStart(){
		super.onStart();
		Const.v(GameActivity.class.getSimpleName(),"onStart()");
		
		startMusicPlayer();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		Const.v(GameActivity.class.getSimpleName(),"onResume()");
		updateCurrentState(); // in case we sign-out from GPS Achievements/Leaderboards UI
		
		//keep sticky full immersive screen in case we lost it
		Const.setFullScreen(this);
		
		if(pAds != null)
			pAds.onResume();
		
		uiHelper.onResume();
		
		//start Music playing
		playMusic(true);
		
		Tracking.shared().onResume(this);
	}
	
	
	
	@Override
	public void onPause(){
		super.onPause();
		Const.v(GameActivity.class.getSimpleName(),"onPause()");
		
		if(pAds != null)
			pAds.onPause();
		
		uiHelper.onPause();
		
		playMusic(false);		
		
		// uploads the file containing the logged events. The onPause method is
        // guaranteed to be called in the life cycle of an Android App, so we
        // are guaranteed the events log file are uploaded
		Tracking.shared().onPause();
	}
	
	@Override
	public void onStop(){
		//release resources of the media player and delete it
		Const.v(GameActivity.class.getSimpleName(),"onStop()");

		uiHelper.onStop();
		
		stopSound();
		stopMusicPlayer();
		
		super.onStop();
	}
	
	@Override
	public void onDestroy(){
		Const.v(GameActivity.class.getSimpleName(),"onDestroy()");
		
		uiHelper.onDestroy();
		super.onDestroy();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
		Const.v(GameActivity.class.getSimpleName(),"onSaveInstanceState()");
	    uiHelper.onSaveInstanceState(outState);
	}
	
	
	private void scheduleReminderNotifForSavedGame(){
		if(ProgNPrefs.getIns().isGameSaved()){
	   		   Notifier notifier = Notifier.getInstance(GameActivity.instance);
	   		   notifier.clearAll();
	   		   String title = getResources().getString(R.string.notif_title_need_you);
	   		   String msg = getResources().getString(R.string.notif_msg_need_you);
	   		   int oneDayInMinutes = (60 * 24 - 15);
	   		   notifier.schedule(title, msg, SplashScreen.class, oneDayInMinutes, true);
 	   }
	}
	
	/** Display an exit confirmation dialog to prevent accidentally quitting the game*/
	public void showExitDialog(){
		//Use the Builder class for convenient dialog construction
		new AlertDialog.Builder(GameActivity.this)
		.setMessage(R.string.exit_confirmation)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) { // Exit the game
        	   scheduleReminderNotifForSavedGame();
        	   GameActivity.instance = null;
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
	
				stopSound();
				
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
	
	/** If there is a sound playing, stop and release it so we can restart it later*/
	public void stopSound(){
		try{
			if(soundPlayer != null){
				soundPlayer.stop();
				soundPlayer.release();
				soundPlayer = null;
			}	
		}catch(IllegalStateException ise){}
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
	
	private void stopMusicPlayer(){
		try{
			if( musicPlayer != null){
				musicPlayer.stop();
				musicPlayer.release();
				musicPlayer = null;
			}
		}catch(IllegalStateException ise){}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		
		//facebook sharing
		uiHelper.onActivityResult(requestCode, resultCode, intent, new FacebookDialog.Callback() {
	        @Override
	        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
	            Const.e(GameActivity.class.getSimpleName(), String.format("Error: %s", error.toString()));
				GameStatsSync.revealAchievement(getApiClient(), R.string.achievement_sharing_is_good);
	        }

	        @Override
	        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
	        	GameStatsSync.revealAchievement(getApiClient(), R.string.achievement_sharing_is_good);
	        	// To get the correct sharing result, I should implement FB Sign-in within app
	        	boolean didCancel = FacebookDialog.getNativeDialogDidComplete(data);
	        	String completionGesture = FacebookDialog.getNativeDialogCompletionGesture(data);
	        	String postId = FacebookDialog.getNativeDialogPostId(data);
				Const.d(GameActivity.class.getSimpleName(),"onFbShare didCancel="+didCancel+
						",completionGesture="+completionGesture+", postID="+postId);
	        }
	    });
		
	
		if(requestCode == Const.RC_SHARE) {
			if(resultCode == RESULT_OK) {
				GameStatsSync.unlockAchievement(getApiClient(), R.string.achievement_sharing_is_good);
				Const.d(GameActivity.class.getSimpleName(),"onGoogleShare Successful");
			}
			else
				GameStatsSync.revealAchievement(getApiClient(), R.string.achievement_sharing_is_good);
		}
	}
	
	/*------------------------ LEADERBOARDS and ACHIEVEMENTS METHODS ----------------------------------*/
	

	@Override
	public void onSignInFailed() {
		// Show sign-in button on main menu
		updateCurrentState();
		
		mPlayerName = null;
	}

	@Override
    public void onSignInSucceeded() {
		Player player = Games.Players.getCurrentPlayer(getApiClient());
		
        // Show sign-out button on main menu

        // Show "you are signed in" message on win screen, with no sign in button.

        // Set the greeting appropriately on main menu
        
		// Sync data between local  and google games services saves
		GameStatsSync.syncLeaderboardsScores(getApiClient(), true);
		GameStatsSync.syncAchievements(getApiClient());
        
		updateCurrentState();
		
		// We also want to keep track of how many times the user has signed in

		
		if (player!=null){
	    	Tracking.shared().onPlayerIdUpdated(player.getPlayerId());
		
			if (p_suscribeForPush == false){
				PushNotificationHelper push = new PushNotificationHelper();
					push.subscribeToPush(player.getPlayerId());
					p_suscribeForPush= true;
			}
		}
		
		if(mActivityToShow == ACHIEVEMENTS_AFTER_SIGNIN){
			onShowAchievementsRequested();
		} else if ( mActivityToShow == LEADERBOARDS_AFTER_SIGNIN){
			onShowLeaderboardsRequested();
		}
    }

	/** If signed in, return the player name, else return null*/
	public String getPlayerName(){
		if (mPlayerName == null){
			final int maxNumOfNames = 3;
			if(isSignedIn()){		
				Player p = Games.Players.getCurrentPlayer(getApiClient());
		        if (p != null) {
		        	mPlayerName = p.getDisplayName();
		        	Tracking.shared().setPlayerProperty("$full_name", mPlayerName);
		        	
		        	//show only 3 names
		            String[] names = mPlayerName.split(" ");
		            if (names.length > maxNumOfNames){
		            	StringBuilder sb = new StringBuilder();
		            	for (int i = 0 ; i < maxNumOfNames ; ++i){
		            		sb.append(names[i]);
		            	}
		            	mPlayerName = sb.toString();
		            }
		        }
		        Const.d(GameActivity.class.getSimpleName(),"User: "+mPlayerName);
			}
		}
        return mPlayerName;
	}
	
	/** Try to launch the Google+ share dialog*/
   @SuppressLint("InlinedApi")
public boolean onGoogleShareRequested(String text){
	   Const.d(GameActivity.class.getSimpleName(),"Launch the Google+ share.");
	   if (isSignedIn()) {        	
		   Intent shareIntent = new com.google.android.gms.plus.PlusShare.Builder(this)
	          .setType("text/plain")
	          .setText(text)
	          .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.webs.itmexicali.colorized"))
	          .getIntent();

		   //with this flag, we ensure that when we open again our app, it doesn't show the sharing intent screen
		   shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
	      startActivityForResult(shareIntent, Const.RC_SHARE);
           return true;
       } else {
	       showAlert(getString(R.string.share_not_available));
       }
	   return false;
   }
	
   /** Try to launch the Facebook share dialog*/
   public boolean onFbShareRequested(String text, String pictureURL){
	   if(pictureURL == null){
		   pictureURL = "https://fbcdn-sphotos-c-a.akamaihd.net/hphotos-ak-xpa1/v/t1.0-9/10603423_531397536990342_6991488415590407112_n.png?oh=b38087f27ad60743ff23cacaeb17d2db&oe=54CBB5A1&__gda__=1419341337_e67331aab67f68c2f25f631bd9b6b5fe";
	   }
	   
	   if (FacebookDialog.canPresentShareDialog(getApplicationContext(), 
               FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
		   // Publish the post using the Share Dialog
		   FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
		   	.setApplicationName("Color Flooded")
		   //.setApplicationName("Invasión de Color")
	   		.setName(getString(R.string.app_name))
	   		.setCaption(text)
	   		.setLink(getString(R.string.app_url))
	   		.setPicture(pictureURL)
	   		.build();
		   uiHelper.trackPendingDialogCall(shareDialog.present());
		   return true;
		} else {
			// Fallback. For example, publish the post using the Feed Dialog
			Bundle params = new Bundle();
		    params.putString("name", getString(R.string.app_name));
		    params.putString("caption", text);
		    params.putString("link", getString(R.string.app_url));
		    params.putString("picture", pictureURL);

		    WebDialog feedDialog = (
		        new WebDialog.FeedDialogBuilder(this,
		            Session.getActiveSession(),
		            params)).setOnCompleteListener(new OnCompleteListener() {

		            @Override
		            public void onComplete(Bundle values, FacebookException error) {
		                if (error == null) {
		                    // When the story is posted, echo the success
		                    // and the post Id.
		                    final String postId = values.getString("post_id");
		                    if (postId != null) {
		                    	//Posted correctly
		                    	Const.d(GameActivity.class.getSimpleName(),"onShare Successful thru webDialog");
		        				GameStatsSync.unlockAchievement(getApiClient(), R.string.achievement_sharing_is_good);
		                    } else {
		                        // User clicked the Cancel button
		                    	Const.d(GameActivity.class.getSimpleName(),"onShare Cancelled thru webDialog");
		        				GameStatsSync.revealAchievement(getApiClient(), R.string.achievement_sharing_is_good);
		                    }
		                } else if (error instanceof FacebookOperationCanceledException) {
		                    // User clicked the "x" button
	                    	Const.d(GameActivity.class.getSimpleName(),"onShare clicked X from webDialog");
		    				GameStatsSync.revealAchievement(getApiClient(), R.string.achievement_sharing_is_good);
		                } else {
		                    // Generic, ex: network error
	                    	Const.d(GameActivity.class.getSimpleName(),"onShare error on webDialog: "+error.getStackTrace());
		    				GameStatsSync.revealAchievement(getApiClient(), R.string.achievement_sharing_is_good);
		                }
		            }
		        })
		        .build();
		    feedDialog.show();
		}
	   return true;
   }
	
	/** Try to show Achievements activity, if not signed in, show message
	 * @return true if Achievements were shown*/
	public boolean onShowAchievementsRequested() {
        if (isSignedIn()) {
        	startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()), Const.RC_UNUSED);
        	return true;
        } else {
        	if(isGAPPSavailable())
//                showAlert(getString(R.string.achievements_not_available));
        		signInAndShow(ACHIEVEMENTS_AFTER_SIGNIN);
        	else
        		StateMachine.getIns().pushState(BaseState.statesIDs.STATS);
        }
        return false;
    }

	/** Try to show Leaderboards activity, if not signed in, show message
	 * @return true if Leaderboards were shown*/
    public boolean onShowLeaderboardsRequested() {
    	if (isSignedIn()) {
        	startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(getApiClient()), Const.RC_UNUSED);
            return true;
        } else {
        	if(isGAPPSavailable())
//            	showAlert(getString(R.string.leaderboards_not_available));
        		signInAndShow(LEADERBOARDS_AFTER_SIGNIN);
        	else
        		StateMachine.getIns().pushState(BaseState.statesIDs.STATS);
        }
        return false;
    }
    
    /** Once the tutorial is fully completed, push a new achievement to
     * Google Games Services*/
    public void onTutorialFinished(){
    	//Achievement Finish the tutorial
    	GameStatsSync.unlockAchievement(getApiClient(),R.string.achievement_now_i_get_it);
    }
    
    /** Once the game is over saved all the info locally and try
     * to push it to Google Games Services*/
    public void onGameOver(boolean win, int moves, int gameMode, int boardSize) {
    	if(firstGameFinished){
    		firstGameFinished = false;
			ProgNPrefs.getIns().incrementAppOpened();// LOCALLY
    	}
    	
    	ProgNPrefs.getIns().updateGameFinished(boardSize, gameMode, win);
    	
    	//Each 2 games, show Interstitial.
		if( (ProgNPrefs.getIns().getGamesFinished(Const.TOTAL_SIZES) 
				% Advertising.GAMEOVERS_TO_INTERSTITIAL) == 0)
			GameActivity.instance.displayInterstitial();
		else
			GameActivity.instance.loadInterstitial();
    	
		//update achievs and leads.
		GameStatsSync.updateAchievementsAndLeaderboards(getApiClient(),win,moves,gameMode,boardSize);
    }
   
   /** Start sign-in flow by user interaction*/
   public void onSignInButtonClicked() {
	   if(isSignedIn()||getApiClient().isConnecting()||getApiClient().isConnected())
		   return;
	   Const.d(GameActivity.class.getSimpleName(),"onSignInButtonClicked()");
	   mPlayerName = null;
       beginUserInitiatedSignIn();
   }

   
   /** Start sign-out flow by user interaction*/
   public void onSignOutButtonClicked() {
       signOut();
       mPlayerName = null;
   }
	
   /** Update currently active state in the statestack*/
   private void updateCurrentState(){
	 if(StateMachine.getIns().getCurrentState() != null)
		 StateMachine.getIns().getCurrentState().onFocus();
   }
}
