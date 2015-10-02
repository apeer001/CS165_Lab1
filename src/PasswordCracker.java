/**
 * CS 165
 * Password cracking 
 * Lab 1
 * Section 22
 * @author pboyl001
 * @author apeer001
 *
 */

import java.math.BigInteger;
import java.security.*;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.charset.StandardCharsets;

// hashed
// 2yMDuIEVLYaDgZMVZQRlW/:16653:0:99999:7:::


// hased to hex  using the website
// d3f5c15ec6cc85611f8242eb6aeaaac9


public class PasswordCracker {
	
	private static final int maxIteration = 26;
	
	private static final String SALT = "hfT7jp2q";
	private static final String HEX_FINAL_PASS = "$1$hfT7jp2q$2yMDuIEVLYaDgZMVZQRlW/";
	
	
	/** The Identifier of this crypt() variant. */
	static final String MD5_PREFIX = "$1$";
	
	/** The number of rounds of the big loop. */
	private static final int ROUNDS = 1000;
	
	private static final int BLOCKSIZE = 16;
	
	// Linux mapping for base64
	static final String B64T = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	
	
	public static void main(String [] args)
	{
		String test = "aaaaba";
		byte[] bytesOfMessage = test.getBytes(StandardCharsets.UTF_8);
		try {
			test = md5Crypt(bytesOfMessage, SALT, MD5_PREFIX);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		String foundPassword = crackPassword(test);
		//String foundPassword = crackPassword(HEX_FINAL_PASS);
		System.out.println(foundPassword);
	}
	
	// Password is going to be 6-8 characters [alpha only, lowercase]
	// MD5 hashing algorithm
	
	// /etc/passwd
	// /etc/shadow
	
	// $1 = hashing algo
	// $2 = SALT
	// $3 = hashed password
	// Crypto lib
	// apache.commons.digest -> MD5
	
	public static String crackPassword(String passwd) {
		char [] startPoint = new char[]{'a','a','a','a','a','a','a','a'};
		
		int startingPos = 5;
		int checkIteratorNum = 0;
		
		
		
		String currentCheckedPassword = "";
		while(true){
			
			// get string from char array
			String testPassword = "";
			if (checkIteratorNum == 0){
				for (int k = 0; k < 6; k++) {
					testPassword += startPoint[k];
				}
				
				currentCheckedPassword = testPassword;
				byte[] bytesOfMessage = testPassword.getBytes(StandardCharsets.UTF_8);
				try {
					testPassword = md5Crypt(bytesOfMessage, SALT, MD5_PREFIX);
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
			    //System.out.println("MD5: " + testPassword);
			  
			} else if (checkIteratorNum == 1) {
				for (int k = 0; k < 7; k++) {
					testPassword += startPoint[k];
				}
			} else if (checkIteratorNum == 2) {
				for (int k = 0; k < 8; k++) {
					testPassword += startPoint[k];
				}
			}
			
			//System.out.println("running, current string: " + testPassword);
			
			// We have the string , do a compare
			if(testPassword.equals(passwd)) {
				System.out.println("MD5: " + testPassword);
				return "Found: " + currentCheckedPassword;
			} else {
				if (checkIteratorNum == 0){
					if (currentCheckedPassword.equals("zzzzzz")) {
						checkIteratorNum = 1;
						startingPos = 6;
						
						startPoint = new char[]{'a','a','a','a','a','a','a','a'};
						
						return "NOT FOUND";
					} else {
						// Update test char array
						if (startPoint[startingPos] == 'z') {
							if (startingPos != 0) {
								startPoint[startingPos] = 'a';
								
								int tempPos = startingPos - 1;
								if (startPoint[tempPos] != 'z') {
									startPoint[tempPos]++;
								} else {
									while(startPoint[tempPos] == 'z') {
										startPoint[tempPos] = 'a';
										tempPos--;
									}
									// update last character
									startPoint[tempPos]++;
								}
							} 
						} else {
							startPoint[startingPos]++;
						}
					}
				} else if (checkIteratorNum == 1) {
					// Update test char array
					
					if (testPassword.equals("zzzzzzz")) {
						checkIteratorNum = 2;
						startingPos = 7;
						return "!!!FAILED!!!";
					}
				} else if (checkIteratorNum == 2) {
					// Update test char array
					
					if (testPassword.equals("zzzzzzzz")) {
						return "!!!FAILED!!!";
					}
				}
			}
		}
	}

	
	public static String md5Crypt(final byte[] keyBytes, final String salt, final String prefix) throws NoSuchAlgorithmException {
		final int keyLen = keyBytes.length;
		
		// Extract the real salt from the given string which can be a complete hash string.
		String saltString;

		saltString = salt;		
		
		final byte[] saltBytes = saltString.getBytes(StandardCharsets.UTF_8);
		
		final MessageDigest ctx = MessageDigest.getInstance("MD5");
		
		ctx.update(keyBytes);
		
		ctx.update(prefix.getBytes(StandardCharsets.UTF_8));
		
		ctx.update(saltBytes);
		
		MessageDigest ctx1 = MessageDigest.getInstance("MD5");
		ctx1.update(keyBytes);
		ctx1.update(saltBytes);
		ctx1.update(keyBytes);
		byte[] finalb = ctx1.digest();
		int ii = keyLen;
		while (ii > 0) {
			ctx.update(finalb, 0, ii > 16 ? 16 : ii);
			ii -= 16;
		}
		
		Arrays.fill(finalb, (byte) 0);
		ii = keyLen;
		final int j = 0;
		while (ii > 0) {
			if ((ii & 1) == 1) {
				ctx.update(finalb[j]);
			} else {
				ctx.update(keyBytes[j]);
			}
			ii >>= 1;
		}
		
		final StringBuilder passwd = new StringBuilder(prefix + saltString + "$");
		finalb = ctx.digest();
		
		for (int i = 0; i < ROUNDS; i++) {
			ctx1 = MessageDigest.getInstance("MD5");
			if ((i & 1) != 0) {
				ctx1.update(keyBytes);
			} else {
				 ctx1.update(finalb, 0, BLOCKSIZE);
			}
			
			if (i % 3 != 0) {
				ctx1.update(saltBytes);
			}
			
			if (i % 7 != 0) {
				ctx1.update(keyBytes);
			}
			
			if ((i & 1) != 0) {
				ctx1.update(finalb, 0, BLOCKSIZE);
			} else {
				ctx1.update(keyBytes);
			}
			
			finalb = ctx1.digest();
			
		}
		
		b64from24bit(finalb[0], finalb[6], finalb[12], 4, passwd);
		b64from24bit(finalb[1], finalb[7], finalb[13], 4, passwd);
		b64from24bit(finalb[2], finalb[8], finalb[14], 4, passwd);	
		b64from24bit(finalb[3], finalb[9], finalb[15], 4, passwd);
		b64from24bit(finalb[4], finalb[10], finalb[5], 4, passwd);
		b64from24bit((byte) 0, (byte) 0, finalb[11], 2, passwd);
		
		return passwd.toString();
	}
	
		
	
	 static void b64from24bit(byte b2, byte b1, byte b0, int outLen, StringBuilder buffer) {
		// The bit masking is necessary because the JVM byte type is signed!
		int w = ((b2 << 16) & 0x00ffffff) | ((b1 << 8) & 0x00ffff) | (b0 & 0xff);
		
		// It's effectively a "for" loop but kept to resemble the original C code.
		int n = outLen;
		while (n-- > 0) {
			buffer.append(B64T.charAt(w & 0x3f));
			w >>= 6;
		}
	 }
	

}