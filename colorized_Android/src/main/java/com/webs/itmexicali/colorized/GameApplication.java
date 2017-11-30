package com.webs.itmexicali.colorized;

import android.app.Application;

import com.webs.itmexicali.colorized.util.ProgNPrefs;
import com.webs.itmexicali.colorized.util.Tracking;

import ProtectedInt.ProtectedInt;

public class GameApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    //Setup ProtectedInt once
    if (!ProtectedInt.isSetup())
      ProtectedInt.setup();

    //Init tracking lib in case we don't start the app form an activity but a broadcast receiver
    Tracking.shared().init(this);

    //Initialize Preferences, it depends on ProtectedInt and OpenTracking since
    ProgNPrefs.init(this);
  }
}
