package com.webs.itmexicali.colorized.util;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

public class Tracking {

	private static Tracking instance = null;

	MixpanelAPI m_mixpanel = null;
	
	public static Tracking shared(){
		if (instance == null)
			instance = new Tracking();
		return instance;
	}
	
	public void init(Context context, String token) {
		// Initialize the Mixpanel library for tracking and push notifications.
  		// We also identify the current user with a distinct ID
  		m_mixpanel = MixpanelAPI.getInstance(context, token);
  		// set user id, this can be different ID from event id, but meh!
  		// that will be used for people analytics. You must set this explicitly in order
        // to dispatch people data.
  		m_mixpanel.getPeople().identify(m_mixpanel.getDistinctId()); 
        // People analytics must be identified separately from event analytics.
        // The data-sets are separate, and may have different unique keys (distinct_id).
        // We recommend using the same distinct_id value for a given user in both,
        // and identifying the user with that id as early as possible.
	}
	
	/** Get the tracking service.
	 * @throws IllegalStateException if the tracking service hasn't been initialized*/
	public MixpanelAPI getService(){
		if (m_mixpanel == null)
			throw new IllegalStateException("Init tracking service before using it");
		return m_mixpanel;
	}
	

	/** Since this service can handle push notifications, we let it know the current registration*/
	public void setPushRegistrationId(String regId) {
        m_mixpanel.getPeople().setPushRegistrationId(regId);
	}
	
	/** If you have surveys or notifications, and you have set AutoShowMixpanelUpdates set to false,
         the onResume function is a good place to call the functions to display surveys or
         in app notifications. It is safe to call both these methods right after each other,
         since they do nothing if a notification or survey is already showing. 
	 */
	public void onResume(Activity act){
		m_mixpanel.getPeople().showNotificationIfAvailable(act);
		m_mixpanel.getPeople().showSurveyIfAvailable(act);
		m_mixpanel.track("Foreground");
		m_mixpanel.timeEvent("Background");
	}
	
	/** To preserve battery life, the Mixpanel library will store  events rather than send
		 them immediately. This means it is important to call flush() to send any unsent events
         before your application is taken out of memory. 
	 */
	public void onPause(){
		m_mixpanel.flush();
		m_mixpanel.track("Brackground");
	}
	
	public void onPlayerIdUpdated(String playerId){
		m_mixpanel.alias(playerId, null);
		m_mixpanel.getPeople().identify(playerId);
		
		setPlayerProperty("$user_id", playerId);
		incrementPlayerProperty("Signed in", 1);
		
		Map<String, Object> props = new HashMap<String, Object>();
		props.put("player_id", playerId);
		registerSuperProperties(props);
	}
	
	public void setPlayerProperty(String property, String value){
		final MixpanelAPI.People people = m_mixpanel.getPeople();
		people.set(property, value);
	}
	
	public void incrementPlayerProperty(String property, long value){
		final MixpanelAPI.People people = m_mixpanel.getPeople();
		people.increment(property, value);
	}
	
	public void registerSuperPropertiesOnce(Map<String, Object> map){
		m_mixpanel.registerSuperPropertiesOnceMap(map);
	}
	
	public void registerSuperProperties(Map<String, Object> map){
		m_mixpanel.registerSuperPropertiesMap(map);
	}
	
	public void track(String event, Map<String, Object> properties){
		m_mixpanel.trackMap(event, properties);
	}
	
	
	// ------------------------------------ SPECIAL EVENTS --------------------------------------- //
	public void updateVersion(String ver_str, int ver_int){
		Map<String, Object> props = new HashMap<String, Object>();
		props.put("version_str", ver_str);
		props.put("version_int", ver_int);
		registerSuperProperties(props);
	}
	//TODO improve:
	/*
	 * GameOverState 223
	 * GameState 198, 569
	 * MainState 79, 92, 102
	 * OptionState 228
	 * TutoState 65
	 * ProgNPrefs 66
	 */
}
