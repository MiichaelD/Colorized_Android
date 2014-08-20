package com.webs.itmexicali.colorized.security;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
//import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.webs.itmexicali.colorized.util.Const;

/** Encrypt / Decrypt Between Android, PC-JAVA and PHP and vice-versa
 * https://github.com/serpro/Android-PHP-Encrypt-Decrypt
 * https://github.com/MiichaelD/Android-PHP-Encrypt-Decrypt*/
public class MCrypt {        

	private boolean pEncrypt = true;
    private int keyIndex = 0;

    //prefix indicating the algorithm used
    public static String AES_PREF = "AES ";
    public static int MAIN_IND = 0, FIN_GAM_IND = 1, WON_GAM_IND = 2;
    
    private String SecretKeys[] = {"COLORIZEDAES0814","COLORIZEDAES0148","COLORIZEDAES1408","COLORIZEDAES1084","COLORIZEDAES0184"};
    //private String iv = "AES0814DEZIROLOC";

    //private IvParameterSpec ivspec;
    private SecretKeySpec keyspec[];
    private Cipher cipher;
    
    private String charset = "UTF8";

    private static MCrypt pInstance	= null;
    
    public static MCrypt getIns(){
    	if(pInstance == null)
    		pInstance = new MCrypt();
    	return pInstance;
    }
    
    public static void delIns(){
    	//let garbage collector do it's job
    	pInstance = null;
    }
    
    public MCrypt(){
        try {
        	keyIndex = MAIN_IND;
        	//ivspec = new IvParameterSpec(iv.getBytes("charset"));
        	keyspec = new SecretKeySpec[SecretKeys.length];
        	for(int i=0;i<SecretKeys.length;i++){
                keyspec[i] = new SecretKeySpec(SecretKeys[i].getBytes(charset), "AES");
        	}
        	cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
    		pEncrypt = false;
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
        	pEncrypt = false;
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
        	//ivspec = new IvParameterSpec(iv.getBytes());
        	for(int i=0;i<SecretKeys.length;i++){
                keyspec[i] = new SecretKeySpec(SecretKeys[i].getBytes(), "AES");
        	}
			e.printStackTrace();
		}
    }
    
    /** Choose what index to use*/
    public void setSecretKeyIndex(int index){
    	if (index < 0 || index >= SecretKeys.length)
    		keyIndex = MAIN_IND;
    	else
    		keyIndex = index;
    }
    
    public void resetSecretKeyIndex(){
    	keyIndex = MAIN_IND;
    }

    public byte[] encrypt(byte[] text){
        if(!pEncrypt)
        	return text;
        
        byte[] encrypted = null;
        try {
        	if(text == null || text.length == 0)
                throw new Exception("Empty string");

            cipher.init(Cipher.ENCRYPT_MODE, keyspec[keyIndex]/*, ivspec*/);
            encrypted = cipher.doFinal(text);
        } catch (Exception e){
        	//TODO remove for release
            e.printStackTrace();
        }
        return encrypted;
    }

    public byte[] decrypt(byte[] code){
    	if(!pEncrypt)
        	return code;
    	
        byte[] decrypted = null;
        try {
            if(code == null || code.length == 0)
            	throw new Exception("Empty string");

            cipher.init(Cipher.DECRYPT_MODE, keyspec[keyIndex]/*, ivspec*/);

            decrypted = cipher.doFinal(code);
        } catch (Exception e){
        	//TODO remove for release
            e.printStackTrace();
        }
        return decrypted;
    }
    
    public byte[] encrypt(String text){
    	try{
    		return encrypt(text.getBytes(charset));
    	}catch(UnsupportedEncodingException e){
    		return encrypt(text.getBytes());
    	}
    }
    
    public byte[] decrypt(String code){
    	try{
    		return decrypt(code.getBytes(charset));
    	}catch(UnsupportedEncodingException e){
    		return decrypt(code.getBytes());
    	}
    }
    
    public static void test(){
		try {
			MCrypt mc= new MCrypt();
			
			byte[] arr=mc.SecretKeys[0].getBytes();
    		System.out.println("bits:"+8*arr.length);
    	
			
			String toEncode = "AES:21 12 3 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 0 5 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 0 1 5 2 1 1 1 1 1 1 1 4 1 1 0 2";
			System.out.println("toEncode: "+toEncode);
			System.out.println("toEncode: "+Const.byteArrayToHexString(toEncode.getBytes()));
			
			byte[] encrypted = mc.encrypt(toEncode);
			System.out.println("encrypted: "+Const.byteArrayToHexString(encrypted));
			
			byte[] decrypted = mc.decrypt(encrypted);
			System.out.println("decrypted: "+Const.byteArrayToHexString(decrypted));
			System.out.println("decrypted: "+new String(decrypted));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
