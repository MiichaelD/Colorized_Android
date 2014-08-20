package com.webs.itmexicali.colorized.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

/** Class containing constants and static methods accessible across the app*/ 
public class Const {

	//Debug variable
	public final static boolean D = true;
	
	//Tag for debugging
	public final static String TAG = "Colorized";
	
	//Board constants
	public final static int mov_limit[]={21,34,44};
	public final static int board_sizes[]={12,18,24};
	
	//Game modes
	public final static int STEP = 0, CASUAL = 1;
	
	//AdMob Advertising
    /** Your ad unit id. Replace with your actual ad unit id. */
    public static final String
    	ADVIEW_AD_UNIT_ID 		= "ca-app-pub-4741238402050454/6518301004",
    	INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-4741238402050454/7995034204" ;


    private static char[] hexChars={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

    public static String AES_ALG = "AES ";
    
    /** Hide Action Bar in devices that support it */
	@SuppressLint("InlinedApi")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void setFullScreen(Activity ac){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ac.getActionBar().hide();
			
			try{

				View decorView = ac.getWindow().getDecorView();
				// Hide both the navigation bar and the status bar.
				// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
				// a general rule, you should design your app to hide the status bar whenever you
				// hide the navigation bar.
				int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
						View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		
				decorView.setSystemUiVisibility(uiOptions);
			}catch(NoSuchMethodError e){ /* some devices have not defined this method*/}
		}
		
		ac.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,
		WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	
	
	/** Read from file.
	 * @param fileName the file to read from*/
	public static String getFileContent(String fileName) {
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

	/** Return a HEX String generated from the byte array*/
	public static String byteArrayToHexString(byte[] toHex) {		
        StringBuilder hexString = new StringBuilder();
        for (byte b:toHex) {
            hexString.append(byteToHexString(b));
        }
        return hexString.toString();
    }

	/** Append string representation of a byte*/
    private static String byteToHexString(byte b) {
        int unsigned_byte = b < 0 ? b + 256 : b;
        int hi = unsigned_byte / 16;
        int lo = unsigned_byte % 16;
        return String.format("%c%c", hexChars[hi],hexChars[lo]);
    }
    
    /** Return a new String from the byte array*/
    public static byte[] HexStringToByte(String in) {
    	int len = in.length();
    	if(len < 2) 
    		return null;
    	
    	byte[] ret = new byte[len/2];
    	
    	for(int i =0;i<len;i+=2){
    		ret[i/2] = (byte)(charToByte(in.charAt(i))<<4 | charToByte(in.charAt(i+1)));
    	}
    	
        return ret;
    }
	
    /** Return half byte (4bits) value from a HEXADECIMAL char*/
    private static byte charToByte(char c){
    	switch(c){
    	case '0':
    	case '1':
    	case '2':
    	case '3':
    	case '4':
    	case '5':
    	case '6':
    	case '7':
    	case '8':
    	case '9':
    		return (byte)(c-'0');
    	case 'A':
    		return 10;
    	case 'B':
    		return 11;
    	case 'C':
    		return 12;
    	case 'D':
    		return 13;
    	case 'E':
    		return 14;
    	case 'F':
    		return 15;
		default:
			return 0;
    	}
    }
	
}
