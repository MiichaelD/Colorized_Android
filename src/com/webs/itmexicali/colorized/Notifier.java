package com.webs.itmexicali.colorized;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.webs.itmexicali.colorized.R;
import com.webs.itmexicali.colorized.drawcomps.BitmapLoader;
import com.webs.itmexicali.colorized.util.Const;
import com.webs.itmexicali.colorized.util.ProgNPrefs;

public class Notifier {
	
	static int NO_NOTIF = -1,  NOTIFICATION_ID = NO_NOTIF;
	
	Context mContext;
	
	NotificationManager mNotifMan = null;
	
	public static String ACTIVITY = "activity", TITLE = "title", TICKER = "ticker", WHEN = "when";
	public static String MESSAGE = "message", CONTENT_INFO = "content_info", NOTIF_KEY = "last_notif_id";
	public static String SMALL_ICON = "small", BIG_ICON = "big", VIBRATE = "vibrate", SOUND = "sound";
	public static String DIRECT_TO_GAME = "go_to_gamestate";
	
	public Notifier(Context ctx){
		mContext = ctx;
		mNotifMan = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		SharedPreferences sp = mContext.getSharedPreferences(Const.TAG, 0);
		NOTIFICATION_ID = sp.getInt(NOTIF_KEY, NO_NOTIF);
	}
	
	public static Notifier getInstance(Context ctx){
		return new Notifier(ctx);
	}
	   
	public void notify(String title, String message){
		final Bundle bundle = new Bundle();
		bundle.putString(TITLE, title);
		bundle.putString(MESSAGE, message);
		bundle.putString(TICKER, title);
		new Thread(new Runnable(){ public void run(){
			Notifier.this.notify(bundle);
		}}).start();
	}
	
	public void notify(String title, String message, int smallIcon, int largeIcon, Class<? extends Activity> classToStart){
		final Bundle bundle = new Bundle();
		bundle.putString(MESSAGE, message);
		bundle.putString(TITLE, title);
		bundle.putInt(NOTIF_KEY, ++NOTIFICATION_ID);
		bundle.putString(TICKER, title);
		bundle.putInt(SMALL_ICON, android.R.drawable.ic_menu_agenda);
		bundle.putInt(BIG_ICON, R.drawable.app_icon);
		bundle.putString(ACTIVITY, classToStart.getCanonicalName());
		new Thread(new Runnable(){ public void run(){
			Notifier.this.notify(bundle);
		}}).start();
	}

	public void schedule(String title, String message, Class<? extends Activity> classToStart, int mins, boolean directToGame){
		Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.MINUTE, mins);

	    Intent intent = new Intent(mContext, NotificationReceiver.class);
	    intent.putExtra(MESSAGE, message);
	    intent.putExtra(TITLE, title);
	    intent.putExtra(NOTIF_KEY, ++NOTIFICATION_ID);
	    intent.putExtra(TICKER, title);
	    intent.putExtra(SMALL_ICON, android.R.drawable.ic_menu_agenda);
	    intent.putExtra(BIG_ICON, R.drawable.app_icon);
	    intent.putExtra(ACTIVITY, classToStart.getCanonicalName());
	    if(directToGame)
	    	intent.putExtra(DIRECT_TO_GAME, true);

	    PendingIntent sender = PendingIntent.getBroadcast(mContext, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	    AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
	    am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);

