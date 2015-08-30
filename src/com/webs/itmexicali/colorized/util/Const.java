package com.webs.itmexicali.colorized.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.webs.itmexicali.colorized.BuildConfig;
/** Class containing constants and static methods accessible across the app*/ 
public class Const {

	//Debug variable
	public final static boolean D = BuildConfig.DEBUG;
	public final static boolean CHEATS = D ;
	
	//Tag for debugging
	public final static String TAG = "Colorized";
	
	/** Read from file.
	 * @param fileName the file to read from*/
	public static String getFileContent2(String fileName) {
		String output = null;
        File file = new File(fileName);
        FileInputStream fin = null;
        try {
        	if (!file.exists()){//if the file doesn't exist return null;
        		return output;
        	}
        	
            fin = new FileInputStream(file);// create FileInputStream object
            
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
}
