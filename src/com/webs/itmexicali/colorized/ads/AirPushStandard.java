package com.webs.itmexicali.colorized.ads;

import android.app.Activity;
import android.view.View;

import com.webs.itmexicali.colorized.util.Const;

/** Advertising by AirPush - Standard edition 
 * 
 * http://manage.airpush.com/docs/index.php?title=Standard_SDK_1.0
 * 
 * NOTE: to use this ads, you should uncomment some lines of code in
 * AirPushStandard class and the AndroidManifest file(THE PROPER MODIFICATIONS 
 * OF MANIFEST ARE AVAILABLE AT THE END OF THIS CLASS)*/
public class AirPushStandard extends Advertising{
	

	//AirPush Ads STD
    private com.xsqhbao.bipppts201390.AdView AirPushView;
    private com.xsqhbao.bipppts201390.Prm air_std;
	
	public AirPushStandard(Activity act) {
		super(act);
	}
    
	@Override
	public void initAds() {
		AirPushView = new com.xsqhbao.bipppts201390.AdView(
				pAct, com.xsqhbao.bipppts201390.AdView.BANNER_TYPE_IN_APP_AD,
				com.xsqhbao.bipppts201390.AdView.PLACEMENT_TYPE_INLINE, false, false, 
				com.xsqhbao.bipppts201390.AdView.ANIMATION_TYPE_LEFT_TO_RIGHT);
		
		air_std = new com.xsqhbao.bipppts201390.Prm(pAct,
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
		
		//360 Ad.
		air_std.run360Ad(pAct,0,false,null);
		
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
		air_std.runSmartWallAd();
		//air_std.runAppWall();
	}
	
	@Override
	public void showInterstitial() {
		try {
			air_std.runCachedAd(pAct, com.xsqhbao.bipppts201390.AdListener.AdType.smartwall);
			//air_std.runCachedAd(pAct, com.xsqhbao.bipppts201390.AdListener.AdType.appwall);
		} catch (Exception e) {
			Const.w(this.getClass().getSimpleName(),"Interstitial not showing Cached");
			air_std.runSmartWallAd();
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
 