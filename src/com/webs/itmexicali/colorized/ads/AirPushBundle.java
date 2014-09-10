package com.webs.itmexicali.colorized.ads;

import android.app.Activity;
import android.view.View;

import com.webs.itmexicali.colorized.util.Const;

/** Advertising by AirPush - Bundle edition 
 * NOTE: to use this ads, you should uncomment some lines of code in
 * AirPushBundle class and the AndroidManifest file*/
public class AirPushBundle extends Advertising{

	//AirPush Ads Bundle
    //private com.xsqhbao.bipppts201390.MA air;
    private com.xsqhbao.bipppts201390.AdView AirPushView;
	
	public AirPushBundle(Activity ctx) {
		super(ctx);
	}

	@Override
	public void initAds() {
		
		AirPushView = new com.xsqhbao.bipppts201390.AdView(
				pAct, com.xsqhbao.bipppts201390.AdView.BANNER_TYPE_IN_APP_AD,
				com.xsqhbao.bipppts201390.AdView.PLACEMENT_TYPE_INLINE, false, false, 
				com.xsqhbao.bipppts201390.AdView.ANIMATION_TYPE_FADE);
		
		/**Initializing Bundle SDK 
		 * @param Activity * @param AdListener * @param caching*/
		/*
		air = new MA(this, 
		
				new com.xsqhbao.bipppts201390.AdListener(){
			@Override
			public void noAdAvailableListener() {
				Const.e(this.getClass().getSimpleName(), "noAdAvailableListener");	
			}
			@Override
			public void onAdCached(com.xsqhbao.bipppts201390.AdListener.AdType arg0) {
				Const.d(this.getClass().getSimpleName(), "onAdCached type: "+arg0.name());
			}
			@Override
			public void onAdError(String arg0) {
				Const.e(this.getClass().getSimpleName(), "onAdError: "+arg0);	
			}
			@Override
			public void onSDKIntegrationError(String arg0) {
				Const.e(this.getClass().getSimpleName(), "onSDKIntegrationError: "+arg0);	
			}
			@Override
			public void onSmartWallAdClosed() {
				Const.d(this.getClass().getSimpleName(), "onSmartWallAdClosed");
				loadInterstitial();
			}
			@Override
			public void onSmartWallAdShowing() {
				Const.d(this.getClass().getSimpleName(), "onSmartWallAdShowing");				
			}
		},
		true);
		 */
		//Caching Smartwall Ad. 
		loadInterstitial();
				
		
	}

	@Override
	public View getBanner() {
		return AirPushView;
	}

	@Override
	public void destroyBanner() {
		if(AirPushView != null){// remove the existing
        	AirPushView = null;
        }
	}

	@Override
	public void loadInterstitial() {
		//Caching Smartwall Ad for future use
		//air.callSmartWallAd();
	}

	@Override
	public void showInterstitial() {
		try {
			//air.showCachedAd(pAct, AdType.smartwall);
		} catch (Exception e) {
			Const.w(AirPushBundle.class.getSimpleName(),"Interstitial not showing Cached");
			//air.callSmartWallAd();
			e.printStackTrace();
		}
	}
}
