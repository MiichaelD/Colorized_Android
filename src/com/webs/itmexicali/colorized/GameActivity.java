package com.webs.itmexicali.colorized;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.webs.itmexicali.colorized.gamestates.StateMachine;
import com.webs.itmexicali.colorized.util.Const;

import ProtectedInt.ProtectedInt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class GameActivity extends Activity {
	
	//AdMob Advertising
    /** The view to show the ad. */
    private AdView adView = null;
     
    private InterstitialAd interstitial =  null;
	
	//this activity instance, to access its members from other classes
    public static GameActivity instance;
    
    // Media player to play sounds of user interactions & background music.
    private MediaPlayer soundPlayer = null, musicPlayer = null;
    //private SoundType previousSound = SoundType.NONE;
    public static enum SoundType{ NONE, TOUCH, WIN, LOSE};
    
	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(GameActivity.class.getSimpleName(),"onCreate()");
		instance = this;
		
		//run on FullScreen with no Action and Navigation Bars
		Const.setFullScreen(this);
		
		//Setup ProtectedInt once
		if(!ProtectedInt.isSetup())
			ProtectedInt.setup();
		
		//Init Preferences once 
		Prefs.initPreferences(this);
		
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
		if(Prefs.getIns().playSFX()){
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
		if (play && Prefs.getIns().playMusic()){
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

}
