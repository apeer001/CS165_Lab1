/**
 * CS 165
 * Password cracking 
 * Lab 1
 * Section 22
 * @author pboyl001
 * @author apeer001
 *
 */
public class PasswordCracker {
	
	private static final int maxIteration = 26;
	
	public static void main(String [] args)
	{
		String passwd = "zaabaa";
		String foundPassword = crackPassword(passwd);
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
		
		while(true){
			
			// get string from char array
			String testPassword = "";
			if (checkIteratorNum == 0){
				for (int k = 0; k < 6; k++) {
					testPassword += startPoint[k];
				}
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
				return "Found: "+ testPassword;
			} else {
				if (checkIteratorNum == 0){
					
					if (testPassword.equals("zzzzzz")) {
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
}
