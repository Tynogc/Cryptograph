package crypto;

import java.io.File;

public class KeySaveLoad {

	/**
	 * This Method saves the Key in the given file.
	 * This happens unencrypted, so it is not recommended for Private-Keys
	 * @param key The key to be saved
	 * @param f The File the Key should be stored in
	 * @param publ Save only the Public part of the Key. Recommended: true
	 */
	public static final void saveKey(RSAsaveKEY key, File f, boolean publ){
		
	}
	
	/**
	 * Stores the whole Key, however the Private-Exponent gets Encrypted.
	 * @param key The key to be saved
	 * @param f The File the Key should be stored in
	 * @param enc Encryption-String for the Private-Exponent
	 */
	public static final void saveKeyEncrypted(RSAsaveKEY key, File f, String enc){
		NumberEncrypter nec = new NumberEncrypter(enc);
		String privExp = nec.encrypt(key.getPrivateExponent().toString());
	}
	
	private static final void save(RSAsaveKEY key, String priv, File f){
		
	}
}
