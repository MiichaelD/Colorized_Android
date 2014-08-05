package com.webs.itmexicali.colorized;

import android.app.Activity;
import android.os.Bundle;

public class MainMenu extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//run on FullScreen with no Action and Navigation Bars
		Const.setFullScreen(this);
		setContentView(R.layout.main_screen);
	}
}
