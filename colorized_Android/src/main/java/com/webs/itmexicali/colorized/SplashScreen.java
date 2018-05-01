package com.webs.itmexicali.colorized;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.webs.itmexicali.colorized.util.Screen;

/**
 * Splash Screen to display the LOGO
 */
public class SplashScreen extends Activity {

  // Splash screen timer
  private static int SPLASH_TIME_OUT = 1000;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    //run on FullScreen with no Action and Navigation Bars
    Screen.setFullScreen(this);

    setContentView(R.layout.splash_screen);

    if (GameActivity.getActivity() != null) {
      startMainActivityAndClose();
      return;
    }
    //set the timer to start the new activity
    new Handler().postDelayed(new Runnable() {
      @SuppressLint("InlinedApi")
      @Override
      public void run() {
        startMainActivityAndClose();
      }
    }, SPLASH_TIME_OUT);
  }

  private void startMainActivityAndClose() {
    // This method will be executed once the timer is over
    // Start your app main activity
    Intent i = new Intent(SplashScreen.this, GameActivity.class);
    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    if (getIntent().getExtras() != null)
      i.putExtras(getIntent().getExtras());
    startActivity(i);

    // close this activity
    finish();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }
}
