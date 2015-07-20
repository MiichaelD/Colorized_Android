package com.webs.itmexicali.colorized;
import com.webs.itmexicali.colorized.util.ProgNPrefs;
import com.webs.itmexicali.colorized.util.Tracking;

import ProtectedInt.ProtectedInt;
import android.app.Application;

public class GameApplication extends Application {

	@Override
	public void onCreate() {
        super.onCreate();
      
        //Setup ProtectedInt once
  		if(!ProtectedInt.isSetup())
  			ProtectedInt.setup();
  		
  		//Init tracking lib
  		Tracking.shared().init(this, getResources().getString(R.string.mixpanel_api_token));
        
  		//Initialize Preferences, it depends on ProtectedInt and OpenTracking since 
       ProgNPrefs.init(this);
	}
}
