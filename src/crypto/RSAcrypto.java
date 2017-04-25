package crypto;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import cryptoUtility.AdvancedSecureRandom;
import cryptoUtility.Random;

public class RSAcrypto {

	private static final String DIV = " ";
	
	/**
	 * This method is only save to use, if the message is Padded
	 * @param mes
	 * @param key
	 * @param priv
	 * @return
	 */
	public static BigInteger encryptBlock(BigInteger mes, RSAsaveKEY key, boolean pub){
		if(!pub){
			return mes.modPow(key.getPrivateExponent(), key.getModulus());
		}else{
			return mes.modPow(key.getPublicExponent(), key.getModulus());
		}
	}
	
	/**
	 * All in one Method, Adds Padding and encrypts the given String
	 * @param s String to Encrypt, needs to be Base64 compatible
	 * @param key
	 * @param pub
	 * @return Padded&Encrypted Sting in Base64 notation
	 * @throws SecurityException If the Block size is Problematic
	 */
	public static String encrypt(String s, RSAsaveKEY key, boolean pub)
			throws SecurityException{
		if(s.length()<maxBlockSize(key.size)){ //Can be Encrypted directly
			byte[] by = s.getBytes();
			return encryptByte(by, key, pub);
		}
		//Needs to be Split
		String ret = "";
		//Position in String
		int pos = 0;
		final int maxBlock = maxBlockSize(key.size);
		//Divide the String
		while(pos<s.length()){
			int lastPos = pos;
			pos += maxBlock/2+Random.getInt(maxBlock/2);
			if(pos>=s.length())pos = s.length();
			String toEncrypt = s.substring(lastPos,pos);
			byte[] by = toEncrypt.getBytes();
			ret += encryptByte(by, key, pub)+DIV;
		}
		
		return ret;
		
		//Non-Direct implementation, please ignore:
		/*byte[] b = s.getBytes();
		Cipher cipher = Cipher.getInstance("RSA");
		if(pub)
			cipher.init(Cipher.ENCRYPT_MODE, key.getPublicKey());
		else
			cipher.init(Cipher.ENCRYPT_MODE, key.getPrivateKey());
		int blockSize = (key.size/8)-11;
		
		b = addPadding(b, blockSize);
		
		b = cipher.doFinal(b);
		
		return Base64.getEncoder().encodeToString(b);*/
	}
	
	/**
	 * Adds Padding and encrypts the given Byte array
	 * @param by
	 * @param key
	 * @param pub
	 * @return
	 * @throws SecurityException If the Block size is Problematic
	 */
	public static String encryptByte(byte[] by, RSAsaveKEY key, boolean pub) throws SecurityException{
		by = addPadding(by, key.size-1);
		
		BigInteger b = new BigInteger(by);
		
		System.out.println(b.bitLength()+"-"+key.size);
		
		b = encryptBlock(b, key, pub);
		by = b.toByteArray();
		return Base64.getEncoder().encodeToString(by);
	}
	
	/**
	 * All in one Method, decrypts the String and removes Padding
	 * @param s String to Encrypt, needs to be Base64 compatible
	 * @param key
	 * @param pub
	 * @return Padded&Encrypted Sting in Base64 notation
	 * @throws GeneralSecurityException If the Padding can't be undone (Faulty decryption)
	 * @throws UnsupportedEncodingException
	 */
	public static String decrypt(String s, RSAsaveKEY key, boolean pub) 
			throws GeneralSecurityException, UnsupportedEncodingException{
		String ret = "";
		String[] substrings = s.split(DIV);
		for (int i = 0; i < substrings.length; i++) {
			byte[] by = decryptByte(substrings[i], key, pub);
			ret += new String(by, "UTF-8");
		}
		return ret;
		
		//Non-Direct implementation, please ignore:
		/*byte[] b = Base64.getDecoder().decode(s);
		Cipher cipher = Cipher.getInstance("RSA");
		if(pub)
			cipher.init(Cipher.DECRYPT_MODE, key.getPublicKey());
		else
			cipher.init(Cipher.DECRYPT_MODE, key.getPrivateKey());
		b = cipher.doFinal(b);
		
		b = removePadding(b);
		return new String(b, "UTF-8");*/
	}
	
