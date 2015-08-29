package com.webs.itmexicali.colorized.ads;

import android.app.Activity;
import android.view.View;

import com.webs.itmexicali.colorized.util.Log;

/** Advertising by AirPush - Bundle edition 
 * 
 * http://manage.airpush.com/docs/index.php?title=Bundle_SDK_1.0_Documentation
 * 
 * NOTE: to use this ads, you should uncomment some lines of code in
 * AirPushBundle class and the AndroidManifest file (THE PROPER MODIFICATIONS 
 * OF MANIFEST ARE AVAILABLE AT THE END OF THIS CLASS)*/
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
				com.xsqhbao.bipppts201390.AdView.ANIMATION_TYPE_LEFT_TO_RIGHT);
		
		/**Initializing Bundle SDK 
		 * @param Activity * @param AdListener * @param caching*/
		/*
		air = new com.xsqhbao.bipppts201390.MA(pAct, 
				new com.xsqhbao.bipppts201390.AdListener(){
			@Override
			public void noAdAvailableListener() {
				Log.e(this.getClass().getSimpleName(), "noAdAvailableListener");	
			}
			@Override
			public void onAdCached(com.xsqhbao.bipppts201390.AdListener.AdType arg0) {
				Log.d(this.getClass().getSimpleName(), "onAdCached type: "+arg0.name());
			}
			@Override
			public void onAdError(String arg0) {
				Log.e(this.getClass().getSimpleName(), "onAdError: "+arg0);	
			}
			@Override
			public void onSDKIntegrationError(String arg0) {
				Log.e(this.getClass().getSimpleName(), "onSDKIntegrationError: "+arg0);	
			}
			@Override
			public void onSmartWallAdClosed() {
				Log.d(this.getClass().getSimpleName(), "onSmartWallAdClosed");
				loadInterstitial();
			}
			@Override
			public void onSmartWallAdShowing() {
				Log.d(this.getClass().getSimpleName(), "onSmartWallAdShowing");				
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
			//air.showCachedAd(pAct, com.xsqhbao.bipppts201390.AdListener.AdType.smartwall);
		} catch (Exception e) {
			Log.w(AirPushBundle.class.getSimpleName(),"Interstitial not showing Cached");
			//air.callSmartWallAd();
			e.printStackTrace();
		}
	}
}

/* **MANIFEST MODIFICATIONS:**

 ***********************	PERMISSIONS /	UNDER ROOT TAG: *******************************************
 
 <!-- AirPush Implementation START -->  
	<uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
	<uses-permission android:name="android.permission.READ_PHONE_STATE" />
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	<!-- AirPush Bundle -->
	<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
	<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
	<uses-permission android:name="com.android.browser.permission.READ_HISTORY_BOOKMARKS" />
	<uses-permission android:name="android.permission.GET_ACCOUNTS" />
<!-- AirPush Implementation END -->  
 
 ************	ACTIVITIES AND APP COMPONENTS	UNDER APPLICATION TAG: *********************************
 
 <!-- AirPush Implementation START -->        
        <meta-data android:name="com.google.android.gms.version" android:value="@integer/google_play_services_version" />
		<meta-data android:name="com.xsqhbao.bipppts201390.APPID" android:value="236182" />
		<meta-data android:name="com.xsqhbao.bipppts201390.APIKEY" android:value="android*1409523218201390512"/>
		<!-- AirPush Bundle -->
		<activity android:exported="false" android:name="com.xsqhbao.bipppts201390.AdActivity"		
		<!-- AirPush STD 
		<activity android:exported="false" android:name="com.xsqhbao.bipppts201390.MainActivity"
		-->
		     android:configChanges="orientation|screenSize"
			android:theme="@android:style/Theme.Translucent" />
			
		<activity android:name="com.xsqhbao.bipppts201390.BrowserActivity"
			android:configChanges="orientation|screenSize" />
		<!-- AirPush Bundle -->
		<activity android:name="com.xsqhbao.bipppts201390.VActivity"
		<!-- AirPush STD
		<activity android:name="com.xsqhbao.bipppts201390.VDActivity"
		-->
			android:configChanges="orientation|screenSize" android:screenOrientation="portrait"
		    android:theme="@android:style/Theme.NoTitleBar.Fullscreen" >
		</activity> 
		<!-- AirPush Bundle -->
		 <service android:name="com.xsqhbao.bipppts201390.LService" android:exported="false"></service>
		 <receiver android:name="com.xsqhbao.bipppts201390.BootReceiver" android:exported="true">
			<intent-filter>
				<action android:name="android.intent.action.BOOT_COMPLETED" />
			</intent-filter>
		</receiver>
<!-- AirPush Implementation END -->
 
 
 */
 
