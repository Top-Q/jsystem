/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * The class purpose is to give the ability to handle encrypted and decrypted Strings within JuniTest infra-structures.
 * <br>
 * The encryption is always "DES encryption" and the secret key is a static key , in other words : a specific key
 * encryption result will always be the same.
 * 
 * @author Haimm
 */

public class Encryptor {
	
	private final static byte[] SECRET_KEY = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7 };
	
	/**
	 * for verifying if a string is already encrypted
	 */
	private final static String ENCRYPTION_TERMINATING_STRING = "%&*";

	/**
	 * The method gets a String which its secret key is hard coded and encrypt it <br>   
	 * 
	 * @param str - String to encrypt
	 * @return encrypted String
	 * @throws Exception
	 */
	public static String encrypt(String str) throws Exception {
		Cipher ecipher;
		Cipher dcipher;

		SecretKeySpec key = new SecretKeySpec(SECRET_KEY, "DES");
		ecipher = Cipher.getInstance("DES");
		dcipher = Cipher.getInstance("DES");
		ecipher.init(Cipher.ENCRYPT_MODE, key);
		dcipher.init(Cipher.DECRYPT_MODE, key);

		// Encode the string into bytes using utf-8
		byte[] utf8 = str.getBytes("UTF8");

		// Encrypt
		byte[] enc = ecipher.doFinal(utf8);

		// Encode bytes to base64 to get a string
		return Base64.getEncoder().encode(enc)+ENCRYPTION_TERMINATING_STRING;
	}

	/**
	 * The method get an encrypted String which its secret key is hard coded and decrypt it<br>
	 *
	 * @param str  - String to decrypt
	 * @return - decrypted String
	 * @throws Exception
	 */
	public static String decrypt(String str) throws Exception {
		Cipher ecipher;
		Cipher dcipher;

		str = str.replace(ENCRYPTION_TERMINATING_STRING, "");
		
		SecretKeySpec key = new SecretKeySpec(SECRET_KEY, "DES");
		ecipher = Cipher.getInstance("DES");
		dcipher = Cipher.getInstance("DES");
		ecipher.init(Cipher.ENCRYPT_MODE, key);
		dcipher.init(Cipher.DECRYPT_MODE, key);
		// Decode base64 to get bytes
		byte[] dec = Base64.getDecoder().decode(str);

		// Decrypt
		byte[] utf8 = dcipher.doFinal(dec);

		// Decode using utf-8
		return new String(utf8, "UTF8");
	}
	
	/**
	 * checks if a given String ends with the defined encription terminating string
	 * @param s	the String to check
	 * @return	true if is already encrypted
	 */
	public static boolean isEncrypted(String s){
		return s.endsWith(ENCRYPTION_TERMINATING_STRING);
	}
}
