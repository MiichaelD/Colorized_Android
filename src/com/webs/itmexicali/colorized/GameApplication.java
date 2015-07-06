package com.webs.itmexicali.colorized;

import net.opentracker.android.OTLogService;

import com.webs.itmexicali.colorized.util.Const;

import ProtectedInt.ProtectedInt;
import android.app.Application;

public class GameApplication extends Application {

	@Override
	public void onCreate() {
        super.onCreate();
      
        //Setup ProtectedInt once
  		if(!ProtectedInt.isSetup())
  			ProtectedInt.setup();
  		
  		//OpenTracking
        OTLogService.onCreate(getApplicationContext(), getResources().getString(R.string.app_name));
        // to test things real-time always send data directly to logging service
        // make sure to comment this out if you are not testing
        OTLogService.setDirectSend(Const.D);
	}
}
