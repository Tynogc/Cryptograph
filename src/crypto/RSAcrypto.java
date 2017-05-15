package crypto;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import cryptoUtility.AdvancedSecureRandom;
import cryptoUtility.Random;

public class RSAcrypto {

	private static final byte DIV = Byte.MIN_VALUE;
	
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
		return encrypt(s.getBytes(), key, pub);
	}
	
	public static String encrypt(byte[] by, RSAsaveKEY key, boolean pub){
		by = encryptByte(by, key, pub);
		return Base64.getEncoder().encodeToString(by);
	}
	
	public static byte[] encryptByte(String s, RSAsaveKEY key, boolean pub){
		return encryptByte(s.getBytes(), key, pub);
	}
	
	/**
	 * Adds Padding and encrypts the given Byte array
	 * @param by
	 * @param key
	 * @param pub
	 * @return
	 * @throws SecurityException If the Block size is Problematic
	 */
	public static byte[] encryptByte(byte[] by, RSAsaveKEY key, boolean pub) throws SecurityException{
		int[] headerRaw = new int[100];
		int pos = 0;
		int k = 0;
		while(true){
			int v = Random.getInt(maxBlockSize(key.size)/2)+maxBlockSize(key.size)/2;
			if(pos+v >= by.length){
				break;
			}
			pos+=v;
			headerRaw[k] = v;
			k++;
		}
		
		byte[][] segments = new byte[k][0];
		int divisions = k;
		k = 0;
		pos = 0;
		int lastPos = 0;
		do {
			if(k<divisions)
				pos = lastPos+headerRaw[k];
			else
				pos = by.length;
			
			System.out.println("E"+lastPos + " " + pos);
			byte[] w = Arrays.copyOfRange(by, lastPos, pos);
			////////ENCRYPTION
			w = addPadding(w, key.size-1);
			
			BigInteger bigInt = new BigInteger(w);
			bigInt = encryptBlock(bigInt, key, pub);
			
			w = bigInt.toByteArray();
			///////END OF ENCRYPTION
			segments[k] = w;
			
			System.out.println("D"+w.length+" "+w[0]+" "+w[1]+" "+w[w.length-2]+" "+w[w.length-1]);
			
			lastPos = pos;
			
			k++;
		} while (k<divisions);
		
		return mixTogether(segments);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	/////D-E-C-R-Y-P-T-I-O-N
	//////////////////////////////////////////////////////////////////////////////////////////////
	
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
		byte[] by = decryptByte(s, key, pub);
		return new String(by, "UTF-8");
	}
	
	public static String decrypt(byte[] by, RSAsaveKEY key, boolean pub) throws UnsupportedEncodingException, SecurityException{
		by = decryptByte(by, key, pub);
		return new String(by, "UTF-8");
	}
	
	public static byte[] decryptByte(String s, RSAsaveKEY key, boolean pub) throws SecurityException{
		byte[] by = Base64.getDecoder().decode(s);
		return decryptByte(by, key, pub);
	}
	
	public static byte[] decryptByte(byte[] by, RSAsaveKEY key, boolean pub) throws SecurityException{
		int pos = 0;
		int k = 0;
		
		int segmentLenght = (key.size-1)/8+1;
		int numOfSegments = by.length/segmentLenght;
		
		byte[][] segments = new byte[numOfSegments][0];
		int lastPos = 0;
		do {
			pos = lastPos+segmentLenght;
			
			System.out.println("E"+lastPos + " " + pos);
			byte[] w = Arrays.copyOfRange(by, lastPos, pos);
			////////DECRYPTION
			
			BigInteger bigInt = new BigInteger(w);
			bigInt = bigInt.abs();
			
			bigInt = encryptBlock(bigInt, key, pub);
			
			w = bigInt.toByteArray();
			
			w = removePadding(w);
			///////END OF DECRYPTION
			segments[k] = w;
			
			lastPos = pos;
			
			k++;
		} while (k<numOfSegments);
		
		return mixTogether(segments);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	/////P-A-D-I-N-G
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	public static final int MIN_PADDING_BYTE = 16; 
	
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
			higherPadding = asr.nextInt(toFill-10)+9;
		
		int lowerPadding = toFill-higherPadding;
		
		if(lowerPadding<2)
			debug.Debug.println("Weak Padding: Lenght of Fill:"+higherPadding+" "+lowerPadding, debug.Debug.ERROR);
		
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
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	/////U-T-I-L-I-T-Y
	//////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Traverses the given Header until 2 Zero-bytes are found.
	 * Returns the length combination of all previous bytes
	 * @param header
	 * @return
	 */
	private static int[] processHeader(byte[] header){
		int k = 0;
		for (int i = 0; i < header.length; i+=2) {
			if(header[i] == DIV && header[i+1] == DIV){
				break;
			}
			k++;
		}
		System.out.println("Header Lenght="+k+" ");
		int[] r = new int[k];
		for (int i = 0; i < k; i++) {
			r[i] = (int)(header[i*2] & 0xff)*256 + (int)(header[i*2+1] & 0xff);
			System.out.print(r[i]+" ");
		}
		System.out.println();
		return r;
	}
	
	private static byte[] generateHeader(int[] sets){
		int k = 0;
		for (int i = 0; i < sets.length; i++) {
			if(sets[i]<=0)break;
			k++;
			System.out.print(sets[i]+" ");
		}
		System.out.println();
		byte[] ret = new byte[k*2+2];
		for (int i = 0; i <= k; i+=2) {
			ret[i] = (byte)(sets[i/2]/256);
			ret[i+1] = (byte)(sets[i/2]%256);
		}
		ret[ret.length-2] = DIV;
		ret[ret.length-1] = DIV;
		return ret;
	}
	
	private static byte[] mixTogether(byte[][] r){
		int l = 0;
		for (int i = 0; i < r.length; i++) {
			l+=r[i].length;
		}
		byte[] ret = new byte[l];
		l = 0;
		for (int i = 0; i < r.length; i++) {
			for (int j = 0; j < r[i].length; j++) {
				ret[l] = r[i][j];
				l++;
			}
		}
		return ret;
	}
}
