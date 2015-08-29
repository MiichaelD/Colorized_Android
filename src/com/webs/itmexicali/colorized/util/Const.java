package com.webs.itmexicali.colorized.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.webs.itmexicali.colorized.BuildConfig;
import com.webs.itmexicali.colorized.GameActivity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

/** Class containing constants and static methods accessible across the app*/ 
public class Const {

	//Debug variable
	public final static boolean D = BuildConfig.DEBUG;
	
	public final static boolean CHEATS = D ;
	
	//Tag for debugging
	public final static String TAG = "Colorized";
	
	private static String pLocale = null;
	
	// version name and code
	private static String pVersionName = null;
	private static int pVersionCode = 0;
	
    /** Request codes we use when invoking an external activity*/
    public static final int RC_RESOLVE = 5000, RC_UNUSED = 5001, RC_SHARE=7427;
	
	/** Read from file.
	 * @param fileName the file to read from*/
	public static String getFileContent2(String fileName) {
		String output = null;
        File file = new File(fileName);
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);// create FileInputStream object
            
			if(!file.exists()){//if the file doesn't exist return null;
				fin.close();
				return output;
			}
			
            byte fileContent[] = new byte[(int)file.length()];
             
            // Reads up to certain bytes of data from this input stream into an array of bytes.
            fin.read(fileContent);
            
            output = new String(fileContent);//create string from byte array
            System.out.println("File content: " + output);
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found" + e);
        }
        catch (IOException ioe) {
            System.out.println("Exception while reading file " + ioe);
        }
        finally {
            // close the streams using close method
            try {
                if (fin != null) {
                    fin.close();
                }
            }
            catch (IOException ioe) {
                System.out.println("Error while closing stream: " + ioe);
            }
        }
        return output;
    }
    
    public static void updateVersionInfo(Context appContext){
		PackageInfo pi = null;
		try{
			pi =GameActivity.instance.getPackageManager().getPackageInfo(GameActivity.instance.getPackageName(), 0);
		}catch(NameNotFoundException e){
			pVersionName = "0.0.0";
			pVersionCode = 0;
		}
		
		if(pVersionName == null && pi != null){
			pVersionName = pi.versionName;
			pVersionCode = pi.versionCode;
		}	
	}
	
	public static String getVersionName(){
		return pVersionName;
	}
	
	public static int getVersionCode(){
		return pVersionCode;
	}
	
	public static String getLocale(Context appContext) {
		if (pLocale == null)
			pLocale = appContext.getResources().getConfiguration().locale.toString();
		return pLocale;
    }
}
