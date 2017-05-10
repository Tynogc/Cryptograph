package crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class NumberEncrypter {
	
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
	 * In a later version, there will be an option for higher Memory-Usage too.
	 * 
	 * The basic principle of destroying the Pre-Image is based on John Horton Conway's "Game of Life" 
	 * @author Sven T. Schneider
	 * @version 0.1
	 */
	
	private int[][] rotor;
	private int[] rPos;
	
	//Counting variable for position in hash
	private int counterI;
	//Hash of PW
	private byte[] bHash;
	private int usedBytes = 0;
	
	private int[] longTermDestructors;
	private int[] longTermAdder;
	private int[] longTermDestructorsWheel;
	private int[] longTermAdderWheel;
	protected int longTermCount;
	//Counts the LTD for the Wheels
	private int wheelDestructorCount;
	
	private boolean destroyd = false;
	
	private final int radix;
	
	/**
	 * @param pw The password
	 */
	public NumberEncrypter(String pw){
		this(pw, 16);
	}
	
	/**
	 * @param pw The password
	 */
	public NumberEncrypter(String pw, int radix){
		this(pw.getBytes(), radix);
	}
	
	public NumberEncrypter(byte[] pw, int radix){
		this(pw, radix, true);
	}
	
	/**
	 * @param pw The Password
	 * @param radix Radix of the String to encrypt/decrypt 1-16
	 */
	protected NumberEncrypter(byte[] pw, int radix, boolean needToBeHashed){
		this.radix = radix;
		if(needToBeHashed){
			MessageDigest messageDigest;
			try {
				messageDigest = MessageDigest.getInstance("SHA-512");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				return;
			}
			messageDigest.update(pw);
			bHash = messageDigest.digest();
		}else{
			bHash = pw.clone();
		}
		
		//Find the starting position
		counterI = bHash[0];
		
		if(counterI<0)counterI *= -1;
		counterI = counterI%(bHash.length);
		
		counterI = bHash[counterI];
		if(counterI<0)counterI *= -1;
		counterI = counterI%(bHash.length*2);
		
		//Choose size of Rotors
		rotor = new int[radix][1];
		for (int i = 0; i < rotor.length; i++) {
			rotor[i] = new int[(extractByte()+1)*2];
		}
		
		//Fill Rotors
		for (int i = 0; i < rotor.length; i++) {
			for (int j = 0; j < rotor[i].length; j++) {
				rotor[i][j] = extractByte();
			}
		}
		
		//Select starting pos for Rotors
		rPos = new int[radix];
		for (int i = 0; i < rPos.length; i++) {
			rPos[i] = extractByte()%rotor[i].length;
		}
		
		//Long term numbers, to destroy any Pattern
		longTermDestructors = new int[(extractByte())%8+3];
		for (int i = 0; i < longTermDestructors.length; i++) {
			longTermDestructors[i] = (extractByte()+1)*(extractByte()+1);
		}
		
		longTermAdder = new int[longTermDestructors.length];
		for (int i = 0; i < longTermAdder.length; i++) {
			int u = extractByte()+2;
			int w = 0;
			for (int j = 0; j < u; j++) {
				w+=extractByte()*extractByte();
			}
			w/=(u/2);
			System.out.print(w + " ");
			longTermAdder[i] = w;
		}
		System.out.print("| ");
		longTermDestructorsWheel = new int[(extractByte())%5+3];
		for (int i = 0; i < longTermDestructorsWheel.length; i++) {
			longTermDestructorsWheel[i] = (extractByte()+1)*(extractByte()+1);
		}
		
		longTermAdderWheel = new int[longTermDestructorsWheel.length];
		for (int i = 0; i < longTermAdderWheel.length; i++) {
			longTermAdderWheel[i] = (extractByte()+1)*(extractByte()+1)*3;
			System.out.print(longTermAdderWheel[i] + " ");
		}
		
		wheelDestructorCount = 0;
		
		System.out.println("Used "+usedBytes+" of "+bHash.length);
	}
	/**
	 * This Methode calls destroy() at the end and renders the whole Object useless!
	 * @param s String to Encrypt, this String should only consist of chars 0-9
	 * @return encrypted number
	 * @throws IllegalStateException if destroy() had been called
	 */
	public final String encrypt(String s){
		if(destroyd)
			throw new IllegalStateException("This Object has been destroyed");
		String r = "";
		longTermCount = 0;
		for (int i = 0; i < s.length(); i++) {
			int u = intFromChar(s.charAt(i));
			u = singleEncrypt(u);
			r+=charFromInt(u);
			longTermCount++;
		}
		destroy();
		return r;
	}
	
	/**
	 * This Methode calls destroy() at the end and renders the whole Object useless!
	 * @param s String to decrypt
	 * @return decrypted String
	 * @throws IllegalStateException if destroy() had been called
	 */
	public final String decrypt(String s){
		if(destroyd)
			throw new IllegalStateException("This Object has been destroyed");
		String r = "";
		longTermCount = 0;
		for (int i = 0; i < s.length(); i++) {
			int u = intFromChar(s.charAt(i));
			u = singleDecrypt(u);
			r+=charFromInt(u);
			longTermCount++;
		}
		destroy();
		return r;
	}
	
	public final byte[] decrypt(byte[] q){
		byte[] r = new byte[q.length];
		for (int i = 0; i < r.length; i++) {
			int k = (q[i] & 0xff);
			int d = k%16;
			int b = k/16;
			b = singleDecrypt(b);
			longTermCount++;
			d = singleDecrypt(d);
			longTermCount++;
			r[i] = (byte)(d+b*16);
		}
		return r;
	}
	
	public final byte[] encrypt(byte[] q){
		byte[] r = new byte[q.length];
		for (int i = 0; i < r.length; i++) {
			int k = (q[i] & 0xff);
			int d = k%16;
			int b = k/16;
			b = singleEncrypt(b);
			longTermCount++;
			d = singleEncrypt(d);
			longTermCount++;
			r[i] = (byte)(d+b*16);
		}
		return r;
	}
	
	//Single operation Methods, Note that Decryption is harder, because the Rotor-switch is
	//dependent on the last Sum, so it is non-absolute.
	protected final int singleEncrypt(int i){
		int u = i;
		i-=summRotors();
		flipRotor(u);
		while(i<0)i+=radix;
		i = i%radix;
		return i;
	}
	protected int singleDecrypt(int i){
		i+=summRotors();
		flipRotor(i);
		i = i%radix;
		return i;
	}
	
	private int summRotors(){
		int u = 0;
		for (int i = 0; i < rotor.length; i++) {
			u+=rotor[i][rPos[i]];
		}
		
		//If a Long-Term-Destructor expiers add 1 and up the LTD
		for (int i = 0; i < longTermDestructors.length; i++) {
			if(longTermDestructors[i]==longTermCount){
				longTermDestructors[i]+=longTermAdder[i];
				u++;
			}
		}
		//Takes also the higher planes into calculation
		u+=u/50;
		
		return u%radix;
	}
	
	//Extracts single bits 0-15 from PW-Hash
	private int extractByte(){
		counterI++;
		if(counterI>=bHash.length*2){
			counterI -= bHash.length*2;
		}
		byte b = bHash[counterI/2];
		if(counterI%2==0)
			return 0xf & b;
		usedBytes++;
		return (0xf0 & b)>>4;
	}
	
	private void flipRotor(int i){
		//Swaps Rotors by time
		for (int j = 0; j < longTermDestructorsWheel.length; j++) {
			if(longTermCount == longTermDestructorsWheel[j]){
				wheelDestructorCount++;
				longTermDestructorsWheel[j]+=longTermAdderWheel[j];
			}
		}
		
		i = (i+wheelDestructorCount)%radix;
		rPos[i]++;
		
		if(rPos[i]>=rotor[i].length)
			rPos[i] -= rotor[i].length;
	}
	
	public boolean isDestroyd(){
		return destroyd;
	}
	
	/**
	 * Destroys sensitive Data and renders the Object useless
	 */
	public final void destroy(){
		for (int i = 0; i < bHash.length; i++) {
			bHash[i] = 0;
		}
		for (int i = 0; i < longTermDestructors.length; i++) {
			longTermDestructors[i] = 0;
		}
		for (int i = 0; i < longTermAdder.length; i++) {
			longTermAdder[i] = 0;
		}
		for (int i = 0; i < rotor.length; i++) {
			for (int j = 0; j < rotor[i].length; j++) {
				rotor[i][j] = 0;
			}
			rotor[i] = new int[]{0};
		}
		for (int i = 0; i < rPos.length; i++) {
			rPos[i] = 0;
		}
		longTermCount = 0;
		wheelDestructorCount = 0;
		destroyd = true;
		Runtime.getRuntime().gc();
	}
	
	private int intFromChar(char c){
		int u = (int)(c-'0');
		if(u>=10||u<0)
			u = (int)(c-'a')+10;
		return u;
	}
	private char charFromInt(int i){
		if(i<10)
			return (char)('0'+i);
		return (char)('a'+(i-10));
	}
}