	/**
	 * Same as decrypt() but returns the raw byte[]
	 * @param s
	 * @param key
	 * @param pub
	 * @return raw byte[] (decrypted and without Padding)
	 * @throws GeneralSecurityException
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] decryptByte(String s, RSAsaveKEY key, boolean pub) 
			throws GeneralSecurityException, UnsupportedEncodingException{
		byte[] by = Base64.getDecoder().decode(s);
		BigInteger b = new BigInteger(by);
		
		b = encryptBlock(b, key, pub);
		
		by = b.toByteArray();
		by = removePadding(by);
		return by;
	}
	
	public static final int MIN_PADDING_BYTE = 15; 
	/**
	 * Adds Random Padding to the byte-Array, at least 15 byte
	 * The Padding consist as follows:
	 * [9 rndm. bytes upper padding] [1byte upper Padding size] [upperPadding]
	 * [2 byte lowerPadding size] [Original] [lower Padding]
	 * @param b
	 * @param bitLenght Bit-Length of the Padding
	 * @return
	 * @throws SecurityException if the Added Padding is <15 byte or >256*256 byte
	 */
	public static byte[] addPadding(byte[] b, int bitLenght) throws SecurityException{
		
		int toLenght = (bitLenght)/8;
		toLenght++;
		
		byte[] bNew = new byte[toLenght];
		
		AdvancedSecureRandom asr = Random.generateSR();
		int toFill = toLenght-b.length-2;
		
		if(toFill<=12)
			throw new SecurityException("Padding can't be added! Byte-Array to long "+toLenght);
		
		//Determin size of Higher and Lower Padding
		int higherPadding;
		if(toFill>256)
			higherPadding = asr.nextInt(241)+15;
		else
			higherPadding = asr.nextInt(toFill-9)+9;
		
		int lowerPadding = toFill-higherPadding;
		
		if(lowerPadding > 256*256)
			throw new SecurityException("Padding can't be added! There is to mutch to add!");
		
		//Fill bytes
		asr.nextBytes(bNew);
		
		//Leading Bits (all 0) 
		int diff = -(bitLenght-toLenght*8);
		//Leading zeros and first 1 bit
		bNew[0] = (byte)(bNew[0] & (0xff>>diff));
		bNew[0] = (byte)(bNew[0] | (0x80>>diff));
		
		//2Bytes for Lower Padding
		int lpu = lowerPadding%256;
		int lpd = lowerPadding/256;
		//Note Padding
		bNew[9] = (byte)higherPadding;
		bNew[higherPadding+1] = (byte)lpu;
		bNew[higherPadding+2] = (byte)lpd;
		
		//Over-Padding: Hashes the bytes 0-higherPadding and uses it, to "encrypt" the message
		byte[] hash = new byte[higherPadding+3];
		for (int i = 0; i < hash.length; i++) {
			hash[i] = bNew[i];
		}
		//generate Hash, TODO own Algorithm if necessary
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new SecurityException("Problem with hash algorithm: "+e.toString());
		}
		messageDigest.update(hash);
		hash = messageDigest.digest();
		
		//Add the original Message to the return byte[]
		int hashPos = 0;
		for (int i = 0; i < b.length; i++) {
			bNew[i+higherPadding+3] = (byte)(b[i] ^ hash[hashPos]);
			hashPos++;
			if(hashPos>=hash.length)hashPos = 0;
		}
		
		//System.out.println(b.length+"-"+higherPadding+"-"+lowerPadding);
		
		return bNew;
	}
	
	/**
	 * Removes the Padding from the byte[]
	 * @param b
	 * @return
	 * @throws SecurityException if the Padding can't be removed, this can only mean faulty decryption
	 */
	public static byte[] removePadding(byte[] b) throws SecurityException{
		try {
			return removePaddingIntern(b);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SecurityException("Can't remove Padding! "+e.toString());
		}
	}
	
	private static byte[] removePaddingIntern(byte[] b) throws SecurityException{
		byte[] bNew;
		int higherPadding = b[9];
		if(higherPadding<0)higherPadding+=256;
		int lowerPadding;
		int lpu = b[higherPadding+1];
		int lpd = b[higherPadding+2];
		if(lpu<0)lpu+=256;
		if(lpd<0)lpd+=256;
		lowerPadding = lpu+lpd*256;
		
		bNew = new byte[b.length-higherPadding-lowerPadding-2];
		
		//Removes Over-Padding: Hashes the bytes 0-higherPadding and uses it, to "encrypt" the message
		byte[] hash = new byte[higherPadding+3];
		for (int i = 0; i < hash.length; i++) {
			hash[i] = b[i];
		}
		//generate Hash, TODO own Algorithm if necessary
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new SecurityException("Problem with hash algorithm: "+e.toString());
		}
		messageDigest.update(hash);
		hash = messageDigest.digest();
		
		//Retrieves Original
		int hashPos = 0;
		for (int i = 0; i < bNew.length; i++) {
			bNew[i] = (byte)(b[i+higherPadding+3] ^ hash[hashPos]);
			hashPos++;
			if(hashPos>=hash.length)hashPos = 0;
		}
		return bNew;
	}
	
	public static final int maxBlockSize(int keySize){
		return (keySize/8)-MIN_PADDING_BYTE;
	}
}
