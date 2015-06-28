package com.webs.itmexicali.colorized;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
    	Notifier.getInstance(context).notify(intent.getExtras());
    }
}
