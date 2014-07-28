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
		Const.setFullScreen(this);
	}
}
