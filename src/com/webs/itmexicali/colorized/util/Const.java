package com.webs.itmexicali.colorized.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.webs.itmexicali.colorized.BuildConfig;
import com.webs.itmexicali.colorized.GameActivity;
import com.webs.itmexicali.colorized.R;
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
	
	// Game board constants
	/** Game modes*/
	public final static int STEP = 0, CASUAL = 1, TOTAL_MODES =2;
	
	/** Board Sizes*/
	public final static int SMALL = 0, MEDIUM =1, LARGE = 2, TOTAL_SIZES = 3;
	
	/** Board constants*/
	public final static int MOV_LIMS[]={21,34,44}, BOARD_SIZES[]={12,18,24};
	
	public final static int BOARD_NAMES_IDS[]={R.string.options_easy, R.string.options_med, R.string.options_hard};
	
	public final static String COLOR_NAMES[] = {"RED", "BLUE", "YELLOW", "PURPLE", "GRAY", "GREEN"};
	
	public final static String GAME_MODES[] = {"STEP", "CASUAL"};
	
    /** For HEXStrings <-> ByteArrays conversions*/
    private final static char[] hexChars={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    
    /** Request codes we use when invoking an external activity*/
    public static final int RC_RESOLVE = 5000, RC_UNUSED = 5001, RC_SHARE=7427;
	
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
