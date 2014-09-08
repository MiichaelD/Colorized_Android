package com.webs.itmexicali.colorized;

import ProtectedInt.ProtectedInt;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.webs.itmexicali.colorized.GameView.surfaceListener;
import com.webs.itmexicali.colorized.gamestates.BaseState;
import com.webs.itmexicali.colorized.gamestates.GameState.GameFinishedListener;
import com.webs.itmexicali.colorized.gamestates.StateMachine;
import com.webs.itmexicali.colorized.util.Const;
import com.webs.itmexicali.colorized.util.GameStatsSync;
import com.webs.itmexicali.colorized.util.ProgNPrefs;

import com.xsqhbao.bipppts201390.AdListener.AdType;
//import com.xsqhbao.bipppts201390.MA; //AirPush_BNDL
import com.xsqhbao.bipppts201390.Prm;//AirPush_STD

public class GameActivity extends BaseGameActivity implements GameFinishedListener, surfaceListener{
	
	//AdMob Advertising
    /** The view to show the ad. */
    private AdView AdMobView = null;
    private InterstitialAd interstitial =  null;
    
    //AirPush Ads
    //Bundle
    //private MA air = null;
    private com.xsqhbao.bipppts201390.AdView AirPushView = null;
    
    private Prm air_std = null;
    
    
    // true if the game finished is the first one since the app being launched
    private boolean firstGameFinished = true;    
	
	/** This activity instance, to access its members from other classes*/
    public static GameActivity instance;
    
    /**Media player to play sounds of user interactions & background music.*/
    private MediaPlayer soundPlayer = null, musicPlayer = null;
    //private SoundType previousSound = SoundType.NONE;
    public static enum SoundType{ NONE, TOUCH, WIN, LOSE};
    
    //SignInButton buttonSI = null;
    
    
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
		
		//Setup ProtectedInt once
		if(!ProtectedInt.isSetup())
			ProtectedInt.setup();
		
		//Init Preferences once
		ProgNPrefs.initPreferences(this);
		
		
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
		
