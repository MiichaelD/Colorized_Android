package com.webs.itmexicali.colorized;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

/**
 * Splash Screen to display the LOGO
 */
public class SplashScreen extends Activity {
	
	// Splash screen timer
    private static int SPLASH_TIME_OUT = 400;//2500;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.splash_screen);
		
		
		//run on FullScreen with no Action and Navigation Bars
		Const.setFullScreen(this);

		//set the timer to start the new activity
		new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, GameActivity.class);
                startActivity(i);
 
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
	}
	
}
