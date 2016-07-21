package com.webs.itmexicali.colorized.ads;

import android.app.Activity;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.webs.itmexicali.colorized.util.Log;

public class AdMob extends Advertising{


	//AdMob Advertising
    /** The view to show the ad. */
    private AdView AdMobView;
    private InterstitialAd interstitial;
	
	public AdMob(Activity act) {
		super(act);
	}
    
    @Override
    public View getBanner(){
    	return AdMobView;
    }

	@Override
	public void initAds() {
		// Create an ad.
        AdMobView = new AdView(pAct);
        AdMobView.setAdSize(AdSize.SMART_BANNER);
        AdMobView.setAdUnitId(Advertising.ADVIEW_AD_UNIT_ID);
        AdMobView.setVisibility(View.GONE);
        AdMobView.setAdListener(new AdListener() {
        	@Override
    		public void onAdOpened() {// Save app state before going to the ad overlay.
    			Log.d(this.getClass().getSimpleName(),"AdView - Opened");
    			AdMobView.setVisibility(View.GONE);
    		}
    		@Override
    		public void onAdFailedToLoad(int errorCode){
    			Log.d(this.getClass().getSimpleName(),"AdView - FailedToLoad = "+errorCode);
    			AdMobView.setVisibility(View.GONE);
    		}
    		@Override
    		public void onAdLoaded(){
    			AdMobView.setVisibility(View.VISIBLE);
    		}
    	});
        /* Start loading the ad in the background.*/
        AdMobView.loadAd(createAdRequest());	
		
		
        
        // Create the INTERSTTIAL.
        interstitial = new InterstitialAd(pAct);
        interstitial.setAdUnitId(Advertising.INTERSTITIAL_AD_UNIT_ID);
        interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
            	loadInterstitial();
            }
        });
        loadInterstitial();
	}

	@Override
	public void loadInterstitial() {
		if(!interstitial.isLoaded())
			interstitial.loadAd(createAdRequest());
	}

	@Override
	public void showInterstitial() {
		Log.i(this.getClass().getSimpleName(),"ShowingInterstitials ");
		if (interstitial.isLoaded())
			interstitial.show();
		else
			Log.w(this.getClass().getSimpleName(),"Interstitial not loaded");		
	}

	@Override
	public void destroyBanner() {
		if(AdMobView != null){// remove the existing adView 
	    	AdMobView.destroy();
	    	AdMobView = null;
		}
	}

	@Override
	public void onResume() {
		//ADMOB Advertising
		if (AdMobView != null) {
	  	      AdMobView.resume();
	  	}
	}

	@Override
	public void onPause() {
		//ADMOB Advertising
		if (AdMobView != null) {
			AdMobView.pause();
  		}
	}
	
	/** Create an ad request. Check logcat output for the hashed device ID to
     *	get test ads on a physical device.*/
	private AdRequest createAdRequest(){
		return new AdRequest.Builder()
            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
            //.addTestDevice("584586A082596B5844C4E301E1285E95") //My Nexus 4
            .build();
	}
}
