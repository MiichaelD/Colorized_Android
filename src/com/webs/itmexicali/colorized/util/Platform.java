package com.webs.itmexicali.colorized.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

public class Platform {
	// version name and code
	private static String m_appVersionName = null;
	private static int m_appVersionCode = -1;
	private static String m_locale = null;
	
	private static HashMap<String, String> m_cacheType = new HashMap<String, String>();
	
    private static final long NETWORK_CACHE_MS = 10000l;// = 1 / 6 * 1000l * 60l; //1 sexto de 1 minuto = 10 segs
    public static final String NO_NETWORK = "NO_NETWORK";
//    private static final String TAG = Platform.class.getSimpleName();
	
	//Android context
	private static Context m_context;
	
	public static void init(Context ctx){
		m_context = ctx;
	}
	
	public static Context getContext(){
		return m_context;
	}
	
	public static String getPlatform() {
        return "Android";
    }

    public static String getVersionName() {
        String release = android.os.Build.VERSION.RELEASE;
        return release;
    }
    
    public static int getVersionCode() {
    	return android.os.Build.VERSION.SDK_INT;
    }
    
    public static void updateAppVersionInfo(){
		PackageInfo pi = null;
		try{
			pi = m_context.getPackageManager().getPackageInfo(m_context.getPackageName(), 0);
		}catch(NameNotFoundException e){
			m_appVersionName = "0.0.0";
			m_appVersionCode = 0;
		}
		
		if(m_appVersionName == null && pi != null){
			m_appVersionName = pi.versionName;
			m_appVersionCode = pi.versionCode;
		}	
	}
	
	public static String getAppVersionName(){
		if (m_appVersionName == null)
			updateAppVersionInfo();
		return m_appVersionName;
	}
	
	public static int getAppVersionCode(){
		if (m_appVersionCode == -1)
			updateAppVersionInfo();
		return m_appVersionCode;
	}
	
	/** Get device's locale (Language_Country)
	 * http://stackoverflow.com/questions/4212320/get-the-current-language-in-device*/
	public static String getLocale() {
		if (m_locale == null)
			m_locale = m_context.getResources().getConfiguration().locale.toString();
		return m_locale;
    }
	
	/** Get device's model*/
	public static String getDevice() {
        String model = android.os.Build.MODEL;
        return model;
    }
	
	/** Get IP address*/
	public static String getIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) { }
        return null;
    }
	
	/**Get telephone network carrier
     * http://stackoverflow.com/questions/3838602/how-to-find-out-carriers-name-in-android */
    public static String getCarrier(final Context appContext) {
        TelephonyManager manager = (TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE);
        String carrier = manager == null ? null : manager.getNetworkOperatorName();
        return carrier;
    }
    
    /** Get network type
     * @returns network_type WIFI, MOBILE or NO_NETWORK*/
    public static String getNetworkType(){
        if (m_cacheType.get("last modified time") != null && (System.currentTimeMillis() - Long.parseLong( m_cacheType.get("last modified time")) < NETWORK_CACHE_MS)) {
            return (String) m_cacheType.get("networkType");
        }
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) m_context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
	            String networkInfoTypeName = activeNetworkInfo.getTypeName();
	            m_cacheType.put("networkType", networkInfoTypeName);
	            m_cacheType.put("last modified time", String.valueOf(System.currentTimeMillis()));
	            return networkInfoTypeName;
            }
        } catch (SecurityException ise) { ise.printStackTrace(); }
        
        m_cacheType.put("networkType", NO_NETWORK);
        m_cacheType.put("last modified time", String.valueOf(System.currentTimeMillis()));
        return NO_NETWORK;
    }
    
    /**Get WiFi info if any is active 
     * @return info contained in WifiInfo or null if no active connection is present*/
    public static String getWifiInfo(final Context appContext) {
        try {
            WifiManager wifiManager = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            return wifiInfo.toString();
        } catch (SecurityException ise) { ise.printStackTrace(); }
        return null;
    }
}
