package com.webs.itmexicali.colorized;

import java.util.Date;
import java.util.Iterator;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.webs.itmexicali.colorized.util.Const;

public class GcmIntentService extends IntentService{
	public static String TAG = GcmIntentService.class.getSimpleName();
	
	public GcmIntentService(){
		super("GcmIntentService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);
        Bundle extras = intent.getExtras();
        Const.i(TAG, "message Type: "+messageType);
        if (!extras.isEmpty()){
        	Iterator<String> keyIterator = extras.keySet().iterator();
        	while(keyIterator.hasNext()){
        		String key = keyIterator.next();
        		Const.v(TAG, "key: "+key+"="+extras.get(key).toString());
        	}
        	
        	
        	if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)){
        		Bundle bundle = new Bundle();
        		bundle.putString(Notifier.ACTIVITY,SplashScreen.class.getCanonicalName());
        		if(extras.getString(Notifier.TITLE) != null)
        			bundle.putString(Notifier.TITLE,extras.getString(Notifier.TITLE));
        		
        		if(extras.getString(Notifier.MESSAGE) != null)
        			bundle.putString(Notifier.MESSAGE,extras.getString(Notifier.MESSAGE));


        		if(extras.getString(Notifier.TICKER) != null)
        			bundle.putString(Notifier.TICKER,extras.getString(Notifier.TICKER));
        		
        		if(extras.getString(Notifier.CONTENT_INFO) != null)
        			bundle.putString(Notifier.CONTENT_INFO,extras.getString(Notifier.CONTENT_INFO));
        		
        		String when = extras.getString(Notifier.WHEN);
        		if (when != null){
        			Date d = new Date(Long.parseLong(when)*1000);
            		Const.i(TAG, "time: "+when+": "+d.toString());
            		bundle.putString(Notifier.WHEN, when);
        		}

    			bundle.putBoolean(Notifier.VIBRATE,extras.getBoolean(Notifier.VIBRATE));
    			bundle.putBoolean(Notifier.SOUND,extras.getBoolean(Notifier.SOUND));
        		
        		Notifier.getInstance(getApplicationContext()).notify(bundle);
        	}
        }
        GcmReceiver.completeWakefulIntent(intent);
	}
}