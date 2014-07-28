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
		Const.setFullScreen(this);
		
		/*************************************	ADS ADMOB	*********************************************/
		// Create an ad.
        adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
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
	
}
