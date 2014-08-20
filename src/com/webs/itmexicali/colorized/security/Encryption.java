package com.webs.itmexicali.colorized.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.webs.itmexicali.colorized.util.Const;

import android.annotation.SuppressLint;

public class Encryption {
	
	/**************************************** ENCODER ******************************************/
	@SuppressLint("TrulyRandom")
	/** Generate a secret key, if any problem happens the key returned will be
	 * the password parameter*/
	public static byte[] generateKey(String password){
		try{
		    byte[] keyStart = password.getBytes("UTF-8");
	
		    KeyGenerator kgen = KeyGenerator.getInstance("AES");
		    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG","Crypto");
		    sr.setSeed(keyStart);
		    kgen.init(128, sr);
		    //kgen.init(128);
		    
		    SecretKey skey = kgen.generateKey();
		    return skey.getEncoded(); 
		}catch(Exception e){
	    	e.printStackTrace();
	    	return password.getBytes();
	    }
	}

	public static byte[] encode(byte[] key, String fileData){
		try{
			return encode(key,fileData.getBytes("UTF-8"));
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static byte[] encode(byte[] key, byte[] fileData){
		return cipherUtil(true,key,fileData);
    }

	public static byte[] decode(byte[] key, String fileData){
    	try{
			return decode(key,fileData.getBytes("UTF-8"));
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
			return null;
		}
    }
	
    public static byte[] decode(byte[] key, byte[] fileData){
    	return cipherUtil(false,key,fileData);
    }

    /** Method to Encode/Decode the file data using the given key*/
    private static byte[] cipherUtil(boolean encode, byte[] key, byte[] fileData){
    	try{
	        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
	        Cipher cipher = Cipher.getInstance("AES");
	        cipher.init(encode?Cipher.ENCRYPT_MODE:Cipher.DECRYPT_MODE, skeySpec);
	
	        return cipher.doFinal(fileData);
    	}catch(Exception e){
    		e.printStackTrace();
			return null;
    	}
    }
    
    /**************************************** ENCODER ******************************************/
	
	/** Encrypt SHA1, this is one way encryption, so no way to decode the result
	 * you can only compare it with other encrypted strings to check they are the same*/
	public static byte[] encryptSHA1toByteArray(String toEncrypt){
		try{
			return MessageDigest.getInstance("SHA1").digest(toEncrypt.getBytes("UTF-8"));
		}catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch(UnsupportedEncodingException ex1){
        	ex1.printStackTrace();
        }
		return null;
	}
	
	public static void test(){
		try {
			/*
			java.security.Provider[] pvdr = (java.security.Security.getProviders());
			for(java.security.Provider i:pvdr){
				System.out.println("Provider: "+i.getName()+",info:"+i.getInfo());
			}
			String st1 = Encryption.generateKey("boardSize");
			String st2 = Encryption.generateKey("boardSize");//Const.generateKey("boardSize");
			String st3 = Encryption.generateKey("boardsize");
			System.out.println(st1);
			System.out.println(st2);
			System.out.println("are st1 and st2 equal?"+st1.equals(st2));
			System.out.println(st3);
			System.out.println("are st1 and st3 equal?"+st1.equals(st3));
			*/
			//Security.addProvider(new Crypto());
			
			String toEncode = "AES:21 12 3 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 0 5 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 0 1 5 2 1 1 1 1 1 1 1 4 1 1 0 2";
			System.out.println("toEncode: "+toEncode);
			System.out.println("toEncode: "+Const.byteArrayToHexString(toEncode.getBytes()));
			
			byte[] key = Encryption.generateKey("coloRized");
			System.out.println("ourKey: "+Const.byteArrayToHexString(key));
			System.out.println("ourKey: "+new String(key));
			
			byte[] encrypted = Encryption.encode(key, toEncode);
			System.out.println("encrypted: "+Const.byteArrayToHexString(encrypted));
			
			byte[] decrypted = Encryption.decode(key, encrypted);
			System.out.println("decrypted: "+Const.byteArrayToHexString(decrypted));
			System.out.println("decrypted: "+new String(decrypted));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

