package main;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import crypto.NumberEncrypter;

public class Main {

	public static void main(String[] args) throws Exception {
		//testNumberEnc();
		//testKeyGen();
		//testKeyLoad();
		//new StartUp(new debug.DebugFrame()).doStartUp();
		new SeyprisMain();
	}
	
	protected static void testNumberEnc(){
		crypto.NumberEncrypter n1 = new NumberEncrypter("abcfhdskj");
		crypto.NumberEncrypter n2 = new NumberEncrypter("abcfhdskj");
		BigInteger b1 = new BigInteger(1000, new Random());
		System.out.println(b1.toString(16));
		String s = n1.encrypt(b1.toString(16));
		System.out.println(s);
		s = n2.decrypt(s);
		System.out.println(s);
		System.out.println(new BigInteger(s,16).compareTo(b1));
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
