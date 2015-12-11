package com.qddagu.app.meetreader.util;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5 {
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',  
        'A', 'B', 'C', 'D', 'E', 'F' };  
	private static String toHexString(byte[] b) {  
	    //String to  byte  
	    StringBuilder sb = new StringBuilder(b.length * 2);    
	    for (int i = 0; i < b.length; i++) {    
	        sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);    
	        sb.append(HEX_DIGITS[b[i] & 0x0f]);    
	    }    
	    return sb.toString();    
	}  
	public static String encode(String str) {
		String md5 = "";
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(str.getBytes());
			byte messageDigest[] = digest.digest();  
			md5 = toHexString(messageDigest);  
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			md5 = URLEncoder.encode(str);
		}
		return md5;
	}
}
