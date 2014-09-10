package com.webs.itmexicali.colorized.ads;

import android.app.Activity;
import android.view.View;

import com.webs.itmexicali.colorized.util.Const;
import com.xsqhbao.bipppts201390.AdListener.AdType;
import com.xsqhbao.bipppts201390.Prm;//AirPush_STD

public class AirPushStandard extends Advertising{
	

	//AirPush Ads STD
    private com.xsqhbao.bipppts201390.AdView AirPushView;
    private Prm air_std;
	
	public AirPushStandard(Activity act) {
		super(act);
	}
    
	@Override
	public void initAds() {
		AirPushView = new com.xsqhbao.bipppts201390.AdView(
				pAct, com.xsqhbao.bipppts201390.AdView.BANNER_TYPE_IN_APP_AD,
				com.xsqhbao.bipppts201390.AdView.PLACEMENT_TYPE_INLINE, false, false, 
				com.xsqhbao.bipppts201390.AdView.ANIMATION_TYPE_FADE);
		
		air_std = new Prm(pAct,
				new com.xsqhbao.bipppts201390.AdListener(){
			@Override
			public void noAdAvailableListener() {
				Const.e(this.getClass().getSimpleName(), "noAdAvailableListener");	
			}
			@Override
			public void onAdCached(AdType arg0) {
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
		air_std.runSmartWallAd();
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
		air_std.runSmartWallAd();
	}
	
	@Override
	public void showInterstitial() {
		try {
			air_std.runCachedAd(pAct, AdType.smartwall);
		} catch (Exception e) {
			Const.w(this.getClass().getSimpleName(),"Interstitial not showing Cached");
			air_std.runSmartWallAd();
			e.printStackTrace();
		}
	}
}
