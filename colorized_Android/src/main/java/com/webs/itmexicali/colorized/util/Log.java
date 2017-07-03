package com.webs.itmexicali.colorized.util;

/**
 * 
 * Wrapper for Androids logging mechanism.
 * 
 * While researching android's logging mechanics, logging seems to be hard to
 * turn off. This class is intended to turn it on or off easily.
 * 
 * Verbose should never be compiled into an application except during
 * development. Debug logs are compiled in but stripped at runtime. Error,
 * warning and info logs are always kept.
 * 
 * As per http://stackoverflow.com/questions/2018263/android-logging/2019002#2019002
 */
public class Log {
    //Const\.([a-z]\() -> Log.$1
    public enum LogLevel{ kVerbose, kDebug, kInfo, kWarn, kError, kAssert, kNone; };

    /**
     * Common way is make a LogLevel variable, and define it as Debug
     * This will show all the logs of level Debug and up
     * 
     * By default this should be INFO or above.
     * 
     * Lower levels will give more details but will also clutter up the LogCat
     * output.
     */
    private static  LogLevel m_logLevel = LogLevel.kVerbose;
    
    /** In case we want to change the logging level at runtime
     * @param level the new log level to be used*/
    public static void changeLogLevel(LogLevel level){
    	m_logLevel = level;
    }

    public static void v(String tag, Object msg) {
        if (m_logLevel.compareTo(LogLevel.kVerbose) <= 0)
            android.util.Log.v(tag, msg.toString());
    }

    public static void d(String tag, Object msg) {
        if (m_logLevel.compareTo(LogLevel.kDebug) <= 0)
        	android.util.Log.d(tag, msg.toString());
    }

    public static void i(String tag, Object msg) {
        if (m_logLevel.compareTo(LogLevel.kInfo) <= 0)
        	android.util.Log.i(tag, msg.toString());
    }

    public static void w(String tag, Object msg) {
        if (m_logLevel.compareTo(LogLevel.kWarn) <= 0)
        	android.util.Log.w(tag, msg.toString());
    }

    public static void e(String tag, Object msg) {
        if (m_logLevel.compareTo(LogLevel.kError) <= 0)
        	android.util.Log.e(tag, msg.toString());
    }
    
    public static void wtf(String tag, Object msg) {
        if (m_logLevel.compareTo(LogLevel.kAssert) <= 0)
        	android.util.Log.wtf(tag, msg.toString());
    }
}