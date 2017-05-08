package main;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Random;

import crypto.NumberEncrypter;
import crypto.RSAcrypto;
import crypto.RSAsaveKEY;
import crypto.SRSHA;

public class Main {
	
	public static void main(String[] args) throws Exception {
		cryptoUtility.Random.enterEntropy(0);
		
		//testKeyGen();
		//testKeyLoad();
		//new StartUp(new debug.DebugFrame()).doStartUp();
		//testSRSHA(100, 512);
		SecureRandom sr = new SecureRandom();
		for (int i = 0; i < 1; i++) {
			byte[] pw = "Hello you akda".getBytes();
			byte[] b = new byte[]{-62, 90, -37, 77, -116, 60, -1, 116, -111, 53, 100, -83, -98, -113, -109, -108, 108, 103, -73, 103, -35, -53, -66, -79, 87, 80, 103, -96, 44, -126, 54, -60, 65, 51, -58, -94, -43, -21, -42, -46, -31, 32, 93, -12, 36, -117, 13, 79, 84, 26, -80, -128, 16, -90, 104, 115, 55, 82, -76, -100, 125, -44, -83, 37, -70, 78, -55, -95, 73, -25, -64, -78, 46, 48, 46, -115, 69, -78, -105, 108, -34, -10, 49, 9, -26, -120, 119, -51, -21, -47, 69, -44, 53, 32, -18, -93, 114, -109, -111, -52};
			
			numberEncryptionSuperLoop(pw, b);
			
			System.out.println(i);
		}
		
		new SeyprisMain();
	}
	
	protected static void numberEncryptionSuperLoop(byte[] s, byte[] b) throws Exception{
		NumberEncrypter n = new NumberEncrypter(s, 16);
		for (int i = 0; i < b.length; i++) {
			System.out.print(b[i] +" ");
		}
		System.out.println();
		b = n.encrypt(b);
		n = new NumberEncrypter(s, 16);
		for (int i = 0; i < b.length; i++) {
			System.out.print(b[i] +" ");
		}
		System.out.println();
		b = n.decrypt(b);
		
		for (int i = 0; i < b.length; i++) {
			System.out.print(b[i] +" ");
		}
		System.out.println();
	}
	
	protected static void testNumberEnc() throws Exception{
		cryptoUtility.Random.enterEntropy(0);
		/*String b1 = "HelloHello Tlionghaifskjaksjfkkjfkdsajkfjkdsajkf";
		System.out.println(b1+" "+b1.getBytes().length);
		byte[] b = crypto.RSAcrypto.addPadding(b1.getBytes(), 63);
		b = crypto.RSAcrypto.removePadding(b);
		String b2 = new String(b,"UTF-8");
		System.out.println(b2);
		System.out.println(b2.compareTo(b1));*/
		String s1 = cryptoUtility.Random.generateRandomString((int)(Math.random()*100));
		RSAsaveKEY key = RSAsaveKEY.generateKey(1024, true, false, 0, cryptoUtility.Random.generateSR());
		RSAsaveKEY key2 = RSAsaveKEY.generateKey(2048, true, false, 0, cryptoUtility.Random.generateSR());
		System.out.println(s1+" "+s1.getBytes().length);
		String s2 = RSAcrypto.encrypt(s1, key, true);
		s2 = RSAcrypto.encrypt(s2, key2, true);
		System.out.println(s2);
		s2 = RSAcrypto.decrypt(s2, key2, false);
		s2 = RSAcrypto.decrypt(s2, key, false);
		System.out.println(s2);
		if(s1.compareTo(s2)!=0)
			throw new Exception("No match! "+s1+" "+s2);
	}
	
	protected static void testKeyGen() throws Exception{
		crypto.RSAsaveKEY k = crypto.RSAsaveKEY.generateKey(2048*2, true, true, 10, null);
		new crypto.KeySaveLoad().saveKeyEncrypted(k, new File("data/user/default/Private.key"), "TestTest123");
		new crypto.KeySaveLoad().saveKey(k, new File("data/user/default/Public.key"), true);
	}
	
	protected static void testKeyLoad() throws Exception{
		crypto.RSAsaveKEY priv = new crypto.KeySaveLoad().loadEncrypted(new File("data/key/test.key"),"abcdeTest");
		System.out.println(priv.runTest());
		
		BigInteger i = new BigInteger(2048-1, new Random());
		System.out.println(i.toString(16));
		i = crypto.RSAcrypto.encryptBlock(i, priv, false);
		System.out.println(i.toString(16));
		i = crypto.RSAcrypto.encryptBlock(i, priv, true);
		System.out.println(i.toString(16));
	}
	
	protected static void testSRSHA(int a, int size) throws NoSuchAlgorithmException{
		long tStart = System.currentTimeMillis();
		long trueBits = 0;
		int minBits = 10000;
		int maxBits = 0;
		int[] ammount = new int[256];
		
		String[][] ags = new String[a][2];
		
		SecureRandom rndm = cryptoUtility.Random.generateSR();
		crypto.SCMHA srsha;
		ags[0][0] = "*Null";
		ags[0][1] = Base64.getEncoder().encodeToString(new byte[size]);
		byte[] b;
		for (int i = 1; i < a; i++) {
			srsha = new crypto.SCMHA(size);
			ags[i][0] = cryptoUtility.Random.generateRandomString(rndm.nextInt(size)+2, rndm);
			srsha.update(ags[i][0].getBytes());
			b = srsha.digest();
			for (int j = 0; j < b.length; j++) {
				int q = b[j];
				if(q<0)q+=256;
				ammount[q]++;
			}
			int q = gui.TestSRSHA.countSetBits(b);
			if(q<minBits)minBits = q;
			if(q>maxBits)maxBits = q;
			trueBits+=q;
			String k = Base64.getEncoder().encodeToString(b);
			for (int j = 0; j < ags.length; j++) {
				if(ags[j][1]==null)
					break;
				
				if(k.compareTo(ags[j][1])==0){
					if(ags[i][0].compareTo(ags[j][0]) != 0){
						System.out.println(">>Collision<<"+ags[i][0]+" "+ags[j][0]+" "+k+"<<<<<<<<<");
						return;
					}
					System.out.println(">>Collision<<"+ags[i][0]+" "+ags[j][0]+" "+k);
					break;
				}
			}
			ags[i][1] = k;
			System.out.println(i);
			//gdzomvw-upy tpopf4wzfq4f79usminx5yr+w-5-k7il
		}
		
		tStart = System.currentTimeMillis()-tStart;
		System.out.println("DONE! Time was:"+tStart+"(Avr.:"+tStart/a+")");
		System.out.println("True Bits: "+trueBits/a+" Min:"+minBits+" Max:"+maxBits);
		for (int i = 0; i < ammount.length; i++) {
			if(i<10)
				System.out.println("  "+i+": "+ammount[i]);
			else if(i<100)
				System.out.println(" "+i+": "+ammount[i]);
			else
				System.out.println(i+": "+ammount[i]);
		}
		
		minBits = 100000;
		maxBits = 0;
		trueBits = 0;
		for (int i = 0; i < ammount.length; i++) {
			if(ammount[i]<minBits)minBits = ammount[i];
			if(ammount[i]>maxBits)maxBits = ammount[i];
			trueBits+=ammount[i];
		}
		System.out.println("Histogramm min:"+minBits+" max:"+maxBits);
	}
}
 