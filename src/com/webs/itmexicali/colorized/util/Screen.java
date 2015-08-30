package com.webs.itmexicali.colorized.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class Screen {
	
	private static int m_width = -1, m_height = -1;
	
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
	
	/**Update screen dimensions 
	 * http://stackoverflow.com/questions/1016896/get-screen-dimensions-in-pixels*/
	@SuppressWarnings("deprecation")
	private static void updateDimensions(){
        Display display = ((WindowManager) Platform.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (Platform.getVersionCode() >= Build.VERSION_CODES.HONEYCOMB_MR2){
        	updateDimensionsHoneyComb(display);
        } else {
        	m_width = display.getWidth();
        	m_height = display.getHeight();
        }
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private static void updateDimensionsHoneyComb(Display display){
		Point size = new Point();
		display.getSize(size);
		m_width = size.x;
		m_height = size.y;
	}
	
	
	/**Get screen height 
	 * // http://stackoverflow.com/questions/1016896/android-how-to-get-screen-dimensions*/
	public static int getHeight(final Context appContext) {
		if (m_height == -1)
			updateDimensions();
        return m_height;
    }
	
	/**Get screen height 
	 * // http://stackoverflow.com/questions/1016896/android-how-to-get-screen-dimensions*/
	public static int getWidth(final Context appContext) {
		if (m_width == -1)
			updateDimensions();
        return m_width;
    }
		
}
