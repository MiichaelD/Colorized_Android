package com.webs.itmexicali.colorized;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.WindowManager;

public class MainMenu extends Activity {

	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_screen);
	
		//run on FullScreen with no Action and Navigation Bars
		setFullScreen();
	}
	
	/** Hide Action Bar in devices that support it */
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
