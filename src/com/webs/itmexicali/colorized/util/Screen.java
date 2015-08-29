package com.webs.itmexicali.colorized.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

public class Screen {
	
	 /** Hide Action Bar in devices that support it */
		@SuppressLint("InlinedApi")
		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		public static void setFullScreen(Activity ac){
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				ac.getActionBar().hide();
				
				try{

					View decorView = ac.getWindow().getDecorView();
					// Hide both the navigation bar and the status bar.
					// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
					// a general rule, you should design your app to hide the status bar whenever you
					// hide the navigation bar.
					int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
							View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
			
					decorView.setSystemUiVisibility(uiOptions);
				}catch(NoSuchMethodError e){ /* some devices have not defined this method*/}
			}
			
			ac.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,
			WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		
}
