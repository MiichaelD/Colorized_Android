package com.webs.itmexicali.colorized.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.webs.itmexicali.colorized.GameActivity;

public class PushNotificationHelper {
	/** Google Cloud Messaging Service constants*/
    private static final String SENDER_ID = "920688942654";
    
    /** Preferences keys */
    public static String REG_ID = "regitration_id", USER = "user", APP_VERSION = "app_version";
    public static String EXPIRATION = "server_expiration", SAVED_ONLINE = "saved_online";
    public static final long EXPIRATION_TIME_MS = 1000 * 3600 * 24 * 7; //1 week

    private GoogleCloudMessaging m_gcm;
    
    private static String TAG = PushNotificationHelper.class.getSimpleName().intern();
    
    private String m_userId = null, m_regId;
    
    public void subscribeToPush(final String userId){
    	m_userId = userId;
    	m_regId = getRegistrationId();
    	if(m_regId == null){
    		// if we didn't find any valid regId, request one to GCM service and store it on a DB
    		RegisterToGcmTask tarea = new RegisterToGcmTask();
            tarea.execute(userId);
    	} else { 
    		// if we found the regId, store it on a DB if we haven't done it
    		storeRegistrationIdOnDb();
    	}
    }
    
	public String getRegistrationId(){ 
		   SharedPreferences sp = ProgNPrefs.getIns().getSharedPrefs();
		   String regId = sp.getString(REG_ID, null);
		   
		   if(regId == null){
		        Log.v(this.getClass().getSimpleName(), "Registro GCM no encontrado.");
		        return null;
		   }
		    
		    String registeredUser = sp.getString(USER, null);
		    int registeredVersion = sp.getInt(APP_VERSION, Integer.MIN_VALUE);
		    long expirationTime = sp.getLong(EXPIRATION, Integer.MIN_VALUE);
		    String expirationDate = null;
		    if(expirationTime != Integer.MIN_VALUE){
			    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
			    expirationDate = sdf.format(new Date(expirationTime));
		    }
		    
		    Log.v(TAG, "Registro GCM encontrado (usuario="+registeredUser+
		    		", version="+registeredVersion+", expira="+expirationDate+")");
		    
		    int currentVersion = Const.getVersionCode();
		    
		    if (registeredVersion < currentVersion){
		    	Log.v(TAG, "Registro de version anterior de la aplicación.");
		        return null;
		    }
		    else if (System.currentTimeMillis() > expirationTime) {
		    	Log.v(TAG, "Registro GCM expirado.");
		        return null;
		    }
		    else if (!m_userId.toString().equals(registeredUser)) {
		    	Log.v(TAG, "Registro con diferente nombre de usuario.");
		        return null;
		    }
		    
		    //set the current valid registration id to the tracking lib
            Tracking.shared().setPushRegistrationId(regId);
		    return regId;
		}

	private void setRegistrationId(String user, String regId){
	    int appVersion = Const.getVersionCode();
		SharedPreferences.Editor spe = ProgNPrefs.getIns().getSharedPrefsEditor();
	    
	    spe.putString(USER, user);
	    spe.putString(REG_ID, regId);
	    spe.putInt(APP_VERSION, appVersion);
	    spe.putLong(EXPIRATION, System.currentTimeMillis() + EXPIRATION_TIME_MS);
	    
	    spe.commit();
	}
	
	private void storeRegistrationIdOnDb(){
		//changed my mind, send it every time possible, in case the player use many devices
//		SharedPreferences sh = ProgNPrefs.getIns().getSharedPrefs();
//		if (sh.getBoolean(SAVED_ONLINE, false) == false) 
			new Thread(){
				public void run(){
					sendIdToServer(m_userId, m_regId);    	    	
				}
    		}.start();
	}
	
	private boolean sendIdToServer(String user, String gcmId){
		boolean saved = false;
		final String URL = "http://skeleton.byethost13.com", URI = "/colorflooded/index.php", SERVER_URL = URL+URI;
		HashMap<String,String> properties = new HashMap<String,String>();
		properties.put("action","register_push_id");
		properties.put("user_id",user);
		properties.put("gcm_id",gcmId);
		String playerName = GameActivity.getActivity().getPlayerName();
		if(playerName != null)
			properties.put("player_name", playerName);
		
        //set the recently gotten registration id to the tracking lib
        Tracking.shared().setPushRegistrationId(gcmId);
		
		try {
			ServerCom request = ServerCom.shared();
			String response = request.getResponse(request.openConnection(ServerCom.Method.POST, SERVER_URL, properties));
			Log.i(TAG, "Response from server saving id: "+response);
			saved = response.equals("Registration: 1");
			ProgNPrefs.getIns().getSharedPrefsEditor().putBoolean(SAVED_ONLINE, saved).commit();
		} catch (Exception e) {
			Log.e(TAG, "Error sending registration id to local server: ");
			e.printStackTrace();
		}
		return saved;
	}
	private class RegisterToGcmTask extends AsyncTask<String,Integer,String> {
		@Override
		protected String doInBackground(String... params){
           String msg = "";
           
           try{
               if (m_gcm == null){
            	   m_gcm = GoogleCloudMessaging.getInstance(GameActivity.getActivity());
               }
               
               //Register in GCM servers
               String regId = m_gcm.register(SENDER_ID);
               Log.v(TAG, "Registered in GCM: registration_id=" + regId);

               //Send register id to our server
               boolean registered = sendIdToServer(params[0], regId);

        	   //save on preferences
               if(registered){
            	   setRegistrationId(params[0], regId);
               }
           } catch (IOException ex) {
        	   Log.v(TAG, "Error registering in GCM:" + ex.getMessage());
           }
           
           return msg;
       }
	}
}
