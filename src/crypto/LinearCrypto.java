package crypto;

import java.security.NoSuchAlgorithmException;

public class LinearCrypto {

	public static byte[] decrypt(byte[] value, byte[] key, boolean showInfo){
		byte[][] sk = getSessionKeys(key, 8);
		for (int i = 0; i < sk.length; i++) {
			SecureNumberEncrypter sne = new SecureNumberEncrypter(key, sk[i]);
			value = sne.decryptAdvanced(value);
			sne.destroy();
			if(showInfo){
				if(i!=0)
					debug.Debug.remove(100);
				else
					debug.Debug.println("");
				debug.Debug.printProgressBar(i, sk.length-1, debug.Debug.TEXT, true);
			}
		}
		return value;
	}
	
	public static byte[] encrypt(byte[] value, byte[] key){
		byte[][] sk = getSessionKeys(key, 8);
		for (int i = sk.length-1; i >= 0; i--) {
			SecureNumberEncrypter sne = new SecureNumberEncrypter(key, sk[i]);
			value = sne.encryptAdvanced(value);
			sne.destroy();
		}
		return value;
	}
	
	private static byte[][] getSessionKeys(byte[] key, int n){
		byte[][] sessionKeys;
		try {
			SCMHA scmha = new SCMHA(SCMHA.SCMHA_1024_BIG_OUTPUT);
			scmha.update(key);
			byte[] k = scmha.digest();
			
			sessionKeys = new byte[n][k.length/n];
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < k.length/n; j++) {
					sessionKeys[i][j] = k[j+i*k.length/n];
				}
			}
			return sessionKeys;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
}
