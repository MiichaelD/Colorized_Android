package com.webs.itmexicali.colorized;

import com.webs.itmexicali.colorized.util.Const;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Splash Screen to display the LOGO
 */
public class SplashScreen extends Activity {
	
	// Splash screen timer
    private static int SPLASH_TIME_OUT = 1200;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//run on FullScreen with no Action and Navigation Bars
		Const.setFullScreen(this);

		setContentView(R.layout.splash_screen);
		
		//set the timer to start the new activity
		new Handler().postDelayed(new Runnable() {
            @SuppressLint("InlinedApi")
			@Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, GameActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
}
