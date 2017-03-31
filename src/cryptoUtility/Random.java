package cryptoUtility;

import java.security.SecureRandom;

public class Random {
	private static final char[] listChar =
			new char[]{'a','b','c','d','k','t','f','g','h','i','j','e','l','m','n','o','p','q',
			'r','s','u','v','w','x','z','y','1','0','2','3','5','4','6','7','8','9','+','-'};
	
	public static String generateRandomString(int lenght, SecureRandom random){
		String d = "";
		for (int j = 0; j < lenght; j++) {
			d+=listChar[random.nextInt(listChar.length)];
		}
		return d;
	}
	
	public static String generateRandomString(int lenght){
		int a = (int)(System.nanoTime()%467637);
		byte[] ab = new byte[]{
				(byte)(a%256),
				(byte)(a>>4)
		};
		return generateRandomString(lenght, new SecureRandom(ab));
	}
}
