package com.webs.itmexicali.colorized;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.webs.itmexicali.colorized.util.Log;

public class GcmReceiver extends WakefulBroadcastReceiver {
  public static String TAG = GcmReceiver.class.getSimpleName();

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.i(TAG, "GcmReceiver got a push!");
    ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
    startWakefulService(context, (intent.setComponent(comp)));
    try {
      setResultCode(Activity.RESULT_OK);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
