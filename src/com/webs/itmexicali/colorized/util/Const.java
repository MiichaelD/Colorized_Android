package com.webs.itmexicali.colorized.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.webs.itmexicali.colorized.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/** Class containing constants and static methods accessible across the app*/ 
public class Const {

	//Debug variable
	public final static boolean D = true;
	
	public final static boolean CHEATS = D && true;
	
	//Tag for debugging
	public final static String TAG = "Colorized";
	
	
	// Game board constants
	/** Game modes*/
	public final static int STEP = 0, CASUAL = 1, TOTAL_MODES =2;
	
	/** Board Sizes*/
	public final static int SMALL = 0, MEDIUM =1, LARGE = 2, TOTAL_SIZES = 3;
	
	/** Board constants*/
	public final static int MOV_LIMS[]={21,34,44}, BOARD_SIZES[]={12,18,24};
	
	public final static int BOARD_NAMES_IDS[]={R.string.options_easy, R.string.options_med, R.string.options_hard};
	
	public final static String COLOR_NAMES[] = {"RED", "BLUE", "YELLOW", "PURPLE", "GRAY", "GREEN"};
	
    /** For HEXStrings <-> ByteArrays conversions*/
    private final static char[] hexChars={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    
    /** Request codes we use when invoking an external activity*/
    public static final int RC_RESOLVE = 5000, RC_UNUSED = 5001, RC_SHARE=7427;
    
    
    //Log\.([a-z]) -> Const.$1
    /** Android logging, only prints out if debug variable is set to true*/
    public static void v(String tag, String msg){
    	if(D)Log.v(tag, msg);
    }
    
    public static void d(String tag, String msg){
    	if(D)Log.d(tag, msg);
    }
    
    public static void i(String tag, String msg){
    	if(D)Log.i(tag, msg);
    }
    
    public static void w(String tag, String msg){
    	if(D)Log.w(tag, msg);
    }
    
    public static void e(String tag, String msg){
    	if(D)Log.e(tag, msg);
    }
    
    
    
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
    	case '0':    	case '1':    	case '2':
    	case '3':    	case '4':    	case '5':
    	case '6':    	case '7':    	case '8':
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
