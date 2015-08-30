package com.webs.itmexicali.colorized.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

public class LocationHelper {

    private static final String TAG = LocationHelper.class.getSimpleName();
    private Context mContext = null;

    /** Androids location manager*/
    private LocationManager m_locMan = null;
    private final String m_locProviders[] = {LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER};
    
    protected float MIN_ACCURACY = 35; //25 meters;
    protected long MAX_TIME_REQUESTING_UPDATES = 30000; //30 secs
    protected long MIN_TIME_BETWEEN_UPDATES = 750; // 1 sec, time to wait between updates
    protected float MIN_DISTANCE_BETWEEN_UPDATES = 0f; // 0 m, this is, give me any update even if the distance hasn't change

    public LocationHelper(Context context) {
        mContext = context;
        if (mContext != null) {
            m_locMan = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    LocationListener[] mLocationListeners =  new LocationListener[] {
                    new LocationListener(m_locProviders[0]), new LocationListener(m_locProviders[1]) };

    private class LocationListener implements android.location.LocationListener {
        Location m_lastLoc;
        boolean m_valid = false;
        String m_provider;

        public LocationListener(String provider) {
            m_provider = provider;
            m_lastLoc = new Location(m_provider);
        }

        public void onLocationChanged(Location newLocation) {
        	// Hack to filter out 0.0,0.0 locations
            if (newLocation == null || (newLocation.getLatitude() == 0.0 && newLocation.getLongitude() == 0.0)) {
                return;
            }

            newLocation.setTime(System.currentTimeMillis());
            if (newLocation.hasAccuracy() && newLocation.getAccuracy() < MIN_ACCURACY){
            	stopRequestingUpdates();
            }
            
            m_lastLoc.set(newLocation);
            m_valid = true;
            Log.v(TAG, "onLocationChanged for provider: "+m_provider);
        }

        public void onProviderEnabled(String provider) {
        	m_valid = true;
        }

        public void onProviderDisabled(String provider) {
            m_valid = false;
            stopRequestingUpdates();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (status == LocationProvider.OUT_OF_SERVICE) {
                m_valid = false;
            }
        }

        public Location current() {
            return m_valid ? m_lastLoc : null;
        }
        
        private void stopRequestingUpdates(){
            try {
            	Log.v(TAG, m_provider+ ": StopRequestingUpdates because we have reached the minimum accuracy threshold");
                LocationHelper.this.m_locMan.removeUpdates(LocationListener.this);
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    };
    
    /** Request location updates for MAX_TIME_REQUESTING_UPDATES ms long, when this
     * time has been accomplished stop location subscription.*/
    public void startTimedLocationRequests(){
    	Thread task = new Thread(){
    		public void run(){
    			startLocationReceiving();
    			try{
    				Thread.sleep(MAX_TIME_REQUESTING_UPDATES);
    			} catch (InterruptedException ie){ ie.printStackTrace();}
    			stopLocationReceiving();
    		}
    	};
    	task.start();
    }
    
    /**Request location updates each time the MIN_TIME_BETWEEN_UPDATES (ms)has passed
     * or the location has changed for MIN_DISTANCE_BETWEEN_UPDATES (m) */
    public void startLocationReceiving() {
        if (this.m_locMan != null) {
        	int providerIndex = 0;
        	for (String provider : m_locProviders){
	            try {
	            	if (m_locMan.isProviderEnabled(provider)){
	            		m_locMan.requestLocationUpdates(provider, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_BETWEEN_UPDATES, mLocationListeners[providerIndex]);
	            	}
	            } catch (java.lang.SecurityException ex) {
	            	Log.e(TAG, "SecurityException when subscribing to provider: "+provider+", error:" + ex.getMessage());
	            	ex.printStackTrace();
	            } catch (IllegalArgumentException ex) { ex.printStackTrace();}
	            ++providerIndex;
        	}
        }
    }

    /** Stop requesting updates from providers we subscribed to*/
    public void stopLocationReceiving() {
        if (m_locMan != null) {
            for (int i = 0; i < this.mLocationListeners.length; i++) {
                try {
                	Log.v(TAG, m_locProviders[i]+": StopRequestingUpdates because max time has passed");
                    m_locMan.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {ex.printStackTrace(); }
            }
        }
    }

    /** Get the current location from the most accurate provider*/
    public Location getCurrentLocation() {
        Location loc = null;

        // go from best to worst order
        for (int i = 0; i < this.mLocationListeners.length; i++) {
            loc = this.mLocationListeners[i].current();
            if (loc != null && (loc.getLatitude() != 0f && loc.getLongitude() != 0f))
                break;
        }

        return loc;
    }
    
    /** Iterate thru location providers to find the last known location.*/
    public Location getLastKnownLocation(){
    	for(String provider : m_locProviders){
    		Location loc = m_locMan.getLastKnownLocation(provider);
    		
    		//Don't return default 0,0 values sometimes seen
            if (loc == null || (loc.getLatitude() == 0f && loc.getLongitude() == 0f))
            	continue;
            
            return loc;
    	}
    	return null;
    }

}
