package com.webs.itmexicali.colorized;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class GameActivity extends Activity {
	
	//this Activity instance
	private static GameActivity m_instance = null;
	
	//AdMob Advertising
    /** The view to show the ad. */
    private AdView adView = null;
	
	public static GameActivity getIns(){
		return m_instance;
	}
	
	

	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		m_instance = this;

		setContentView(R.layout.game_screen);
	
		//run on FullScreen with no Action and Navigation Bars
		setFullScreen();
		
		
        
	}
	
	/** this method let the activity know when the orientation of the device has changed
	 * to place correctly the ads
	 * @param portrait true if the device orientation is portrait, false if not*/
	public void setAdsPosition(boolean portrait){
		
		/*************************************	ADS ADMOB	*********************************************/
		// Add the AdView to the view hierarchy. The view will have no size
        // until the ad is loaded.
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.LayMain);
        if(adView != null){
        	layout.removeView(adView); // remove the existing adView
        	adView.destroy();
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
        		RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		
        
        // Create an ad.
        adView = new AdView(this);
        adView.setAdSize(portrait? AdSize.SMART_BANNER:AdSize.BANNER);
        adView.setAdUnitId(Const.AD_UNIT_ID);
        adView.setAdListener(new AdListener() {
        	  @Override
        	  public void onAdOpened() {
        	    // Save app state before going to the ad overlay.
        		  if(Const.D){
        			  Log.d(Const.TAG,"AdView - Opened");
        		  }
        	  }
        	  @Override
        	  public void onAdFailedToLoad(int errorCode){
        		  if(Const.D){
        			  Log.d(Const.TAG,"AdView - FailedToLoad = "+errorCode);
        		  }
        	  }
        	});
		
		if (portrait){
	        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
	    	params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		}
		else{
	        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	    	params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		}
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
	
	/** Hide Action Bar in devices that support it */
	@SuppressLint("InlinedApi")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setFullScreen(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().hide();
		}
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		

		View decorView = getWindow().getDecorView();
		// Hide both the navigation bar and the status bar.
		// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
		// a general rule, you should design your app to hide the status bar whenever you
		// hide the navigation bar.
		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
				View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		
		decorView.setSystemUiVisibility(uiOptions);
	}
}
