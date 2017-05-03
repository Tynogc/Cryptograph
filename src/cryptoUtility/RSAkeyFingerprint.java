package cryptoUtility;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import crypto.RSAsaveKEY;
import crypto.SCMHA;

public class RSAkeyFingerprint {

	/**
	 * Generates a Fingerprint of the given key, to compare with others, to ensure, the same (Public-)Key is used.
	 * It consists of 6 blocks with 10 characters (A-Z and 0-9) each.
	 * Uses the SCMHA by Sven T. Schneider, however it may also work with SHA-512
	 * @param key key to get the Fingerprint
	 * @return the fingerprint
	 */
	public static String getFingerprint(RSAsaveKEY key){
		SCMHA srsha;
		try {
			srsha = new SCMHA(SCMHA.SCMHA_512);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "ERROR";
		}
		srsha.update(key.getModulus().toByteArray());
		byte[] b = srsha.digest();
		
		BigInteger bi = new BigInteger(b);
		String s = bi.toString(36);
		if(bi.compareTo(BigInteger.ZERO)<0)
		s = s.substring(1);
		
		String sr = "";
		int blockCount = 0;
		for (int i = 0; i < 60; i++) {
			if(blockCount>=10){
				sr+="-";
				blockCount = 0;
			}
			char c = s.charAt(i);
			
			sr += Character.toUpperCase(c);
			
			blockCount++;
		}
			
		return sr;
	}
}
