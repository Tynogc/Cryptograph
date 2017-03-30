package main;

import java.math.BigInteger;
import java.security.KeyException;
import java.util.Random;

import crypto.NumberEncrypter;

public class Main {

	public static void main(String[] args) throws KeyException, Exception {
		//crypto.RSAsaveKEY.generateKey(2048, true, true, 10, null);
		testNumberEnc();
	}
	
	private static void testNumberEnc(){
		crypto.NumberEncrypter n1 = new NumberEncrypter("abcfhdskj");
		crypto.NumberEncrypter n2 = new NumberEncrypter("abcfhdskj");
		BigInteger b1 = new BigInteger(1000, new Random());
		System.out.println(b1);
		String s = n1.encrypt(b1.toString());
		System.out.println(s);
		s = n2.decrypt(s);
		System.out.println(s);
		System.out.println(new BigInteger(s).compareTo(b1));
	}

}