		initAds();  
	}
	
	public void onSurfaceChanged(float width, float height){
        //buttonSI.setPadding((int)(8*width/16), (int)(38*height/48), 0, 0);
	}
	
	
	/** Load ads if they are enabled, depending on the ads service using load
	 * the corresponding one AirPush or AdMob*/
	private void initAds(){
		if(!Const.SHOW_ADS)
			return;
		// Add the AdView to the view hierarchy. The view will have no size
        // until the ad is loaded.
		LinearLayout layout = (LinearLayout) findViewById(R.id.LayMain);
		switch(Const.AD_SERVICE){
		case Const.ADS_ADMOB:
			/*************************************	ADS ADMOB	*********************************************/
			if(AdMobView != null){// remove the existing adView
	        	layout.removeView(AdMobView); 
	        	AdMobView.destroy();
	        	AdMobView = null;
	        }
			// Create an ad.
	        AdMobView = new AdView(this);
	        AdMobView.setAdSize(AdSize.SMART_BANNER);
	        AdMobView.setAdUnitId(Const.ADVIEW_AD_UNIT_ID);
	        AdMobView.setVisibility(View.GONE);
	        AdMobView.setAdListener(new AdListener() {
	        	@Override
	    		public void onAdOpened() {// Save app state before going to the ad overlay.
	    			Const.d(Const.TAG,"AdView - Opened");
	    			AdMobView.setVisibility(View.GONE);
	    		}
	    		@Override
	    		public void onAdFailedToLoad(int errorCode){
	    			Const.d(Const.TAG,"AdView - FailedToLoad = "+errorCode);
	    			AdMobView.setVisibility(View.GONE);
	    		}
	    		@Override
	    		public void onAdLoaded(){
	    			AdMobView.setVisibility(View.VISIBLE);
	    		}
	    	});
	        
	        
	        /* When the game_Screen.xml root tag used to be RelativeLayout, the ad was just
	         OVER the GameView, now they share the screen size
	        //layout = (LinearLayout) findViewById(R.id.LayMain);
	        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
	        		RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	    	params.addRule(RelativeLayout.CENTER_HORIZONTAL);
	    	layout.addView(adView, params);	    	*/
			layout.addView(AdMobView);
			// Start loading the ad in the background.
	        AdMobView.loadAd(createAdRequest());
	        
	        // Create the INTERSTTIAL.
	        interstitial = new InterstitialAd(this);
	        interstitial.setAdUnitId(Const.INTERSTITIAL_AD_UNIT_ID);
	        interstitial.setAdListener(new AdListener() {
	            @Override
	            public void onAdClosed() {
	              interstitial.loadAd(createAdRequest());
	            }
	        });
	        // Begin loading your interstitial.
	        interstitial.loadAd(createAdRequest());
	        /*************************************	ADS ADMOB	*********************************************/
			
			break;
		case Const.ADS_AIRPUSH_BUNDLE:
			/*************************************	ADS AIRPUSH	*********************************************/
			if(AirPushView != null){// remove the existing adView
	        	layout.removeView(AirPushView);
	        	AirPushView = null;
	        }
			
			AirPushView = new com.xsqhbao.bipppts201390.AdView(
					this, com.xsqhbao.bipppts201390.AdView.BANNER_TYPE_IN_APP_AD,
					com.xsqhbao.bipppts201390.AdView.PLACEMENT_TYPE_INLINE, false, false, 
					com.xsqhbao.bipppts201390.AdView.ANIMATION_TYPE_FADE);
			
			layout.addView(AirPushView);
			
			/**Initializing Bundle SDK 
			 * @param Activity * @param AdListener * @param caching*/
			/*
			air = new MA(this, 
			
					new com.xsqhbao.bipppts201390.AdListener(){
				@Override
				public void noAdAvailableListener() {
					Const.e(instance.getLocalClassName(), "noAdAvailableListener");	
				}
				@Override
				public void onAdCached(AdType arg0) {
					Const.d(instance.getLocalClassName(), "onAdCached type: "+arg0.name());
				}
				@Override
				public void onAdError(String arg0) {
					Const.e(instance.getLocalClassName(), "onAdError: "+arg0);	
				}
				@Override
				public void onSDKIntegrationError(String arg0) {
					Const.e(instance.getLocalClassName(), "onSDKIntegrationError: "+arg0);	
				}
				@Override
				public void onSmartWallAdClosed() {
					Const.d(instance.getLocalClassName(), "onSmartWallAdClosed");
					//Caching Smartwall Ad for future use
					air.callSmartWallAd();
				}
				@Override
				public void onSmartWallAdShowing() {
					Const.d(instance.getLocalClassName(), "onSmartWallAdShowing");				
				}
			},
			true);
			
			//Caching Smartwall Ad. 
			//AirPush Bundle//air.callSmartWallAd();
			
			 */
			
			break;
		case Const.ADS_AIRPUSH_STANDARD:
			if(AirPushView != null){// remove the existing adView
	        	layout.removeView(AirPushView);
	        	AirPushView = null;
	        }
			
			AirPushView = new com.xsqhbao.bipppts201390.AdView(
					this, com.xsqhbao.bipppts201390.AdView.BANNER_TYPE_IN_APP_AD,
					com.xsqhbao.bipppts201390.AdView.PLACEMENT_TYPE_INLINE, false, false, 
					com.xsqhbao.bipppts201390.AdView.ANIMATION_TYPE_FADE);
			
			layout.addView(AirPushView);
			
			air_std = new Prm(this,
					new com.xsqhbao.bipppts201390.AdListener(){
				@Override
				public void noAdAvailableListener() {
					Const.e(instance.getLocalClassName(), "noAdAvailableListener");	
				}
				@Override
				public void onAdCached(AdType arg0) {
					Const.d(instance.getLocalClassName(), "onAdCached type: "+arg0.name());
				}
				@Override
				public void onAdError(String arg0) {
					Const.e(instance.getLocalClassName(), "onAdError: "+arg0);	
				}
				@Override
				public void onSDKIntegrationError(String arg0) {
					Const.e(instance.getLocalClassName(), "onSDKIntegrationError: "+arg0);	
				}
				@Override
				public void onSmartWallAdClosed() {
					Const.d(instance.getLocalClassName(), "onSmartWallAdClosed");
					//Caching Smartwall Ad for future use
					air_std.runSmartWallAd();
				}
				@Override
				public void onSmartWallAdShowing() {
					Const.d(instance.getLocalClassName(), "onSmartWallAdShowing");				
				}
			},
			true);
			air_std.runSmartWallAd();
			break;
		/*************************************	ADS AIRPUSH	*********************************************/
		}
		
	}
	
	/*************************************	ADS ADMOB	*********************************************/
	/** Create an ad request. Check logcat output for the hashed device ID to
     *	get test ads on a physical device.*/
	private AdRequest createAdRequest(){
		return new AdRequest.Builder()
	            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	            //.addTestDevice("584586A082596B5844C4E301E1285E95") //My Nexus 4
	            .build();
	}
	
	/*************************************	ADS ADMOB / AIRPUSH	*********************************************/
	

	/** Load Interstitial ads, some ads-services like airPush
	 * needs to call SmartWallAds before showing them*/
	public void loadInterstitial(){
		Const.d(this.getLocalClassName(),"Loading interstitial");
		try{
			if(Const.SHOW_ADS){
				switch(Const.AD_SERVICE){
				case Const.ADS_ADMOB:
					if(!interstitial.isLoaded())
						interstitial.loadAd(createAdRequest());
					break;
				case Const.ADS_AIRPUSH_BUNDLE:
					//AirPush Bundle//air.callSmartWallAd();
					break;
				case Const.ADS_AIRPUSH_STANDARD:
					air_std.runSmartWallAd();
					break;
				}
			}
		}catch(Exception e){}
	}
	
	

	// Invoke displayInterstitial() when you are ready to display an interstitial.
	public void displayInterstitial() {
		if(!Const.SHOW_ADS)
			return;
		Const.i(this.getLocalClassName(),"ShowingInterstitials ");
		
		switch(Const.AD_SERVICE){
		case Const.ADS_ADMOB:
			if (interstitial.isLoaded()) {
				interstitial.show();
			}
			else
				Const.w(this.getLocalClassName(),"Interstitial not loaded");
			break;
		case Const.ADS_AIRPUSH_BUNDLE:
			try {
				//if(air.isSDKEnabled(this))
				//AirPush Bundle//air.showCachedAd(this, AdType.smartwall);
			} catch (Exception e) {
				Const.w(this.getLocalClassName(),"Interstitial not showing Cached");
				//AirPush Bundle//air.callSmartWallAd();
				e.printStackTrace();
			}
			break;
		case Const.ADS_AIRPUSH_STANDARD:
			try {
				air_std.runCachedAd(this, AdType.smartwall);
			} catch (Exception e) {
				Const.w(this.getLocalClassName(),"Interstitial not showing Cached");
				air_std.runSmartWallAd();
				e.printStackTrace();
			}
			break;
		}
	}
	  
	/*******************************	ADS ADMOB / AIRPUSH END **************************************/
	
	
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
		
		//keep sticky full immersive screen in case we lost it
		Const.setFullScreen(this);
		
		//ADMOB Advertising
		if (AdMobView != null) {
  	      AdMobView.resume();
  		}
		//start Music playing
		playMusic(true);
	}
	
	
	
	@Override
	public void onPause(){
		super.onPause();
		Const.v(GameActivity.class.getSimpleName(),"onPause()");
		
		//ADMOB Advertising
		if (AdMobView != null) {
			AdMobView.pause();
  		}
		
		playMusic(false);		
	}
	
	@Override
	public void onStop(){
		super.onStop();
		//release resources of the media player and delete it
		Const.v(GameActivity.class.getSimpleName(),"onStop()");
		
		stopSound();
		stopMusicPlayer();
	}
	
	public void onDestroy(){
		super.onDestroy();
		//release resources of the media player and delete it
		Const.v(GameActivity.class.getSimpleName(),"onDestroy()");
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
	
	/*------------------------ LEADERBOARDS and ACHIEVEMENTS METHODS ----------------------------------*/
	

	@Override
	public void onSignInFailed() {
		// Show sign-in button on main menu
		
		//UPDATE CURRENT STATE
		if(StateMachine.getIns().getCurrentState() != null)
			StateMachine.getIns().getCurrentState().onFocus();
	}

	@Override
    public void onSignInSucceeded() {
        // Show sign-out button on main menu

        // Show "you are signed in" message on win screen, with no sign in button.

        // Set the greeting appropriately on main menu
        
		// Sync data between local  and google games services saves
		GameStatsSync.syncLeaderboardsScores(getApiClient(), true);
		GameStatsSync.syncAchievements(getApiClient());
        
        
        //UPDATE CURRENT STATE//UPDATE CURRENT STATE
		if(StateMachine.getIns().getCurrentState() != null)
			StateMachine.getIns().getCurrentState().onFocus();
    }

	/** If signed in, return the player name, else return null*/
	public String getPlayerName(){
		final int maxNumOfNames = 3;
		String displayName = null;
		String returnStr = null;
		if(isSignedIn()){		
			Player p = Games.Players.getCurrentPlayer(getApiClient());
	        
	        if (p == null) {
	        	returnStr = displayName = "Unknown User";
	        } else {
	            displayName = p.getDisplayName();
	            returnStr = displayName;
	            try{
		            int counter = 0, firstSpace = -1;
		            do{
		            	firstSpace = displayName.indexOf(' ', firstSpace+1);
		            }while(firstSpace != -1 && ++counter < maxNumOfNames);
	            	if(firstSpace != -1)
	            		returnStr = displayName.substring(0,firstSpace);
	            }catch(Exception e){Const.w(GameActivity.class.getSimpleName(),
	            		e.getMessage());}
	        }
	        Const.d(GameActivity.class.getSimpleName(),"User: "+displayName);
		}
        return returnStr;
	}
	
	
	/** Try to show Achievements activity, if not signed in, show message
	 * @return true if Achievements were shown*/
	public boolean onShowAchievementsRequested() {
        if (isSignedIn()) {        	
            startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()),
                    Const.RC_UNUSED);
            return true;
        } else {
        	if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext())
        			== ConnectionResult.SUCCESS)
                showAlert(getString(R.string.achievements_not_available));        		
        	else
        		StateMachine.getIns().pushState(BaseState.statesIDs.STATS);
        	
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
            
        	if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext())
        			== ConnectionResult.SUCCESS)
            	showAlert(getString(R.string.leaderboards_not_available));        		
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
		if(ProgNPrefs.getIns().getGamesFinished(Const.TOTAL_SIZES)%2 == 0)
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
       beginUserInitiatedSignIn();
   }

   
   /** Start sign-out flow by user interaction*/
   public void onSignOutButtonClicked() {
       signOut();
   }
	
}
