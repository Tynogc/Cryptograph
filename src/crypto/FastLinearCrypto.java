package crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class FastLinearCrypto {
	
	private static final byte[] HEADER_1_BOUND = new byte[]{
		12, -79, -127, -117, 111, 60, 38, 59, 89, 92, 116, -22, -39, -6, -114, 90, 105, -4, 63, 42, 84, 51, -34, -99, -2, 120, 99, 95, 24, 23, 20, -24, 
		-92, -11, -9, -15, 61, -64, -128, 3, 113, 34, 69, -54, 110, -68, 125, 79, 9, -60, -110, -111, 30, 26, -94, 117, -116, -23, 27, 80, 85, -97, 64, 103, 
		56, 53, -78, -115, -93, 112, -55, -106, 96, 124, 14, 25, -121, 55, -57, -8, -65, 13, 32, 21, -66, -16, 10, 91, 28, -49, -25, -63, -87, -38, -70, 6, 
		71, -102, -32, -105, -85, -52, 123, 94, -56, 0, -42, -28, 4, -48, -89, -120, -1, 66, -98, -77, 98, -51, -46, -21, -44, -113, 75, 5, 41, 44, -29, 57, 
		-74, -67, 114, -31, 93, -103, 100, 58, 109, -124, -108, 2, -90, 37, -61, -18, 88, -45, -73, 39, 73, 47, -71, -112, 45, -123, -126, 119, -33, 16, -100, 1, 
		52, 74, -82, 17, -91, 31, 126, -13, -84, 43, 15, 19, -101, -72, 54, -50, 29, -75, 18, -3, 104, 11, 106, 86, 46, 83, -62, 33, -109, 70, -88, 50, 
		-83, -36, -14, -47, -41, 122, 8, 65, 62, -107, -125, 77, 102, 72, -10, -69, 36, 82, 22, 40, 48, -19, -35, -7, 78, -27, 35, -26, 76, -96, -119, 68, 
		-95, -43, -58, -80, 7, 81, 67, -81, -76, 101, -118, -17, 115, -40, 97, -53, -12, -30, 87, 118, -37, -122, -5, 121, 127, -20, -59, 49, 108, 107, -104, -86
	};
	private static final byte[] HEADER_1_UNBOUND = new byte[]{
		105, -97, -117, 39, 108, 123, 95, -28, -58, 48, 86, -75, 0, 81, 74, -86, -99, -93, -78, -85, 30, 83, -46, 29, 28, 75, 53, 58, 88, -80, 52, -91, 
		82, -69, 41, -38, -48, -115, 6, -109, -45, 124, 19, -87, 125, -104, -72, -107, -44, -5, -65, 21, -96, 65, -82, 77, 64, 127, -121, 7, 5, 36, -56, 18, 
		62, -57, 113, -26, -33, 42, -67, 96, -51, -108, -95, 122, -36, -53, -40, 47, 59, -27, -47, -71, 20, 60, -73, -14, -112, 8, 15, 87, 9, -124, 103, 27, 
		72, -18, 116, 26, -122, -23, -52, 63, -76, 16, -74, -3, -4, -120, 44, 4, 69, 40, -126, -20, 10, 55, -13, -101, 25, -9, -59, 102, 73, 46, -90, -8, 
		38, 2, -102, -54, -119, -103, -11, 76, 111, -34, -22, 3, 56, 67, 14, 121, -105, 51, 50, -68, -118, -55, 71, 99, -2, -123, 97, -84, -98, 23, 114, 61, 
		-35, -32, 54, 68, 32, -92, -116, 110, -66, 92, -1, 100, -88, -64, -94, -25, -29, 1, 66, 115, -24, -79, -128, -110, -83, -106, 94, -49, 45, -127, 84, 80, 
		37, 91, -70, -114, 49, -6, -30, 78, 104, 70, 43, -17, 101, 117, -81, 89, 109, -61, 118, -111, 120, -31, 106, -60, -19, 12, 93, -12, -63, -42, 22, -100, 
		98, -125, -15, 126, 107, -39, -37, 90, 31, 57, 11, 119, -7, -43, -113, -21, 85, 35, -62, -89, -16, 33, -50, 34, 79, -41, 13, -10, 17, -77, 24, 112
	};
	
	private static final byte[] HEADER_2_BOUND = new byte[]{
		-76, 87, 91, 107, 0, 99, -123, -124, 4, -34, 24, -39, 95, 18, -101, -44, 20, -105, -93, -94, 61, -70, 70, -82, 78, 26, -122, 88, 126, 1, 105, -37, 
		-12, -8, -24, -85, -22, -121, -35, 127, -13, 62, -40, 75, 5, -16, 37, 2, 66, 120, 74, 35, 63, -9, -57, 58, -43, -80, -60, 48, 124, -104, 22, 7, 
		14, -97, 94, -108, -83, 43, -69, -4, 53, -15, -33, -78, -10, 73, 11, -106, 100, 12, -95, -48, -38, -88, -84, -25, -18, -120, 6, -30, -3, -128, 76, 69, 
		16, 17, 30, 101, -61, 34, 57, -11, 116, 110, -91, 56, 117, -32, 41, -74, -2, -127, 113, 33, -46, -73, 9, -71, 106, 68, 103, -75, -119, 32, -51, 121, 
		-59, -14, -45, -99, -50, -53, 40, -65, 13, 112, 25, -118, -62, -67, 98, 3, 109, -28, 21, 90, -1, 27, 50, 83, -58, -81, 123, -68, -52, 81, -107, -114, 
		-110, 10, 59, -103, 64, 85, -89, 125, 86, -98, -5, 72, -125, 79, -116, 47, -100, 77, 80, 119, 49, -96, 8, 114, -49, 96, 52, -113, 89, 111, 36, -23, 
		-64, 108, -26, -20, -92, 82, -102, 104, -87, -90, -27, -72, -7, 46, -117, 65, 115, -109, -86, -63, 23, 29, -17, 97, 42, 60, -55, 118, 28, 71, 84, -66, 
		55, -29, -79, 93, -36, -111, -112, -31, 31, 44, -47, 122, 67, 92, -77, -41, -56, 45, -126, 15, -115, 39, -19, 19, -21, -42, -6, 54, 102, 38, -54, 51
		};
	private static final byte[] HEADER_2_UNBOUND = new byte[]{
		4, 29, 47, -113, 8, 44, 90, 63, -74, 118, -95, 78, 81, -120, 64, -13, 96, 97, 13, -9, 16, -110, 62, -44, 10, -118, 25, -107, -36, -43, 98, -24, 
		125, 115, 101, 51, -66, 46, -3, -11, -122, 110, -40, 69, -23, -15, -51, -81, 59, -76, -106, -1, -70, 72, -5, -32, 107, 102, 55, -94, -39, 20, 41, 52, 
		-92, -49, 48, -20, 121, 95, 22, -35, -85, 77, 50, 43, 94, -79, 24, -83, -78, -99, -59, -105, -34, -91, -88, 1, 27, -68, -109, 2, -19, -29, 66, 12, 
		-71, -41, -114, 5, 80, 99, -4, 122, -57, 30, 120, 3, -63, -112, 105, -67, -119, 114, -73, -48, 104, 108, -37, -77, 49, 127, -21, -102, 60, -89, 28, 39, 
		93, 113, -14, -84, 7, 6, 26, 37, 89, 124, -117, -50, -82, -12, -97, -69, -26, -27, -96, -47, 67, -98, 79, 17, 61, -93, -58, 14, -80, -125, -87, 65, 
		-75, 82, 19, 18, -60, 106, -55, -90, 85, -56, -46, 35, 86, 68, 23, -103, 57, -30, 75, -18, 0, 123, 111, 117, -53, 119, 21, 70, -101, -115, -33, -121, 
		-64, -45, -116, 100, 58, -128, -104, 54, -16, -38, -2, -123, -100, 126, -124, -72, 83, -22, 116, -126, 15, 56, -7, -17, 42, 11, 84, 31, -28, 38, 9, 74, 
		109, -25, 91, -31, -111, -54, -62, 87, 34, -65, 36, -8, -61, -10, 88, -42, 45, 73, -127, 40, 32, 103, 76, 53, 33, -52, -6, -86, 71, 92, 112, -108
	};
	
	private static final byte[] HEADER_3_BOUND = new byte[]{
		-85, 121, 80, 70, 107, 116, 120, 83, -13, 86, -24, -121, -4, -95, -87, 54, 97, -64, 111, 113, 87, 79, -82, -108, 124, -69, -16, 68, 15, -43, 71, -28, 
		-33, -116, -27, 36, -59, 58, 42, 114, 103, -101, -105, 43, 75, 106, -58, -50, 84, -109, 33, -120, -30, -89, -119, 99, 10, -25, 115, -79, -34, 105, -103, -123, 
		-14, -15, 127, -42, -53, -117, 117, -128, -66, 21, 73, -40, 57, 47, 40, -5, 110, -88, -51, -100, 11, -127, 95, 30, 61, 81, 60, 13, -22, -54, -98, 9, 
		93, 72, -57, 101, 27, 53, 20, 26, 51, -122, -9, 38, -126, -106, 62, -12, -3, -76, 45, -1, 22, 88, 90, 55, 100, 1, 94, -37, 102, 109, 89, 46, 
		52, -44, 126, 64, 125, 34, -102, -99, 50, -55, -68, 119, -45, -96, 69, -111, -78, 31, 6, -20, -31, -10, 14, 7, 2, 37, 76, -74, -39, 122, 85, -29, 
		-46, -81, 92, -70, 39, 35, -49, -56, -107, -19, -118, 8, -94, -62, 74, -8, -63, 4, -112, 65, -18, 44, -90, 12, -17, -86, 123, 96, -91, 59, -11, -97, 
		-84, -114, -52, -80, -77, 63, -23, -71, 24, 19, 112, 41, 91, -92, -110, 29, 77, -67, -115, -61, -93, 82, -2, -113, -83, 18, -41, -47, 104, -65, 49, 25, 
		98, 48, 78, 16, 67, 28, -7, 118, -6, -73, 5, -72, -38, 56, -26, -36, 3, -125, 0, -124, -60, 32, 23, 17, -21, -75, -104, 108, -35, -48, -32, 66
	};
	private static final byte[] HEADER_3_UNBOUND = new byte[]{
		-14, 121, -104, -16, -79, -22, -110, -105, -85, 95, 56, 84, -73, 91, -106, 28, -29, -9, -39, -55, 102, 73, 116, -10, -56, -33, 103, 100, -27, -49, 87, -111, 
		-11, 50, -123, -91, 35, -103, 107, -92, 78, -53, 38, 43, -75, 114, 127, 77, -31, -34, -120, 104, -128, 101, 15, 119, -19, 76, 37, -67, 90, 88, 110, -59, 
		-125, -77, -1, -28, 27, -114, 3, 30, 97, 74, -82, 44, -102, -48, -30, 21, 2, 89, -43, 7, 48, -98, 9, 20, 117, 126, 118, -52, -94, 96, 122, 86, 
		-69, 16, -32, 55, 120, 99, 124, 40, -36, 61, 45, 4, -5, 125, 80, 18, -54, 19, 39, 58, 5, 70, -25, -117, 6, 1, -99, -70, 24, -124, -126, 66, 
		71, 85, 108, -15, -13, 63, 105, 11, 51, 54, -86, 69, 33, -46, -63, -41, -78, -113, -50, 49, 23, -88, 109, 42, -6, 62, -122, 41, 83, -121, 94, -65, 
		-115, 13, -84, -44, -51, -68, -74, 53, 81, 14, -71, 0, -64, -40, 22, -95, -61, 59, -112, -60, 113, -7, -101, -23, -21, -57, -93, 25, -118, -47, 72, -35, 
		17, -80, -83, -45, -12, 36, 46, 98, -89, -119, 93, 68, -62, 82, 47, -90, -3, -37, -96, -116, -127, 29, 67, -38, 75, -100, -20, 123, -17, -4, 60, 32, 
		-2, -108, 52, -97, 31, 34, -18, 57, 10, -58, 92, -8, -109, -87, -76, -72, 26, 65, 64, 8, 111, -66, -107, 106, -81, -26, -24, 79, 12, 112, -42, 115
	};
	private static final byte[] HEADER_4_BOUND = new byte[]{
		-5, 69, -111, -67, 16, -35, 48, -10, -124, 63, -40, 4, 60, 88, -8, -83, -64, -104, -20, 55, -78, 121, 52, 66, 70, 10, 7, 58, -97, 68, 38, 91, 
		107, 110, -26, -118, 23, 84, 104, -55, -21, -12, -19, 83, -38, 64, -70, 51, -75, 14, -49, 49, -6, 62, -39, 96, -73, -72, 72, 108, 53, 17, -102, 122, 
		-116, -98, -119, 105, -103, 12, 85, 1, 102, 11, 31, -25, -109, 95, 124, -61, -45, 74, 15, -126, 47, 0, 50, 92, 111, 127, 97, -117, -53, 114, 42, 27, 
		78, 79, -71, -28, -114, -30, 59, 39, 106, 5, -18, 2, 32, 103, 81, 45, -93, -106, -105, -123, -2, -44, -81, -56, -74, 126, -82, 117, 21, -76, -34, -125, 
		8, -91, 112, -62, -29, -36, 26, 75, 25, -46, 19, -58, -69, 125, -13, -14, 89, 24, 100, -22, -4, -7, -60, -1, -51, 101, -68, 18, -94, -84, 94, 76, 
		-52, 57, 65, -87, -15, 44, -23, 9, 99, -99, -127, 22, -95, -89, -100, 119, -88, 116, -66, 90, 41, -85, -50, 43, 123, 73, -32, -63, 29, -120, 109, -41, 
		-54, 3, 118, 56, -33, -37, 98, -96, 36, -101, 86, -47, -9, 80, 71, -108, -17, -65, 82, 33, 120, 6, 35, -43, 61, -42, -121, 93, 28, -59, -79, -122, 
		-128, -90, 30, -57, 34, -3, -113, -92, 40, -107, 37, 87, -112, 46, -77, 113, 13, -48, -31, -80, -86, -27, -11, -16, -110, 77, 67, 20, 115, -24, -115, 54
	};
	private static final byte[] HEADER_4_UNBOUND = new byte[]{
		85, 71, 107, -63, 11, 105, -43, 26, -128, -89, 25, 73, 69, -16, 49, 82, 4, 61, -101, -118, -5, 124, -85, 36, -111, -120, -122, 95, -36, -68, -30, 74, 
		108, -45, -28, -42, -56, -22, 30, 103, -24, -76, 94, -73, -91, 111, -19, 84, 6, 51, 86, 47, 22, 60, -1, 19, -61, -95, 27, 102, 12, -40, 53, 9, 
		45, -94, 23, -6, 29, 1, 24, -50, 58, -71, 81, -121, -97, -7, 96, 97, -51, 110, -46, 43, 37, 70, -54, -21, 13, -112, -77, 31, 87, -37, -98, 77, 
		55, 90, -58, -88, -110, -103, 72, 109, 38, 67, 104, 32, 59, -66, 33, 88, -126, -17, 93, -4, -79, 123, -62, -81, -44, 21, 63, -72, 78, -115, 121, 89, 
		-32, -86, 83, 127, 8, 115, -33, -38, -67, 66, 35, 91, 64, -2, 100, -26, -20, 2, -8, 76, -49, -23, 113, 114, 17, 68, 62, -55, -82, -87, 65, 28, 
		-57, -84, -100, 112, -25, -127, -31, -83, -80, -93, -12, -75, -99, 15, 122, 118, -13, -34, 20, -18, 125, 48, 120, 56, 57, 98, 46, -116, -102, 3, -78, -47, 
		16, -69, -125, 79, -106, -35, -117, -29, 119, 39, -64, 92, -96, -104, -74, 50, -15, -53, -119, 80, 117, -41, -39, -65, 10, 54, 44, -59, -123, 5, 126, -60, 
		-70, -14, 101, -124, 99, -11, 34, 75, -3, -90, -109, 40, 18, 42, 106, -48, -9, -92, -113, -114, 41, -10, 7, -52, 14, -107, 52, 0, -108, -27, 116, -105
	};
	
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
	private byte[] saltTable;
	
	private final int wordSize;
	
	private static final int ROUNDS = 8;
	private static final int LMT_SIZE = 256;
	private static final int SALT_SIZE = 256;
	
	private static final int PADDIN_MIN_LENGHT = 6;
	
	public FastLinearCrypto(byte[] key) throws IllegalArgumentException{
		
		wordSize = key.length;
		
		if(wordSize%4 != 0)
			throw new IllegalArgumentException("Key-Size must be multiple of 32 bits (Recommendet 128, 256, 512 bits), Keysize is "+wordSize+" byte");
		
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
		
		saltTable = new byte[SALT_SIZE];
		c = 0;
		for (int i = 0; i < saltTable.length; i++) {
			curr = generateRoundKey(curr);
			doCellularMutation(curr, c%4 == 0);
			md.update(roundKeys[c%roundKeys.length]);
			md.update(curr);
			byte[] tr = md.digest();
			for (int j = 0; j < tr.length; j++) {
				if(i>=saltTable.length)
					break;
				saltTable[i] = tr[j];
				i++;
			}
			md.reset();
			c++;
		}
		
		printRoundKeys();
		
		/////////////////////TEST
		byte[] test;
		long t = System.currentTimeMillis();
		final int tests = 40;
		for (int j = 0; j < tests; j++) {
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
		}
		System.out.println("T: "+(double)(System.currentTimeMillis()-t)/tests);
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
			lastPos += nextPos;
			if(lastPos>=100000)
				lastPos -= 100000;
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
			pos += getSum(1, tr);
			if(pos>=100000)
				pos -= 100000;
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
			b = ENC_singleRound(b, i, k);
		}
		int pos = getSum(k%7+3, lineMixupTable)+getSum(k%4, roundKeys[ROUNDS-1]);
		ENC_doLMT(b, pos+k/3);
		xOR(b, saltTable, k/3+getSum(3, roundKeys[0]));
		for (; i < ROUNDS-2; i++) {
			b = ENC_singleRound(b, i, k);
		}
		pos = getSum(7, lineMixupTable)+getSum(k%3, roundKeys[1]);
		ENC_doLMT(b, pos+k/7);
		xOR(b, saltTable, k%223);
		for (; i < ROUNDS; i++) {
			b = ENC_singleRound(b, i, k);
		}
		
		return b;
	}
	
	public byte[] decrypt(byte[] b, int k){
		int i = ROUNDS-1;
		for (; i >= ROUNDS-2; i--) {
			b = DEC_singleRound(b, i, k);
		}
		xOR(b, saltTable, k%223);
		int pos = getSum(7, lineMixupTable)+getSum(k%3, roundKeys[1]);
		DEC_doLMT(b, pos+k/7);
		for (; i >= ROUNDS/2-1; i--) {
			b = DEC_singleRound(b, i, k);
		}
		xOR(b, saltTable, k/3+getSum(3, roundKeys[0]));
		pos = getSum(k%7+3, lineMixupTable)+getSum(k%4, roundKeys[ROUNDS-1]);
		DEC_doLMT(b, pos+k/3);
		for (; i >= 0; i--) {
			b = DEC_singleRound(b, i, k);
		}
		
		return b;
	}
	
	private byte[] ENC_singleRound(byte[] b, int r, int k){
		performMixRowsCW(b);
		listChange(b, getSum(1, roundKeys[r]), true);
		b = rotate(b, getSum(3, roundKeys[r]));
		xOR(b, roundKeys[r], k%(r+1));
		return b;
	}
	
	private byte[] DEC_singleRound(byte[] b, int r, int k){
		xOR(b, roundKeys[r], k%(r+1));
		b = rotate(b, -getSum(3, roundKeys[r]));
		listChange(b, getSum(1, roundKeys[r]), false);
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
	
	private void listChange(byte[] b, int i, boolean encrypt){
		byte[] tr;
		i = i%7;
		
		if(encrypt){
			int u = i/2;
			if(i%2 == 0)
				i = u*2+1;
			else
				i = u*2;
		}
		
		switch (i) {
		case 0:
			tr = HEADER_1_BOUND; break;
		case 1:
			tr = HEADER_1_UNBOUND; break;
		case 2:
			tr = HEADER_2_BOUND; break;
		case 3:
			tr = HEADER_2_UNBOUND; break;
		case 4:
			tr = HEADER_3_BOUND; break;
		case 5:
			tr = HEADER_3_UNBOUND; break;
		case 6:
			tr = HEADER_4_BOUND; break;
		case 7:
			tr = HEADER_4_UNBOUND; break;
			
		default:
			System.err.println("ERROR Headerlist-ID");
			return;
		}
		
		for (int j = 0; j < b.length; j++) {
			b[j] = tr[(b[j] & 0xff)];
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
	
	private static void xOR(byte[] b, byte[] tw, int start){
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte)(b[i]^tw[(i+start)%tw.length]);
		}
	}
	
	private static int getSum(int it, byte[] b){
		if(it<=0)
			it = 1;
		int u = 0;
		for (int i = 0; i < b.length; i+=it) {
			u += b[i] & 0xff;
		}
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
		System.out.println("------ SALT ------");
		System.out.print("SALT: ");
		printArray(saltTable);
	}
	
	private static void printArray(byte[] b){
		for (int i = 0; i < b.length; i++) {
			System.out.print(Integer.toHexString(b[i] & 0xff)+" ");
		}
		System.out.print(" (L "+b.length+")");
		System.out.println();
	}
	
	public static void main(String[] a){
		new FastLinearCrypto(new SecureRandom().generateSeed(24));
	}
	
}