		SharedPreferences.Editor spe = mContext.getSharedPreferences(Const.TAG, 0).edit();
		Const.v("Notifier", "We just Scheduled notif: "+NOTIFICATION_ID);
		spe.putInt(NOTIF_KEY, NOTIFICATION_ID);
		spe.commit();
	}
	
	public void notify(Bundle bundle){
		
		if (ProgNPrefs.getIns() == null){
			ProgNPrefs.initPreferences(mContext);
		}
		if (!ProgNPrefs.getIns().showNotifications())
			return;
		
		//get variables from bundle
		int smallIcon = bundle.getInt(SMALL_ICON, android.R.drawable.ic_menu_agenda);
		int bigIcon = bundle.getInt(BIG_ICON, R.drawable.app_icon);
		int notifId = bundle.getInt(NOTIF_KEY, ++NOTIFICATION_ID);
		String targetActivity = bundle.getString(ACTIVITY);
		String title = bundle.getString(TITLE);
		String message = bundle.getString(MESSAGE);
		String ticker = bundle.getString(TICKER);
		String contentInfo = bundle.getString(CONTENT_INFO);
		String when = bundle.getString(WHEN);
		boolean vibrate = bundle.getBoolean(VIBRATE);
		boolean sound = bundle.getBoolean(SOUND);
	    Class<? extends Activity> activityClass = null;
		try {
			if (targetActivity != null)
				activityClass = Class.forName(targetActivity).asSubclass(Activity.class);
		} catch (ClassNotFoundException e) { }
		
		Const.v("Notifier", "Starting notification: "+notifId);
		long t0 = System.currentTimeMillis();
		
		int notifFlags = NotificationCompat.DEFAULT_LIGHTS |  NotificationCompat.FLAG_AUTO_CANCEL | NotificationCompat.FLAG_ONLY_ALERT_ONCE;
		if(vibrate)
			notifFlags |= NotificationCompat.DEFAULT_VIBRATE;
		if(sound)
			notifFlags |= NotificationCompat.DEFAULT_SOUND;
	    //creating notification
		NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
		.setAutoCancel(true)
		.setSmallIcon(smallIcon)
		.setLargeIcon(BitmapLoader.getImage(mContext, bigIcon, true))
		.setContentTitle(title)
		.setContentText(message)
		.setTicker(ticker == null? title : ticker)
		.setWhen(when == null ? System.currentTimeMillis() : Long.parseLong(when)*1000)
		.setContentInfo(contentInfo == null || contentInfo.isEmpty() ?  Integer.toString(notifId) : contentInfo)
		.setDefaults(notifFlags)
		.setStyle(new NotificationCompat.BigTextStyle() // text to be displayed when expanded
			.setBigContentTitle(title)
			.bigText(message));
		
		if(vibrate)
			builder.setVibrate(new long[] { 1000, 1000});
		if(sound)
			builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		
		//adding an action to the notification
		if (activityClass != null){
			Intent notIntent = new Intent(mContext, activityClass);
			notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			if(bundle.getBoolean(DIRECT_TO_GAME))
				notIntent.putExtra(DIRECT_TO_GAME, true);
			PendingIntent contIntent = PendingIntent.getActivity(mContext, NOTIFICATION_ID, notIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(contIntent);
		}
	   
		//send the notification
		mNotifMan.notify(notifId, builder.build());
		saveNotifId();
		Const.v(this.getClass().getSimpleName(), "finihed in: "+(System.currentTimeMillis()-t0));
	}
	
	
	/** Clear pending and received notifications if we don't longer need them*/
	public void clearAll() {
		if (NOTIFICATION_ID == NO_NOTIF)
			return;
		  
	    Intent intent = new Intent(mContext, NotificationReceiver.class);
	    AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

	    //clear scheduled notifications 
	    for (int ind = 0; ind <= NOTIFICATION_ID; ++ind) {
	    	try {
	    		PendingIntent sender = PendingIntent.getBroadcast(mContext, ind, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	    		am.cancel(sender);
	    		Const.v("Notifier", "Cancelling pending notification: "+ind);
	    	} catch (Exception e) { }
	    }

	    //clear notifications already received
	    if (mNotifMan!=null) 
	    	mNotifMan.cancelAll();

		Const.v("Notifier", "Clearing notifs already in the drawer and resetting counter to 0");
		NOTIFICATION_ID = NO_NOTIF;
		SharedPreferences.Editor spe = mContext.getSharedPreferences(Const.TAG, 0).edit();
		spe.putInt(NOTIF_KEY, NO_NOTIF);
		spe.commit();
	}
	
	public void testNotifs(){
		notify("testNotifs","Being displayed now...");
    	schedule("testNotifs","close the app", GameActivity.class, 20, false);
    	notify("testNotifs","test");
    	schedule("testNotifs","let this notification there", GameActivity.class, 25, false);
    	schedule("testNotifs","open the app",GameActivity.class, 30, false);
    	schedule("testNotifs","cann you see this?", GameActivity.class, 45, false);
    	schedule("testNotifs","you shouldn't see this!", GameActivity.class, 60, false);
    	schedule("testNotifs","neither this notif", GameActivity.class, 70, false);
	}
	
	public void saveNotifId(){
		SharedPreferences sp = mContext.getSharedPreferences(Const.TAG, 0);
		int stored = sp.getInt(NOTIF_KEY, NO_NOTIF);
		if ( stored < NOTIFICATION_ID){
			SharedPreferences.Editor spe = sp.edit();
			spe.putInt(NOTIF_KEY, NOTIFICATION_ID);
			spe.commit();
		}
	}
}
