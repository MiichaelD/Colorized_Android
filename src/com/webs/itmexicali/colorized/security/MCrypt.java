package com.webs.itmexicali.colorized.security;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
//import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.webs.itmexicali.colorized.util.Const;

/** */
public class MCrypt {
	
        //private IvParameterSpec ivspec;
        private SecretKeySpec keyspec;
        private Cipher cipher;

        private String SecretKey = "COLORIZEDAES0814";
        private String iv = "AES0814DEZIROLOC";
        
        private static MCrypt pInstance	= null;
        
        public static MCrypt getIns(){
        	if(pInstance == null)
        		pInstance = new MCrypt();
        	return pInstance;
        }
        
        public static void delIns(){
        	pInstance = null;
        }
        
        public MCrypt(){
	        try {
	        		//ivspec = new IvParameterSpec(iv.getBytes("UTF-8"));
	                keyspec = new SecretKeySpec(SecretKey.getBytes("UTF-8"), "AES");
	                cipher = Cipher.getInstance("AES");
	        } catch (NoSuchAlgorithmException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	        } catch (NoSuchPaddingException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	        } catch (UnsupportedEncodingException e) {
	        	//ivspec = new IvParameterSpec(iv.getBytes());
	            keyspec = new SecretKeySpec(SecretKey.getBytes(), "AES");
				e.printStackTrace();
			}
        }

        public byte[] encrypt(byte[] text){
	        byte[] encrypted = null;
	        try {
	        	if(text == null || text.length == 0)
	                throw new Exception("Empty string");
	
	            cipher.init(Cipher.ENCRYPT_MODE, keyspec/*, ivspec*/);
	            encrypted = cipher.doFinal(text);
	        } catch (Exception e){                       
	        	e.printStackTrace();
	        }
	        return encrypted;
        }

        public byte[] decrypt(byte[] code){
            byte[] decrypted = null;
            try {
                if(code == null || code.length == 0)
                	throw new Exception("Empty string");

                cipher.init(Cipher.DECRYPT_MODE, keyspec/*, ivspec*/);

                decrypted = cipher.doFinal(code);
            } catch (Exception e){
                  e.printStackTrace();
            }
            return decrypted;
        }
        
        public byte[] encrypt(String text){
        	try{
        		return encrypt(text.getBytes("UTF-8"));
        	}catch(UnsupportedEncodingException e){
        		return encrypt(text.getBytes());
        	}
        }
        
        public byte[] decrypt(String code){
        	try{
        		return decrypt(code.getBytes("UTF-8"));
        	}catch(UnsupportedEncodingException e){
        		return decrypt(code.getBytes());
        	}
        }
        
        public static void test(){
    		try {
    			MCrypt mc= new MCrypt();
    			
    			byte[] arr=mc.SecretKey.getBytes();
        		System.out.println("bits:"+8*arr.length);
        				
        				arr=mc.iv.getBytes();
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
