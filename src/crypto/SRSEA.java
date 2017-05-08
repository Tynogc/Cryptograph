package crypto;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SRSEA {

	/**
	 * The SuperRandomSymetricEncryptionAlgorithm (SRSEA) is an different implementation of AES-like Encryption,
	 * done by Sven T. Schneider.
	 * It uses the SCMHA for sub-Key generatiom.
	 */
	
	private static final int ROUNDS = 12;
	
	private int size;
	private byte[] pw;
	private byte[] runKey;
	
	private int[] positions;
	private int pos;
	
	private SecureRandom rndm;
	
	//The working field
	private byte[] code;
	private byte[] codeNew;
	
	public SRSEA(byte[] pw, int size){
		
		this.pw = pw.clone();
		
		SCMHA scmha;
		try {
			scmha = new SCMHA(SCMHA.SCMHA_1024_BIG_OUTPUT);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return;
		}
		scmha.update(pw);
		runKey = scmha.digest();
		
		System.out.println(new BigInteger(runKey).toString(16));
		
		positions = new int[ROUNDS*2];
		//Determin Start
		pos = 0;
		for (int i = 0; i < runKey.length; i++) {
			genPos(i);
		}
		//Fill positions
		for (int i = 0; i < positions.length; i++) {
			int p = getBit(pos);
			pos++;
			p += getBit(pos)*3;
			positions[i] = getBit(p)*256;
			pos++;
			p = getBit(pos);
			pos++;
			p += getBit(pos)*3;
			positions[i] += getBit(pos);
			pos++;
			
			//System.out.print((positions[i]%runKey.length)+" ");
		}
		
		for (int i = 0; i < ROUNDS; i++) {
			getRoundKey(i, new byte[]{0,0,0});
		}
		
		rndm = cryptoUtility.Random.generateSR();
	}
	
	public String decrypt(String s){
		code = s.getBytes();
		for (int i = 0; i < ROUNDS; i++) {
			round(ROUNDS-i-1, true);
			printCode();
		}
		return new String(code);
	}
	
	public String encrypt(String s){
		while(s.length()%4 != 0)
			s+="-";
		code = s.getBytes();
		System.out.println(s);
		printCode();
		for (int i = 0; i < ROUNDS; i++) {
			round(i, false);
			printCode();
		}
		return new String(code);
	}
	
	private void round(int round, boolean decryptMode){
		//Random addition
		byte[] add = new byte[4];
		
		int[] mix = getRoundMix(round);
		if(decryptMode){
			//De-Shuffle
			shuffle(mix, true);
			
			//catch adding
			for (int i = 0; i < add.length; i++) {
				add[i] = code[code.length-4+i];
			}
			codeNew = new byte[code.length-4];
			operate(add, round, true);
			
			code = codeNew;
			codeNew = null;
		}else{
			rndm.nextBytes(add);
			codeNew = new byte[code.length+4];
			operate(add, round, false);
			
			code = codeNew;
			codeNew = null;
			for (int i = 0; i < add.length; i++) {
				code[code.length-4+i] = add[i];
			}
			//Shuffle
			shuffle(mix, false);
		}
	}
	
	private void operate(byte[] add, int round, boolean decrypt){
		byte[] key = getRoundKey(round, new byte[]{0});
		key = "Hello you akda".getBytes();
		System.out.println(round+": "+add[0]+" "+add[1]+" "+add[2]+" "+add[3]+"   "+new BigInteger(key).toString(16));
		if(round == ROUNDS-1|| round == ROUNDS/2){
			NumberEncrypter numEn = new NumberEncrypter(key, 16);
			if(decrypt)
				code = numEn.decrypt(code);
			else
				code = numEn.encrypt(code);
			
			fillBn();
			return;
		}
		int decide = add[0] & 0x3;
		switch (decide) {
		case 0:
			//add(key, add, decrypt);
			break;
		case 1:
			//add(key, add, !decrypt);
			break;
		case 2:
			//add(key, add, !decrypt);
			break;
		case 3:
			//add(key, add, !decrypt);
			break;

		default:
			System.err.println("ERRRROR");
			break;
		}
		fillBn();
	}
	
	private void fillBn(){
		for (int i = 0; i < codeNew.length && i < code.length; i++) {
			codeNew[i] = code[i];
		}
	}
	
	private void add(byte[] key, byte[] add, boolean plus){
		int p = add[1]+add[2]*128;
		p = p%key.length;
		if(p<0)p*=-1;
		for (int i = 0; i < codeNew.length && i < code.length; i++) {
			codeNew[i] = (byte)(code[i] ^ key[p]);
			p++;
			if(p>=key.length) p = 0;
		}
	}
	
	private void shuffle(int[] m, boolean decrypt){
		if(true)
			return;
		
		int s = m[0]+m[1];
		if(m[0]%3 == 0 && decrypt){
			for (int i = 0; i < code.length; i+=4) {
				byte b = code[i];
				code[i] = code[i+1];
				code[i+1] = code[i+2];
				code[i+2] = code[i+3];
				code[i+3] = b;
			}
		}else if(m[0]%3 == 0 && !decrypt){
			for (int i = 0; i < code.length; i+=4) {
				byte b = code[i+3];
				code[i+3] = code[i+2];
				code[i+2] = code[i+1];
				code[i+1] = code[i];
				code[i] = b;
			}
		}else if(m[0]%3 == 1){
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
			code = b;
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
			code = b;
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////
	
	private byte[] getRoundSwap(int round, byte[] addition){
		int posInRound = (positions[round*2]+round)%runKey.length;
		int roundLenght = positions[round*2+1]%54+10;
		byte[] b = new byte[roundLenght+addition.length];
		for (int i = 0; i < roundLenght; i++) {
			b[i] = runKey[posInRound];
			posInRound++;
			if(posInRound>=runKey.length)
				posInRound = 0;
		}
		for (int i = roundLenght; i < b.length; i++) {
			b[i] = addition[i-roundLenght];
		}
		return b;
	}
	
	private byte[] getRoundKey(int round, byte[] addition){
		byte[] b = getRoundSwap(round, addition);
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		messageDigest.update(pw);
		messageDigest.update(b);
		b = messageDigest.digest();
		//System.out.println(new BigInteger(b).toString(16));
		return b;
	}
	
	private int[] getRoundMix(int round){
		return new int[]{positions[round*2]/256, positions[round*2+1]/256};
	}
	
	private void genPos(int i){
		pos += getBit(i);
		if(pos>=runKey.length)
			pos -= runKey.length;
	}
	
	private int getBit(int i){
		i = i%runKey.length;
		int p = runKey[i];
		
		if(p<0) p+=256;
		return p;
	}
	
	private void printCode(){
		//System.out.println(new String(code));
		
		for (int i = 0; i < code.length; i++) {
			System.out.print(code[i]+", ");
		}
		System.out.println(":"+code.length);
	}
}
