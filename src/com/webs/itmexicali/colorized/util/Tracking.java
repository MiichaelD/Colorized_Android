package com.webs.itmexicali.colorized.util;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.webs.itmexicali.colorized.R;

public class Tracking {

	private static Tracking instance = null;

	MixpanelAPI m_mixpanel = null;
	
	public static Tracking shared(){
		if (instance == null)
			instance = new Tracking();
		return instance;
	}
	
	public void init(Context context) {
		// Initialize the Mixpanel library for tracking and push notifications.
  		// We also identify the current user with a distinct ID
  		m_mixpanel = MixpanelAPI.getInstance(context, context.getResources().getString(R.string.mixpanel_api_token));
  		// set user id, this can be different ID from event id, but meh!
  		// that will be used for people analytics. You must set this explicitly in order
        // to dispatch people data.
  		String currentId = m_mixpanel.getDistinctId();
  		String currentPlayerId = m_mixpanel.getPeople().getDistinctId();
		Log.i(Tracking.class.getSimpleName(), "Init - current eventId = "+currentId+", playerId = "+currentPlayerId);
		if (currentPlayerId == null)
			m_mixpanel.getPeople().identify(currentId); 
        // People analytics must be identified separately from event analytics.
        // The data-sets are separate, and may have different unique keys (distinct_id).
        // We recommend using the same distinct_id value for a given user in both,
        // and identifying the user with that id as early as possible.
  		
  		HashMap<String, Object> props = new HashMap<String, Object>();
  		props.put("First Run", System.currentTimeMillis());
  		m_mixpanel.registerSuperPropertiesOnceMap(props);
  		
  		props.clear();
  		props.put("Last Run", System.currentTimeMillis());
  		m_mixpanel.registerSuperPropertiesMap(props);
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
	}
	
	/** To preserve battery life, the Mixpanel library will store  events rather than send
		 them immediately. This means it is important to call flush() to send any unsent events
         before your application is taken out of memory. 
	 */
	public void onPause(){
		m_mixpanel.flush();
	}
	
	public void onPlayerIdUpdated(String playerId){
		String Id = m_mixpanel.getDistinctId();
		String PlayerId = m_mixpanel.getPeople().getDistinctId();
		Log.i(Tracking.class.getSimpleName(), "current eventId = "+Id+", playerId = "+PlayerId);
		m_mixpanel.alias(playerId, null);
		// so my thought was that as long as we have identified the player, all the tracking events
		// were going to appear in this profile, and I was going to keep using the default event-distinct_id 
		// so that I could notice every different device the player has used. But what really happens is that
		// the events are linked to the device, but not to the player (weird).
		m_mixpanel.identify(playerId); // this is the Event (device specific) not updating.
		m_mixpanel.getPeople().identify(playerId);
		Id = m_mixpanel.getDistinctId();
		PlayerId = m_mixpanel.getPeople().getDistinctId();
		Log.i(Tracking.class.getSimpleName(), "new eventId = "+Id+", playerId = "+PlayerId);
		
		setPlayerProperty("user_id", playerId);
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
	
	
	/** Track an event. Events have a string name, and an optional set of name/value pairs that describe
	 * the properties of that event.
     *
     * @param eventName The name of the event to send
     * @param properties A Map containing the key value pairs of the properties to include in this event.
     *                   Pass null if no extra properties exist.*/
	public void track(String event, Map<String, Object> properties){
		m_mixpanel.trackMap(event, properties);
	}
	
	/** Begin timing of an event. Calling timeEvent("Thing") will not send an event, but
     * when you eventually call track("Thing"), your tracked event will be sent with a "$duration"
     * property, representing the number of seconds between your calls.*/
	public void time(String event){
		m_mixpanel.timeEvent(event);
	}
	
	
	// ------------------------------------ SPECIAL EVENTS --------------------------------------- //
	public void updateVersion(String ver_str, int ver_int){
		Map<String, Object> props = new HashMap<String, Object>();
		props.put("version_str", ver_str);
		props.put("version_int", ver_int);
		registerSuperProperties(props);
	}
	
	public void updateLocale(String locale){
		Map<String, Object> props = new HashMap<String, Object>();
		props.put("locale", locale);
		registerSuperProperties(props);
	}

	public void onShare(String socialNetwork){
		Map<String, Object> props = new HashMap<String, Object>();
		props.put("social_net", socialNetwork);
		track("share",props);
	}
	/* GameActivity 	OK
	 * GameState        OK
	 * MainState 	 	OK
	 * OptionState 		OK
	 * ProgNPrefs 		OK
	 */
}
