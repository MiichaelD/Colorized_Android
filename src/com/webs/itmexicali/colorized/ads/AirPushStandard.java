package com.webs.itmexicali.colorized.ads;

import android.app.Activity;
import android.view.View;

import com.xsqhbao.bipppts201390.AdConfig;
import com.xsqhbao.bipppts201390.AdConfig.AdType;
import com.xsqhbao.bipppts201390.AdListener;
import com.xsqhbao.bipppts201390.AdView;
import com.xsqhbao.bipppts201390.Main;

import com.webs.itmexicali.colorized.util.Log;

/** Advertising by AirPush - Standard edition 
 * 
 * http://manage.airpush.com/docs/index.php?title=Standard_SDK_1.0
 * 
 * NOTE: to use this ads, you should uncomment some lines of code in
 * AirPushStandard class and the AndroidManifest file(THE PROPER MODIFICATIONS 
 * OF MANIFEST ARE AVAILABLE AT THE END OF THIS CLASS)*/


public class AirPushStandard extends Advertising{
	

	//AirPush Ads STD
    private AdView airPushView;
    private Main airMain;
    private AdListener airListener;
	
	public AirPushStandard(Activity act) {
		super(act);
	}
    
	@Override
	public void initAds() {
		airListener = new AirPushListener();
		
		AdConfig.setAppId(Advertising.APPID);
		AdConfig.setApiKey(Advertising.APIKEY);
		AdConfig.setCachingEnabled(true);
		AdConfig.setPlacementId(0);  //AdView.PLACEMENT_TYPE_INLINE); // what does this mean???
		AdConfig.setAdListener(airListener);

		airMain = new Main(pAct);
		
		airPushView = new AdView(pAct);
		airPushView.setBannerType(AdView.BANNER_TYPE_IN_APP_AD);
		airPushView.setBannerAnimation(AdView.ANIMATION_TYPE_LEFT_TO_RIGHT);
		airPushView.showMRinInApp(false);
		airPushView.setNewAdListener(airListener);
		airPushView.loadAd(); 
		
		//360 Ad.
		//air_main.run360Ad(pAct);
		
		loadInterstitial();
	}

	@Override
	public View getBanner() {
		return airPushView;
	}
	@Override
	public void destroyBanner() {
		if(airPushView != null){// remove the existing
        	airPushView = null;
        }
	}
	@Override
	public void loadInterstitial() {
		//Caching Smartwall Ad for future use
		airMain.startInterstitialAd(AdType.smartwall);
	}
	
	@Override
	public void showInterstitial() {
		try {
			airMain.showCachedAd(AdType.smartwall);
		} catch (Exception e) {
			Log.w(this.getClass().getSimpleName(),"showInterstitial(): Interstitial not showing Cached");
			loadInterstitial();
			e.printStackTrace();
		}
	}
	
	class AirPushListener implements AdListener{
		@Override
		public void onAdError(String errorMessage) {
			//This will get called if any error occurred during ad serving.
			Log.e(this.getClass().getSimpleName(), "onAdError(): "+errorMessage);	
		}
		@Override
		public void noAdListener() {
			 //this will get called when ad is not available 
			Log.d(this.getClass().getSimpleName(), "noAdListener()");
			loadInterstitial();
		}
		@Override
		public void onAdCached(AdType adType) {
			//This will get called when an ad is cached. 
			Log.d(this.getClass().getSimpleName(), "onAdCached(): "+adType.name());	
		}
		@Override
		public void onAdClickedListener() {
			 //This will get called when ad is clicked.
			Log.d(this.getClass().getSimpleName(), "onAdClickedListener()");	
		}
		@Override
		public void onAdClosed() {
			// This will be called by SDK when the SmartWall ad is closed.
			Log.d(this.getClass().getSimpleName(), "onAdClosed()");
			// SDK complains if we request a smart wall in less than 10 secs after showing it
//			loadInterstitial(); 
		}
		@Override
		public void onAdExpandedListner() {
			 //This will get called when an ad is showing on a user's screen. This may cover the whole UI.
			Log.d(this.getClass().getSimpleName(), "onAdExpandedListener()");
		}
		@Override
		public void onAdLoadedListener() {
			  //This will get called when an ad has loaded.
			Log.d(this.getClass().getSimpleName(), "onAdLoadedListener()");
		}
		@Override
		public void onAdLoadingListener() {
			 //This will get called when a rich media ad is loading.
			Log.d(this.getClass().getSimpleName(), "onAdLoadingListener()");
		}
		@Override
		public void onAdShowing() {
			// This will get called by the sSDK when showing any of the smart wall ads
			Log.d(this.getClass().getSimpleName(), "onAdShowing()");
		}
		@Override
		public void onCloseListener() {
			// when an ad is closing/resizing from an expanded state
			Log.d(this.getClass().getSimpleName(), "onCloseListener()");
		}
		@Override
		public void onIntegrationError(String errorMessage) {
			//This will get called if any error occurred and if there are integration mistakes.
			Log.w(this.getClass().getSimpleName(), "onIntegrationError(): "+errorMessage);
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
	<!-- AirPush Bundle
	<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
	<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
	<uses-permission android:name="com.android.browser.permission.READ_HISTORY_BOOKMARKS" />
	<uses-permission android:name="android.permission.GET_ACCOUNTS" />
	-->
<!-- AirPush Implementation END -->  
 
 ************	ACTIVITIES AND APP COMPONENTS	UNDER APPLICATION TAG: *********************************
 
 <!-- AirPush Implementation START -->        
        <meta-data android:name="com.google.android.gms.version" android:value="@integer/google_play_services_version" />
		<meta-data android:name="com.xsqhbao.bipppts201390.APPID" android:value="236182" />
		<meta-data android:name="com.xsqhbao.bipppts201390.APIKEY" android:value="android*1409523218201390512"/>
		<!-- AirPush Bundle
		<activity android:exported="false" android:name="com.xsqhbao.bipppts201390.AdActivity"
		-->
		<!-- AirPush STD -->
		<activity android:exported="false" android:name="com.xsqhbao.bipppts201390.MainActivity"
		     android:configChanges="orientation|screenSize"
			android:theme="@android:style/Theme.Translucent" />
			
		<activity android:name="com.xsqhbao.bipppts201390.BrowserActivity"
			android:configChanges="orientation|screenSize" />
		<!-- AirPush Bundle
		<activity android:name="com.xsqhbao.bipppts201390.VActivity"
		-->
		<!-- AirPush STD -->
		<activity android:name="com.xsqhbao.bipppts201390.VDActivity"
			android:configChanges="orientation|screenSize" android:screenOrientation="portrait"
		    android:theme="@android:style/Theme.NoTitleBar.Fullscreen" >
		</activity> 
		<!-- AirPush Bundle
		 <service android:name="com.xsqhbao.bipppts201390.LService" android:exported="false"></service>
		 <receiver android:name="com.xsqhbao.bipppts201390.BootReceiver" android:exported="true">
			<intent-filter>
				<action android:name="android.intent.action.BOOT_COMPLETED" />
			</intent-filter>
		</receiver>
		-->
<!-- AirPush Implementation END -->
 
 
 */
 