package crypto;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class FastLinearCrypto {
	
	/**
	 * Basic Principle:
	 * 
	 * = 2: Flip Colums
	 * | 3: Table-Shuffle
	 * | 4: Rotate Bits
	 * | 5: Xor with Key(n)
	 * = Repeat (ROUNDS/2-1)x
	 * 6: Perform Line-Mixup with Pseudo-Random Table (Key-Generated)
	 * * = 7: Flip Colums
	 * | 8: Table-Shuffle
	 * | 9: Rotate Bits
	 * | 10: Xor with Key(n)
	 * = Repeat (ROUNDS/2-1)x
	 * 11: Perform Line-Mixup with Pseudo-Random Table (Key-Generated)
	 * * = 12: Flip Colums
	 * | 13: Table-Shuffle
	 * | 14: Rotate Bits
	 * | 15: Xor with Key(n)
	 * = Repeat 2x
	 */

	private byte[][] roundKeys;
	private byte[] lineMixupTable;
	
	private final int wordSize;
	
	private static final int ROUNDS = 8;
	private static final int LMT_SIZE = 256;
	
	private static final int PADDIN_MIN_LENGHT = 5;
	
	public FastLinearCrypto(byte[] key){
		
		wordSize = key.length;
		
		roundKeys = new byte[ROUNDS][1];
		roundKeys[0] = generateRoundKey(key);
		for (int i = 1; i < roundKeys.length; i++) {
			roundKeys[i] = generateRoundKey(roundKeys[i-1]);
		}
		for (int i = 0; i < 7; i++) {
			roundKeys[ROUNDS-1] = generateRoundKey(roundKeys[ROUNDS-1]);
		}
		MessageDigest md;
		
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			debug.Debug.printExeption(e);
			return;
		}
		
		lineMixupTable = new byte[LMT_SIZE];
		byte[] curr = roundKeys[roundKeys.length-1];
		int c = 0;
		for (int i = 0; i < lineMixupTable.length; i++) {
			curr = generateRoundKey(curr);
			md.update(roundKeys[c%roundKeys.length]);
			md.update(curr);
			byte[] tr = md.digest();
			for (int j = 0; j < tr.length; j++) {
				if(i>=lineMixupTable.length)
					break;
				lineMixupTable[i] = tr[j];
				i++;
			}
			md.reset();
			c++;
		}
		
		printRoundKeys();
		
		/////////////////////TEST
		System.out.println("---- ROTATE ----");
		byte[] test = new byte[]{50,-22,33,-125};
		printArray(test);
		test = rotate(test, 7);
		printArray(test);
		test = rotate(test, -7);
		printArray(test);
		
		for (int j = 0; j < 40; j++) {
			long t = System.currentTimeMillis();
			test = new byte[40];
			new SecureRandom().nextBytes(test);
			System.out.println("----- TEST("+(j+1)+") -----");
			System.out.print("OR :");
			printArray(test);
			
			test = encrypt(test);
			System.out.print("ENC:");
			printArray(test);
			
			test = decrypt(test);
			System.out.print("DEC:");
			printArray(test);
			System.out.println("T: "+(System.currentTimeMillis()-t));
		}
	}
	
	public byte[] encrypt(byte[] b){
		SecureRandom sr = new SecureRandom();
		int size = b.length + PADDIN_MIN_LENGHT;
		size /= wordSize;
		size++;
		size *= wordSize;
		byte[] r = new byte[size];
		
		int num = size/wordSize;
		
		int k = size - b.length;
		for (int i = 0; i < size; i++) {
			if(i<k-1){
				r[i] = (byte)sr.nextInt();
				if(r[i] == 2)
					r[i] = 3;
			}else if(i<k){
				r[i] = 2;
			}else{
				r[i] = b[i-k];
			}
		}
		if(num <= 1){
			return encrypt(r, 0);
		}
		int lastPos = 0;
		for (int i = 0; i < num; i++) {
			byte[] tr = Arrays.copyOfRange(r, i*wordSize, (i+1)*wordSize);
			int nextPos = getSum(1, tr);
			tr = encrypt(tr, lastPos);
			lastPos = nextPos;
			for (int j = 0; j < tr.length; j++) {
				r[i*wordSize+j] = tr[j];
			}
		}
		
		return r;
	}
	
	public byte[] decrypt(byte[] b){
		int paddingEnd = 0;
		byte[] r = new byte[b.length];
		
		int pos = 0;
		for (int i = 0; i < r.length; i+=wordSize) {
			byte[] tr = decrypt(Arrays.copyOfRange(b, i, i+wordSize), pos);
			pos = getSum(1, tr);
			for (int j = 0; j < tr.length; j++) {
				r[i+j] = tr[j];
			}
		}
		
		for (int i = 0; i < r.length; i++) {
			if(r[i] == 2){
				paddingEnd = i;
				break;
			}
		}
		
		return Arrays.copyOfRange(r, paddingEnd+1, r.length);
	}
	
	public byte[] encrypt(byte[] b, int k){
		int i = 0;
		for (; i < ROUNDS/2-1; i++) {
			b = ENC_singleRound(b, i);
		}
		int pos = getSum(k%7+3, lineMixupTable)+getSum(k%4, roundKeys[ROUNDS-1]);
		ENC_doLMT(b, pos);
		for (; i < ROUNDS-2; i++) {
			b = ENC_singleRound(b, i);
		}
		pos = getSum(7, lineMixupTable)+getSum(k%3, roundKeys[1]);
		ENC_doLMT(b, pos);
		for (; i < ROUNDS; i++) {
			b = ENC_singleRound(b, i);
		}
		
		return b;
	}
	
	public byte[] decrypt(byte[] b, int k){
		int i = ROUNDS-1;
		for (; i >= ROUNDS-2; i--) {
			b = DEC_singleRound(b, i);
		}
		int pos = getSum(7, lineMixupTable)+getSum(k%3, roundKeys[1]);
		DEC_doLMT(b, pos);
		for (; i >= ROUNDS/2-1; i--) {
			b = DEC_singleRound(b, i);
		}
		pos = getSum(k%7+3, lineMixupTable)+getSum(k%4, roundKeys[ROUNDS-1]);
		DEC_doLMT(b, pos);
		for (; i >= 0; i--) {
			b = DEC_singleRound(b, i);
		}
		
		return b;
	}
	
	private byte[] ENC_singleRound(byte[] b, int r){
		performMixRowsCW(b);
		//TODO tableShuffle
		b = rotate(b, getSum(3, roundKeys[r]));
		xOR(b, roundKeys[r]);
		return b;
	}
	
	private byte[] DEC_singleRound(byte[] b, int r){
		xOR(b, roundKeys[r]);
		b = rotate(b, -getSum(3, roundKeys[r]));
		//TODO tableShuffle
		performMixRowsCCW(b);
		return b;
	}
	
	private void ENC_doLMT(byte[] t, int pos){
		pos = pos%LMT_SIZE;
		for (int i = 0; i < t.length; i++) {
			int u = t[i] & 0xff;
			t[i] = (byte)(t[i]+lineMixupTable[pos]);
			pos = (pos+u)%LMT_SIZE;
		}
	}
	
	private void DEC_doLMT(byte[] t, int pos){
		pos = pos%LMT_SIZE;
		for (int i = 0; i < t.length; i++) {
			t[i] = (byte)(t[i]-lineMixupTable[pos]);
			int u = t[i] & 0xff;
			pos = (pos+u)%LMT_SIZE;
		}
	}
	
	////////////////////////////////////////////////////////////////////////
	//Key-Generation
	////////////////////////////////////////////////////////////////////////
	
	private static byte[] generateRoundKey(byte[] pre){
		byte[] b = new byte[pre.length];
		
		int k = getSum(3, pre)%pre.length;
		if(k == 0)
			k = pre.length/2-1;
		for (int i = 0; i < b.length; i++) {
			b[i] = pre[(i+k)%pre.length];
		}
		
		k = getSum(1, pre);
		doCellularMutation(b, k%3==1);
		
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte)(b[i]^pre[(i+k)%pre.length]);
		}
		
		return b;
	}
	
	private static void doCellularMutation(byte[] b, boolean inv){
		final int size = b.length;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < 8; j++) {
				int u = 0;
				if(extractAdvanced(b, i+1, j-1)) u++;
				if(extractAdvanced(b, i+1, j)) u++;
				if(extractAdvanced(b, i+1, j+1)) u++;
				if(extractAdvanced(b, i, j+1)) u++;
				if(extractAdvanced(b, i, j-1)) u++;
				if(extractAdvanced(b, i-1, j-1)) u++;
				if(extractAdvanced(b, i-1, j)) u++;
				if(extractAdvanced(b, i-1, j+1)) u++;
				
				if(extractAdvanced(b, i, j)){
					if((u != 2 && u != 3)!=inv)
						setBitAdvanced(b, i, j, false);
				}else{
					if((u == 3) != inv)
						setBitAdvanced(b, i, j, true);
				}
			}
		}
	}
	
	private static boolean extractAdvanced(byte[] b, int x, int y){
		x += b.length;
		x = x%b.length;
		y += 8;
		y = y%8;
		
		return extractBit(b[x], y);
	}
	
	private static void setBitAdvanced(byte[] b, int x, int y, boolean t){
		x += b.length;
		x = x%b.length;
		y += 8;
		y = y%8;
		
		b[x] = setBit(b[x], y, t);
	}
	
	////////////////////////////////////////////////////////////////////////
	//Utility
	////////////////////////////////////////////////////////////////////////
	
	private static byte[] rotate(byte[] b, int ammount){
		int length = b.length;
		byte[] ret = new byte[length];
		int stack = ammount%8;
		int add = ammount/8;
		add = add%length;
		if(stack<0){
			stack+=8;
			add ++;
		}
		for (int i = 0; i < length; i++) {
			int u = i+length*2-add;
			ret[i] = (byte)((b[u%length] & 0xff)<<stack | (b[(u+1)%length] & 0xff)>>(8-stack));
		}
		return ret;
	}
	
	private static void performMixRowsCW(byte[] b){
		int l = b.length/4;
		int add = 1;
		for (int i = 0; i < l; i++) {
			byte t = b[(i+l*add)%b.length];
			b[(i+l*add)%b.length] = b[(i+l*(add+1))%b.length];
			b[(i+l*(add+1))%b.length] = b[(i+l*(add+2))%b.length];
			b[(i+l*(add+2))%b.length] = b[(i+l*(add+3))%b.length];
			b[(i+l*(add+3))%b.length] = t;
			add++;
		}
	}
	
	private static void performMixRowsCCW(byte[] b){
		int l = b.length/4;
		int add = 1;
		for (int i = 0; i < l; i++) {
			byte t = b[(i+l*(add+3))%b.length];
			b[(i+l*(add+3))%b.length] = b[(i+l*(add+2))%b.length];
			b[(i+l*(add+2))%b.length] = b[(i+l*(add+1))%b.length];
			b[(i+l*(add+1))%b.length] = b[(i+l*(add))%b.length];
			b[(i+l*(add))%b.length] = t;
			add++;
		}
	}
	
	private static void xOR(byte[] b, byte[] tw){
		for (int i = 0; i < tw.length; i++) {
			b[i] = (byte)(b[i]^tw[i]);
		}
	}
	
	private static int getSum(int it, byte[] b){
		if(it<=0)
			it = 1;
		int u = 0;
		for (int i = 0; i < b.length; i+=it) {
			u += b[i] & 0xff;
		}
		if(u<0)
			System.err.println("Alarm!!!! "+u);
		return u;
	}
	
	private static boolean extractBit(byte b, int pos){
		return ((0x01<<pos)&b) != 0;
	}
	
	private static byte setBit(byte b, int pos, boolean t){
		if(t){
			return (byte)(b | (0x01<<pos));
		}else{
			int i = ~(0x01<<pos);
			return (byte)(b & i);
		}
	}
	
	private void printRoundKeys(){
		System.out.println("------ HEX ------");
		for (int i = 0; i < roundKeys.length; i++) {
			System.out.print("R"+i/10+""+i%10+": ");
			printArray(roundKeys[i]);
		}
		System.out.println("------ BIN ------");
		for (int i = 0; i < roundKeys.length; i++) {
			System.out.print("R"+i/10+""+i%10+": ");
			for (int j = 0; j < roundKeys[i].length; j++) {
				for (int k = 0; k < 8; k++) {
					if(extractBit(roundKeys[i][j], k))
						System.out.print("1");
					else
						System.out.print("0");
				}
			}
			System.out.println();
		}
		
		System.out.println("------ LMT ------");
		System.out.print("LMT: ");
		printArray(lineMixupTable);
	}
	
	private static void printArray(byte[] b){
		for (int i = 0; i < b.length; i++) {
			System.out.print(Integer.toHexString(b[i] & 0xff)+" ");
		}
		System.out.print(" (L "+b.length+")");
		System.out.println();
	}
}
