package crypto;

import java.math.BigInteger;
import java.security.KeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;
import javax.security.auth.DestroyFailedException;

public class RSAsaveKEY {
	
	private BigInteger publicExponent;
	private BigInteger privateExponent;
	private BigInteger modulus;
	
	private boolean destroyed = false;
	
	private PublicKey publicKey;
	private PrivateKey privateKey;
	
	public RSAsaveKEY(String priv, String publ, String mod) throws NoSuchAlgorithmException, InvalidKeySpecException{
		if(priv != null)privateExponent = new BigInteger(priv);
		if(publ != null)publicExponent = new BigInteger(publ);
		modulus = new BigInteger(mod);
		generateKeys();
	}
	
	public RSAsaveKEY(BigInteger priv, BigInteger publ, BigInteger mod) throws NoSuchAlgorithmException, InvalidKeySpecException{
		publicExponent = publ;
		privateExponent = priv;
		modulus = mod;
		generateKeys();
	}
	
	private void generateKeys() throws NoSuchAlgorithmException, InvalidKeySpecException{
		RSAPublicKeySpec publicSpec = new RSAPublicKeySpec(modulus, publicExponent);
        RSAPrivateKeySpec privateSpec = new RSAPrivateKeySpec(modulus, privateExponent);

        KeyFactory factory = KeyFactory.getInstance("RSA");

        publicKey = factory.generatePublic(publicSpec);
        privateKey = factory.generatePrivate(privateSpec);
	}
	
	public void destroy() throws DestroyFailedException{
		destroyed = true;
		privateExponent = new BigInteger("12345");
		privateKey.destroy();
	}
	
	public PrivateKey getPrivateKey()throws IllegalStateException{
		if(destroyed)throw new IllegalStateException("Key has been Destroyed");
		
		return privateKey;
	}
	
	public PublicKey getPublicKey(){
		return publicKey;
	}

	public BigInteger getPublicExponent() {
		return publicExponent;
	}

	public BigInteger getPrivateExponent() throws IllegalStateException{
		if(destroyed)throw new IllegalStateException("Key has been Destroyed");
		return privateExponent;
	}

	public BigInteger getModulus() {
		return modulus;
	}

	public boolean isDestroyed() {
		return destroyed;
	}
	
	public static RSAsaveKEY generateKey(int keySize, boolean defaultExponent, boolean showInfo, int runSelfTest,
			SecureRandom random)
			throws KeyException, Exception{
		if(random == null)
			random = new SecureRandom();
        // Choose two distinct prime numbers p and q.
        BigInteger p = BigInteger.probablePrime(keySize/2,random);
        if(showInfo)
        	System.out.println("1st Prime: "+p.toString().substring(0, 6)+"...");//FIXME delet this Line!!!
        
        BigInteger q = BigInteger.probablePrime(keySize/2,random);
        if(showInfo)
            System.out.println("2nd Prime: "+q.toString().substring(0, 6)+"...");//FIXME delet this Line!!!
        // Compute n = pq (modulus)
        BigInteger modulus = p.multiply(q);
        // Compute O(n) = O(p)O(q) = (p - 1)(q - 1) = n - (p + q -1), where O is Euler's totient function.
        // and choose an integer e such that 1 < e < O(n) and gcd(e, O(n)) = 1; i.e., e and O(n) are coprime.
        BigInteger m = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        BigInteger publicExponent = getCoprime(m,random,defaultExponent);
        if(showInfo)
        	System.out.println("PublicExp: "+publicExponent);
        // Determine d as d = e-1 (mod O(n)); i.e., d is the multiplicative inverse of e (modulo O(n)).
        BigInteger privateExponent = publicExponent.modInverse(m);
        m = new BigInteger("1");
        
        if(showInfo){
        	System.out.println("Modulus  : "+modulus.toString().substring(0, 10)+"...");
        	System.out.println("KeySize is "+modulus.bitLength());
        }
        
        RSAsaveKEY theKey = new RSAsaveKEY(privateExponent, publicExponent, modulus);
        
        for (int i = 0; i < runSelfTest; i++) {
        	if(showInfo)
				System.out.println("Selftest "+(i+1)+"/"+runSelfTest);
        	
			testEncryption(theKey.getPublicKey(), theKey.getPrivateKey());
		}
        
        if(showInfo)
        	System.out.println("DONE!");
        
        return theKey;
	}
	
	public static void testEncryption(PublicKey publicKey, PrivateKey privateKey) throws Exception{
		String plaintext = utility.Random.generateRandomString(34+(int)(Math.random()*40));

		System.out.println("Decypher: "+plaintext);
		// Compute signature
		Signature instance = Signature.getInstance("SHA1withRSA");
		instance.initSign(privateKey);
		instance.update((plaintext).getBytes());
		byte[] signature = instance.sign();

		// Encrypt digest
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		byte[] cipherText = cipher.doFinal(plaintext.getBytes());
		
		cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, publicKey);
		byte[] decypher = cipher.doFinal(cipherText);
		
		System.out.println("Decypher: "+ new String(decypher));
		if(new String(decypher).compareTo(plaintext) != 0)
			throw new KeyException("Selftest Faild!");
		
		cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		decypher = cipher.doFinal(decypher);
		cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		decypher = cipher.doFinal(decypher);
		
		System.out.println("Decypher: "+ new String(decypher));
		if(new String(decypher).compareTo(plaintext) != 0)
			throw new KeyException("Selftest Faild!");
		
		instance = Signature.getInstance("SHA1withRSA");
		instance.initVerify(publicKey);
		instance.update(plaintext.getBytes());
		if(!instance.verify(signature))
			throw new KeyException("Selftest Faild! (Signatur)");
    }
	
	private static BigInteger getCoprime(BigInteger m, SecureRandom random, boolean defaultExponent) throws KeyException {
        BigInteger e;
        if(defaultExponent)e = new BigInteger("65537");
        else e = BigInteger.probablePrime(64,random);
        while (! (m.gcd(e)).equals(BigInteger.ONE) ) {
        	if(defaultExponent)throw new KeyException("Key-Generation Failed, Retry!");
            e = BigInteger.probablePrime(64,random);
        }
        return e;
	}

}
