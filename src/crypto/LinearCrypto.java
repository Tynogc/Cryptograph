package crypto;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class LinearCrypto {
	
	/**
	 * This Key-Encryption-Algorithm was considered and created by Sven T. Schneider (Nuernberg, Germany)
	 * 
	 * The first Consideration was published on the 9. May 2017 on github.com/Tynogc.
	 * The code is under the GNU-General-Public-License v3.0
	 * Your free to use the code in your application, as long as the original Creator(s) is/are marked.
	 * 
	 * It uses the SCMH-Algorithm (Also by: Sven t. Schneider) for Session-Key-Generation.
	 * Because it's based on this relativly slow Hashing-Algorithm it takes quite some time to decrypt a Key.
	 * 
	 * This subsystem uses the SRSH-Algorithm too.
	 * 
	 * The basic principle of destroying the Pre-Image is based on John Horton Conway's "Game of Life" 
	 * @author Sven T. Schneider
	 * @version 0.1
	 * 
	 * @param pw the raw password (unmodified user input)
	 * @param subkey the current Session-Key
	 * @param blockSize the blockSize for this operation. The Processing-Time is ~blockSize*memoryUsage
	 * @param memoryUsage bigger number -> more memoryUsage and processing-Time
	 */

	public static byte[] decrypt(byte[] value, byte[] key, boolean showInfo){
		final int memory = value[0];
		final int processingTime = value[1];
		
		final int blockSize = (int)(value[2]&0xff)+(int)(value[3]&0xff)*256;
		
		final int rounds;
		if(processingTime*2<10)rounds = 10;
		else rounds = processingTime*2;
		
		debug.Debug.println("Memory-Usage is: "+memory+" Blocksize is:"+blockSize+" Rounds are:"+rounds);
		
		value = Arrays.copyOfRange(value, 4, value.length);
		
		byte[][] sk = new byte[memory][0];
		for (int i = 0; i < sk.length; i++) {
			if(i>0)
				sk[i] = getSessionKeys(sk[i-1], key);
			else
				sk[i] = getSessionKeys(key, null);
		}
		
		for (int i = 0; i < rounds; i++) {
			byte[] salt = new byte[8];
			for (int j = 0; j < salt.length; j++) {
				salt[j] = value[j];
			}
			for (int j = 0; j < sk.length; j++) {
				sk[j] = getSessionKeys(sk[j], salt);
			}
			
			value = Arrays.copyOfRange(value, 8, value.length);
			
			if(i%5 == 4){
				value = hyperShuffle(value, sk, true);
			}
			
			SecureNumberEncrypter sne = new SecureNumberEncrypter(key, retriveCurrentKey(sk, i), blockSize, memory);
			value = sne.decryptAdvanced(value);
			sne.destroy();
			if(showInfo){
				if(i!=0)
					debug.Debug.remove(100);
				else
					debug.Debug.println("");
				debug.Debug.printProgressBar(i, rounds-1, debug.Debug.TEXT, true);
			}
		}
		return value;
	}
	
	public static byte[] encrypt(byte[] value, byte[] key, final int memory, final int processingTime ){
		
		final int blockSize;
		int bs = processingTime*4;
		blockSize = value.length/bs;
		
		final int rounds;
		if(processingTime*2<10)rounds = 10;
		else rounds = processingTime*2;
		
		byte[][] salt = new byte[rounds][8];
		
		SecureRandom secureRandom = new SecureRandom();
		for (int i = 0; i < salt.length; i++) {
			secureRandom.nextBytes(salt[i]);
		}
		
		debug.Debug.println("Memory-Usage is: "+memory+" Blocksize is:"+blockSize+" Rounds are:"+rounds);
		
		byte[][] sk = new byte[memory][0];
		for (int i = 0; i < sk.length; i++) {
			if(i>0)
				sk[i] = getSessionKeys(sk[i-1], key);
			else
				sk[i] = getSessionKeys(key, null);
		}
		
		for (int i = rounds-1; i >= 0; i--) {
			byte[][] currentKeys = sk.clone();
			for (int j = 0; j <= i; j++) {
				for (int l = 0; l < currentKeys.length; l++) {
					currentKeys[l] = getSessionKeys(currentKeys[l], salt[j]);
				}
			}
			
			
			SecureNumberEncrypter sne = new SecureNumberEncrypter(key, retriveCurrentKey(currentKeys, i), blockSize, memory);
			value = sne.encryptAdvanced(value);
			sne.destroy();
			
			if(i%5 == 4){
				value = hyperShuffle(value, currentKeys, false);
			}
			
			byte[] vNew = new byte[value.length+8];
			for (int j = 0; j < 8; j++) {
				vNew[j] = salt[i][j];
			}
			for (int j = 0; j < value.length; j++) {
				vNew[j+8] = value[j];
			}
			value = vNew;
		}
		
		byte[] ret = new byte[value.length+4];
		ret[0] = (byte)memory;
		ret[1] = (byte)processingTime;
		ret[2] = (byte)(blockSize%256);
		ret[3] = (byte)(blockSize/256);
		for (int i = 0; i < value.length; i++) {
			ret[i+4] = value[i];
		}
		
		return ret;
	}
	
	private static byte[] getSessionKeys(byte[] key, byte[] salt){
		try {
			SCMHA scmha = new SCMHA(SCMHA.SCMHA_1024_BIG_OUTPUT);
			scmha.update(key);
			if(salt!=null)
				scmha.update(salt);
			return scmha.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static byte[] retriveCurrentKey(byte[][] sk, int round){
		byte[] r = new byte[sk[0].length];
		
		int j = round%sk.length;
		for (int i = 0; i < r.length; i++) {
			r[i] = sk[j][i];
			j++;
			if(j >= sk.length)
				j = 0;
		}
		
		return r;
	}
	
	private static byte[] hyperShuffle(byte[] val, byte[][] keys, boolean decrypt){
		return hyperShuffleBlock(val, keys, decrypt);
	}
	
	/**
	 * This Method only works for val<<keys[].length
	 * @param val
	 * @param keys
	 * @return
	 */
	private static byte[] hyperShuffleBlock(byte[] val, byte[][] keys, boolean decrypt){
		System.out.println("HYYYYYPER:  ");
		int[] flip = new int[val.length];
		int count = 0;
		for (int i = 0; i < keys.length; i++) {
			for (int j = 0; j < keys[i].length; j++) {
				flip[count] = ((int)(keys[i][j] & 0xff)+flip[count])%val.length;
				count++;
				if(count>=flip.length)
					count = 0;
			}
		}
		//Remove daul-To-One
		for (int i = 0; i < flip.length; i++) {
			int p = flip[i];
			boolean cont;
			do {
				cont = false;
				for (int j = 0; j < i; j++) {
					if(flip[j] == p){
						p++;
						if(p>=val.length)p = 0;
						cont = true;
						break;
					}
				}
			} while (cont);
			flip[i] = p;
			System.out.print(p+" ");
		}
		System.out.println();
		
		byte[] ret = new byte[val.length];
		for (int i = 0; i < ret.length; i++) {
			if(decrypt){
				ret[i] = val[flip[i]];
			}else{
				ret[flip[i]] = val[i];
			}
		}
		
		return ret;
	}
}
