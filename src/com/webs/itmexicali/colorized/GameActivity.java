package com.webs.itmexicali.colorized;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

public class GameActivity extends Activity {
	
	//AdMob Advertising
    /** The view to show the ad. */
    private AdView adView = null;
	
	//this activity instance, to access its members from other classes
    public static GameActivity instance;
    
    // Media player to play sounds of user interactions & background music.
    private MediaPlayer soundPlayer = null, musicPlayer = null;
    private SoundType previousSound = SoundType.NONE;
    public static enum SoundType{ NONE, TOUCH, WIN, LOSE};

	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(GameActivity.class.getSimpleName(),"onCreate()");
		instance = this;
		
		//set the game screen
		setContentView(R.layout.game_screen);
	
		//run on FullScreen with no Action and Navigation Bars
		Const.setFullScreen(this);
		
		//check if there is a saved game and ask the user if he'd like to keep
		//playing it
		if (getSharedPreferences(Const.TAG, 0).getBoolean(getString(R.string.key_game_saved), false))
			showSavedGameDialog();
		
		/*************************************	ADS ADMOB	*********************************************/
		// Create an ad.
        adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(Const.AD_UNIT_ID);
        adView.setAdListener(new AdListener() {
        	  @Override
        	  public void onAdOpened() {
        	    // Save app state before going to the ad overlay.
        		Log.d(Const.TAG,"AdView - Opened");
        	  }
        	  @Override
        	  public void onAdFailedToLoad(int errorCode){
        		Log.d(Const.TAG,"AdView - FailedToLoad = "+errorCode);
        	  }
        	});
		
        
        // Add the AdView to the view hierarchy. The view will have no size
        // until the ad is loaded.
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.LayMain);
        if(adView != null){
        	layout.removeView(adView); // remove the existing adView
        	adView.destroy();
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
    	params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		layout.addView(adView, params);
		
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device.
        AdRequest adRequest = new AdRequest.Builder()
            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
            .addTestDevice("584586A082596B5844C4E301E1285E95") //My Nexus 4
            .build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);
        /*************************************	ADS ADMOB	*********************************************/
	}
	
	@Override
	public void onStart(){
		super.onStart();
		Log.v(GameActivity.class.getSimpleName(),"onStart()");
		//start Player with a resource
		if( musicPlayer == null){
			musicPlayer=MediaPlayer.create(this, R.raw.music);
			musicPlayer.setLooping(true);
		}
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
	
	/** If there is any progress in the game, save it in case the user
	 * wants to continue the next time he gets back to the game*/
	private void saveProgress(){
		if(GameView.getIns().getMoves() > 0){
			// the game was started, lets save it 
			SharedPreferences settings= getSharedPreferences(Const.TAG, 0); 
		    SharedPreferences.Editor settingsEditor = settings.edit();
		    
		    settingsEditor.putBoolean(getString(R.string.key_game_saved),true);
		    settingsEditor.putString(getString(R.string.key_board_saved),GameView.getIns().getBoardAsString());
		    settingsEditor.commit();
		}
	}
	
	/** Display an exit confirmation dialog to prevent accidentally quitting the game*/
	private void showExitDialog(){
		//Use the Builder class for convenient dialog construction
		new AlertDialog.Builder(GameActivity.this)
		.setMessage(R.string.exit_confirmation)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) { // Exit the game
        	   saveProgress();
               GameActivity.this.finish();
           }
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {// dismiss menu
               dialog.cancel();
               Const.setFullScreen(GameActivity.this);
           }
		})
		.create()
		.show();
	}
	
	/** Display dialog informing that there is a gamestate saved
	 * and ask if the user wants to play it or prefers a new match*/
	private void showSavedGameDialog(){
		//remove the saved game from the sharedPreferences file
	    SharedPreferences.Editor settingsEditor = getSharedPreferences(Const.TAG, 0).edit();
	    settingsEditor.remove(getString(R.string.key_game_saved));
	    settingsEditor.remove(getString(R.string.key_board_saved));
	    settingsEditor.commit();
	    
		//Use the Builder class for convenient dialog construction
		new AlertDialog.Builder(GameActivity.this)
		.setMessage(R.string.saved_game_confirmation)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) { // Exit the game
        	   dialog.cancel();
        	   Const.setFullScreen(GameActivity.this);
           }
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {// dismiss menu
        	   dialog.cancel();
        	   GameView.getIns().createNewBoard(getSharedPreferences(Const.TAG, 0).
        			   getInt(getString(R.string.key_board_size), 12));
        	   
        	   Const.setFullScreen(GameActivity.this);
           }
		})
		.create()
		.show();
	}
	
	/** Display dialog informing that there is a gamestate saved
	 * and ask if the user wants to play it or prefers a new match*/
	public void showRestartDialog(){
		//Use the Builder class for convenient dialog construction
		new AlertDialog.Builder(GameActivity.this)
		.setMessage(R.string.restart_game_confirmation)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
        	   GameView.getIns().createNewBoard(-1);
        	   Const.setFullScreen(GameActivity.this);   
           }
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {// dismiss menu
        	   Const.setFullScreen(GameActivity.this);
           }
		})
		.create()
		.show();
	}
	
	/** Display dialog informing that the game is over and restart again
	 * @param win if true, show congratulations text, else show condolences*/
	public void showRestartDialog(boolean win){	    
		playMusic(false);
		
		String str =win? 
				String.format(getString(R.string.game_over_win),GameView.getIns().getMoves()) :
				getString(R.string.game_over_lose);
		
		//Use the Builder class for convenient dialog construction
		new AlertDialog.Builder(GameActivity.this)
		.setMessage(str)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
        	   GameView.getIns().createNewBoard(-1);
        	   Const.setFullScreen(GameActivity.this);
        	   playMusic(true);
           }
		})
		.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				GameView.getIns().createNewBoard(-1);
	        	Const.setFullScreen(GameActivity.this);
	        	//playMusic(true);
	        	showExitDialog();
			}
		})
		.create()
		.show();
	}
	
	@Override
	public void onBackPressed(){
		showExitDialog();
		//super.onBackPressed();
	}
	
	/** Play sounds when user touch the controls
	 * @param SoundType to select type of sound to play*/ 
	public void playSound(SoundType s){
		//if we have an instance of the player and the user wants to play sounds
		if(getSharedPreferences(Const.TAG,0).getBoolean(getString(R.string.key_sound),true)){
			if(previousSound != s || soundPlayer == null){
				previousSound = s;
				if(soundPlayer != null)
					soundPlayer.release();
				switch(s){
				case TOUCH:	soundPlayer = MediaPlayer.create(this, R.raw.confirm); break;
				case WIN:	soundPlayer = MediaPlayer.create(this, R.raw.win); break;
				case LOSE:	soundPlayer = MediaPlayer.create(this, R.raw.lose); break;
				default:	break;
				}				
			}
			else
				//if it was playing, stop it to restart it
				if(soundPlayer.isPlaying())
					soundPlayer.stop();
			soundPlayer.start();
		}
			
	}
	
	/** Play/Stop Background music
	 * @param play true to play music, false to pause it*/ 
	public void playMusic(boolean play){
		
		//if we have an instance of the player and the user wants to play sounds
		if(musicPlayer != null)
			if (play && getSharedPreferences(Const.TAG,0).getBoolean(getString(R.string.key_music),true)){
				if(!musicPlayer.isPlaying())
					musicPlayer.start();
			}else
				musicPlayer.pause();
	}
}
