package com.webs.itmexicali.colorized.util;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
    	Notifier.getInstance(context).notify(intent.getExtras());
    }
}
