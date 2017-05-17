package crypto;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SecureNumberEncrypter extends NumberEncrypter{

	private byte[] firstSubKey;
	private byte[] secondSubKey;
	
	private SecureRandom rndm;
	
	/**
	 * This value influences the processing Time A LOT
	 */
	private final int blockLenght;
	private final int SRSHA_length;
	
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
	public SecureNumberEncrypter(byte[] pw, byte[] subKey, int blockSize, int memoryUsage) {
		super(subKey, 16, false);
		
		if(blockSize<8)blockSize = 8;
		blockLenght = blockSize;
		if(memoryUsage*64<blockSize)
			SRSHA_length = blockSize;
		else
			SRSHA_length = memoryUsage*64;
		
		System.out.println("BlockLength is: "+blockLenght+" MemoryUsage is: "+SRSHA_length);
		
		
		byte[] opad = new byte[subKey.length];
		byte[] ipad = new byte[subKey.length];
		for (int i = 0; i < subKey.length; i++) {
			opad[i] = (byte)(0x5c ^ subKey[i]);
			ipad[i] = (byte)(0x36 ^ subKey[i]);
		}
		
		try {
			SCMHA s1 = new SCMHA(SCMHA.SCMHA_1024_BIG_OUTPUT);
			s1.update(ipad);
			firstSubKey = s1.digest();
			SRSHA s2 = new SRSHA(SRSHA_length);
			s2.update(opad);
			s2.update(pw);
			secondSubKey = s2.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		rndm = new SecureRandom();
	}
	
	public final byte[] encryptAdvanced(byte[] b){
		printCode(b);
		xOr(b, firstSubKey);
		printCode(b);
		
		int k = countCross(secondSubKey, 2);
		if(k<0)k*=-1;
		b = superShuffle(b, false, k);
		printCode(b);
		
		b = encrypt(b);
		printCode(b);
		
		k = countCross(firstSubKey, 1);
		if(k<0)k*=-1;
		
		b = shuffle(b, false, k);
		printCode(b);
		
		k = countCross(secondSubKey, 3);
		if(k<0)k*=-1;
		
		superXOr(b, secondSubKey, false, k);
		printCode(b);
		return b;
	}
	
	public final byte[] decryptAdvanced(byte[] b){
		printCode(b);
		
		int k = countCross(secondSubKey, 3);
		if(k<0)k*=-1;
		
		superXOr(b, secondSubKey, true, k);
		
		k = countCross(firstSubKey, 1);
		if(k<0)k*=-1;
		
		b = shuffle(b, true, k);
		printCode(b);
		
		b = decrypt(b);
		printCode(b);
		
		k = countCross(secondSubKey, 2);
		if(k<0)k*=-1;
		
		b = superShuffle(b, true, k);
		printCode(b);
		
		xOr(b, firstSubKey);
		printCode(b);
		return b;
	}
	
	private void xOr(byte[] b, byte[] x){
		int xi = 0;
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte)(b[i] ^ x[xi]);
			xi++;
			if(xi>=x.length)
				xi = 0;
		}
	}
	
	private void superXOr(byte[] b, byte[] key, boolean decrypt, int s){
		byte[] word = new byte[blockLenght];
		int count = 0;
		int pos = s%key.length;
		s = s%b.length;
		for (int i = 0; i < b.length; i++) {
			if(decrypt){
				b[s] = (byte)(b[s] ^ key[pos]);
				
				pos++;
				if(pos>=key.length)
					pos = 0;
				
				count++;
				if(count>=blockLenght){
					count = 0;
					for (int j = 0; j < word.length; j++) {
						int st = s-blockLenght+1+j;
						if(st<0)st+=b.length;
						if(st>=b.length)st-=b.length;
						word[j] = b[st];
					}
					pos = countCross(word, 1)%key.length;
					key = nextKey(key, word);
				}
			}else{
				word[count] = b[s];
				
				b[s] = (byte)(b[s] ^ key[pos]);
				
				pos++;
				if(pos>=key.length)
					pos = 0;
				
				count++;
				if(count>=blockLenght){
					count = 0;
					pos = countCross(word, 1)%key.length;
					key = nextKey(key, word);
				}
			}
			s++;
			if(s>=b.length)s = 0;
		}
	}
	
	private byte[] shuffle(byte[] code, boolean decrypt, int s){
		if(s%2 == 1){
			byte[] b = new byte[code.length];
			int j = s%code.length;
			for (int i = 0; i < code.length; i++) {
				if(decrypt)
					b[i] = code[j];
				else
					b[j] = code[i];
					
				j++;
				if(j>=code.length) j = 0;
			}
			return b;
		}else{
			byte[] b = new byte[code.length];
			int j = s%code.length;
			for (int i = 0; i < code.length; i++) {
				if(decrypt)
					b[i] = code[code.length-j-1];
				else
					b[code.length-j-1] = code[i];
					
				j++;
				if(j>=code.length) j = 0;
			}
			return b;
		}
	}
	
	private byte[] superShuffle(byte[] b, boolean decrypt, int s){
		byte[] add = new byte[8];
		int goTo[] = new int[16];
		byte[] bNew;
		if(decrypt){
			bNew = new byte[b.length-8];
			int j = 0;
			for (int i = b.length-8; i < b.length; i++) {
				add[j] = b[i];
				j++;
			}
		}else{
			bNew = new byte[b.length+8];
			rndm.nextBytes(add);
		}
		
		for (int i = 0; i < bNew.length && i < b.length; i++) {
			bNew[i] = b[i];
		}
		
		//Fill GoTo
		for (int i = 0; i < goTo.length; i++) {
			goTo[i] = -1;
		}
		for (int i = 0; i < goTo.length; i++) {
			int p = extractBit(add, i);
			boolean cont;
			do {
				cont = false;
				for (int j = 0; j < goTo.length; j++) {
					if(goTo[j] == p){
						p++;
						if(p>15)p = 0;
						cont = true;
						break;
					}
				}
			} while (cont);
			goTo[i] = p;
			System.out.print(p+" ");
		}
		System.out.println();
		
		//Shuffle
		int l = bNew.length;
		if(b.length<l)l = b.length;
		//Position in decrypted state
		int posDec = s%l;
		//Position in encrypted state
		int posEnc;
		//Position in loop
		int pos = 0;
		for (int i = 0; i < l; i++) {
			if(pos == 0 && l-i<16)
				break;
			//Get shuffle
			posEnc = posDec+goTo[pos]-pos;
			//Normalize on array
			if(posEnc < 0)posEnc += l;
			if(posEnc >= l)posEnc -= l;
			
			//Do swap
			if(decrypt){
				bNew[posDec] = b[posEnc];
			}else{
				bNew[posEnc] = b[posDec];
			}
			
			//Increment posDec
			posDec++;
			if(posDec >= l)posDec = 0;
			pos++;
			if(pos>15)pos = 0;
		}
		if(!decrypt){
			for (int i = 0; i < add.length; i++) {
				bNew[b.length+i] = add[i];
			}
		}
		return bNew;
	}
	
	private byte[] nextKey(byte[] key, byte[] w){
		SRSHA s = new SRSHA(SRSHA_length);
		s.update(key);
		s.update(w);
		return s.digest();
	}
	
	private int countCross(byte[] b, int step){
		int k = 0;
		int v = 0;
		if(step<=0)
			step = 1;
		for (int i = 0; i < b.length*2; i+=step) {
			k+=extractBit(b, i)*v;
			v++;
			if(v>5)v = 0;
		}
		if(k<0)return -k;
		return k;
	}
	
	private int extractBit(byte[] b, int pos){
		byte i = b[pos/2];
		if(pos%2 == 0)
			return i & 0x0f;
		return (i & 0xf0)>>4;
	}
	
	private void printCode(byte[] code){
		//System.out.println(new String(code));
		
		//for (int i = 0; i < code.length; i++) {
			//System.out.print(code[i]+", ");
		//}
		//System.out.println(":"+code.length);
	}

}
