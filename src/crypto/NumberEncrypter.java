package crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class NumberEncrypter {
	
	private int[][] rotor;
	private int[] rPos;
	
	//Counting variable for position in hash
	private int counterI;
	//Hash of PW
	private byte[] bHash;
	private int usedBytes = 0;
	
	private int[] longTermDestructors;
	private int[] longTermAdder;
	private int longTermCount;
	
	private boolean destroyd = false;
	
	/**
	 * @param pw The password
	 */
	public NumberEncrypter(String pw){
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return;
		}
		messageDigest.update(pw.getBytes());
		bHash = messageDigest.digest();
		
		
		//Find the starting position
		counterI = bHash[0];
		
		if(counterI<0)counterI *= -1;
		counterI = counterI%(bHash.length);
		
		counterI = bHash[counterI];
		if(counterI<0)counterI *= -1;
		counterI = counterI%(bHash.length*2);
		
		//Choose size of Rotors
		rotor = new int[10][1];
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
		rPos = new int[10];
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
		
		System.out.println("Used "+usedBytes+" of "+bHash.length);
	}
	/**
	 * This Methode calls destroy() at the end and renders the whole Object useless!
	 * @param s String to Encrypt, this String should only consist of chars 0-9
	 * @return encrypted number
	 * @throws IllegalStateException if destroy() had been called
	 */
	public String encrypt(String s){
		if(destroyd)
			throw new IllegalStateException("This Object has been destroyed");
		String r = "";
		longTermCount = 0;
		for (int i = 0; i < s.length(); i++) {
			int u = (int)(s.charAt(i)-'0');
			u = singleEncrypt(u);
			r+=u;
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
	public String decrypt(String s){
		if(destroyd)
			throw new IllegalStateException("This Object has been destroyed");
		String r = "";
		longTermCount = 0;
		for (int i = 0; i < s.length(); i++) {
			int u = (int)(s.charAt(i)-'0');
			u = singleDecrypt(u);
			r+=u;
			longTermCount++;
		}
		destroy();
		return r;
	}
	
	//Single operation Methods, Note that Decryption is harder, because the Rotor-switch is
	//dependent on the last Sum, so it is non-absolute.
	private int singleEncrypt(int i){
		int u = i;
		i-=summRotors();
		flipRotor(u);
		while(i<0)i+=10;
		i = i%10;
		return i;
	}
	private int singleDecrypt(int i){
		i+=summRotors();
		flipRotor(i);
		i = i%10;
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
		
		return u%10;
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
		i = i%10;
		rPos[i]++;
		
		if(rPos[i]>=rotor[i].length)
			rPos[i] -= rotor[i].length;
	}
	
	public boolean isDestroyd(){
		return destroyd;
	}
	
	/**
	 * Destroys sensitiv Data and renders the Obnject useless
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
		destroyd = true;
		Runtime.getRuntime().gc();
	}
}
