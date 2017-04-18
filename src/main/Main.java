package main;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import crypto.NumberEncrypter;
import crypto.RSAcrypto;
import crypto.RSAsaveKEY;

public class Main {

	public static void main(String[] args) throws Exception {
		for (int i = 0; i < 100; i++) {
			//testNumberEnc();
		}
		//testKeyGen();
		//testKeyLoad();
		//new StartUp(new debug.DebugFrame()).doStartUp();
		new SeyprisMain();
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
		crypto.RSAsaveKEY k = crypto.RSAsaveKEY.generateKey(2048*4, true, true, 10, null);
		new crypto.KeySaveLoad().saveKeyEncrypted(k, new File("user/key/test.key"), "abcdeTest");
		new crypto.KeySaveLoad().saveKey(k, new File("user/key/testPublic.key"), true);
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

}
 