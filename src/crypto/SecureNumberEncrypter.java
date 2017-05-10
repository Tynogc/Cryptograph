package crypto;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SecureNumberEncrypter extends NumberEncrypter{

	private byte[] firstSubKey;
	private byte[] secondSubKey;
	
	private SecureRandom rndm;
	
	public SecureNumberEncrypter(byte[] pw) {
		super(pw, 16);
		
		byte[] opad = new byte[pw.length];
		byte[] ipad = new byte[pw.length];
		for (int i = 0; i < pw.length; i++) {
			opad[i] = (byte)(0x5c ^ pw[i]);
			ipad[i] = (byte)(0x36 ^ pw[i]);
		}
		
		try {
			SCMHA s = new SCMHA(SCMHA.SCMHA_1024_BIG_OUTPUT);
			s.update(ipad);
			firstSubKey = s.digest();
			s = new SCMHA(SCMHA.SCMHA_1024_BIG_OUTPUT);
			s.update(opad);
			secondSubKey = s.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		rndm = new SecureRandom();
	}
	
	public final byte[] encryptAdvanced(byte[] b){
		printCode(b);
		superXOr(b, firstSubKey, false);
		printCode(b);
		
		int k = secondSubKey[0]+secondSubKey[1];
		if(k<0)k*=-1;
		b = superShuffle(b, false, k);
		printCode(b);
		
		b = encrypt(b);
		printCode(b);
		
		k = firstSubKey[0]+firstSubKey[1];
		if(k<0)k*=-1;
		
		b = shuffle(b, false, k);
		printCode(b);
		
		xOr(b, secondSubKey);
		printCode(b);
		return b;
	}
	
	public final byte[] decryptAdvanced(byte[] b){
		printCode(b);
		xOr(b, secondSubKey);
		
		int k = firstSubKey[0]+firstSubKey[1];
		if(k<0)k*=-1;
		
		b = shuffle(b, true, k);
		printCode(b);
		
		b = decrypt(b);
		printCode(b);
		
		k = secondSubKey[0]+secondSubKey[1];
		if(k<0)k*=-1;
		b = superShuffle(b, true, k);
		printCode(b);
		
		superXOr(b, firstSubKey, true);
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
	
	private void superXOr(byte[] b, byte[] key, boolean decrypt){
		byte[] word = new byte[8];
		int count = 0;
		int pos = 0;
		for (int i = 0; i < b.length; i++) {
			if(decrypt){
				b[i] = (byte)(b[i] ^ key[pos]);
				
				pos++;
				if(pos>key.length)
					pos = 0;
				
				count++;
				if(count>=8){
					count = 0;
					for (int j = 0; j < word.length; j++) {
						word[j] = b[i-7+j];
					}
					pos = countCross(word, 1);
					System.out.println("P:");
					printCode(word);
					key = nextKey(key, word);
				}
			}else{
				word[count] = b[i];
				
				b[i] = (byte)(b[i] ^ key[pos]);
				
				pos++;
				if(pos>key.length)
					pos = 0;
				
				count++;
				if(count>=8){
					count = 0;
					pos = countCross(word, 1);
					System.out.println("P:");
					printCode(word);
					key = nextKey(key, word);
				}
			}
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
		try {
			SCMHA s = new SCMHA(SCMHA.SCMHA_1024_BIG_OUTPUT);
			s.update(key);
			s.update(w);
			return s.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
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
		System.out.println(new String(code));
		
		for (int i = 0; i < code.length; i++) {
			//System.out.print(code[i]+", ");
		}
		//System.out.println(":"+code.length);
	}

}
