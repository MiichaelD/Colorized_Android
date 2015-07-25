package com.webs.itmexicali.colorized;

import java.util.Date;
import java.util.Iterator;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.webs.itmexicali.colorized.util.Const;
import com.webs.itmexicali.colorized.util.Notifier;

public class GcmIntentService extends IntentService{
	public static String TAG = GcmIntentService.class.getSimpleName();
	
	public GcmIntentService(){
		super(TAG);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);
		Bundle extras = intent.getExtras();

        Const.i(TAG, "Action: "+intent.getAction());
        Const.i(TAG, "Data: "+intent.getDataString());
        Const.i(TAG, "message Type: (gcm)="+messageType+"  (intent)="+intent.getType());
        if (!extras.isEmpty()){        	
        	Iterator<String> keyIterator = extras.keySet().iterator();
        	while(keyIterator.hasNext()){
        		String key = keyIterator.next();
        		Const.v(TAG, "key: "+key+"="+extras.get(key).toString());
        	}
        	

        	if(!extras.containsKey("origin") ){
        		return;        		
        	} 
        	
        	String origin = extras.getString("origin");
    		Const.i(TAG, "Origin: "+origin);
    		
        	if (origin.equals("colorized") && GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)){
        		Bundle bundle = new Bundle();
        		bundle.putString(Notifier.ACTIVITY,SplashScreen.class.getCanonicalName());
        		String title, message, ticker, content, when;
        		title = extras.getString(Notifier.TITLE);
        		content = extras.getString(Notifier.CONTENT_INFO);
        		message = extras.getString(Notifier.MESSAGE);
        		ticker = extras.getString(Notifier.TICKER);
        		when = extras.getString(Notifier.WHEN);
        		
        		//needed contents:
        		if (message == null)
        			return;
        		
        		bundle.putString(Notifier.TITLE, title);
        		bundle.putString(Notifier.MESSAGE,message);
        		bundle.putString(Notifier.TICKER,ticker == null? message : ticker);
        		
        		//optional contents:
        		if(content != null){
        			bundle.putString(Notifier.CONTENT_INFO,extras.getString(Notifier.CONTENT_INFO));
        		}
        		
        		if (when != null){
        			Date d = new Date(Long.parseLong(when)*1000);
            		Const.i(TAG, "time: "+when+": "+d.toString());
            		bundle.putString(Notifier.WHEN, when);
        		}

        		if(extras.containsKey(Notifier.VIBRATE))
        			bundle.putBoolean(Notifier.VIBRATE, extras.getBoolean(Notifier.VIBRATE));
        		
        		if(extras.containsKey(Notifier.SOUND))
        			bundle.putBoolean(Notifier.SOUND, extras.getBoolean(Notifier.SOUND));
        		
        		Notifier.getInstance(getApplicationContext()).notify(bundle);
        	}
        }
        GcmReceiver.completeWakefulIntent(intent);
	}
}