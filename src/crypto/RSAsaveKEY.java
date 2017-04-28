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
import java.util.Random;

import javax.crypto.Cipher;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;

public final class RSAsaveKEY implements Destroyable{
	
	private BigInteger publicExponent;
	private BigInteger privateExponent;
	private BigInteger modulus;
	
	private boolean destroyed = false;
	
	private PublicKey publicKey;
	private PrivateKey privateKey;
	
	public final int size;
	
	private static final String DIVIDER = "---";
	
	public RSAsaveKEY(String publicKeyString) throws InvalidKeySpecException, ArrayIndexOutOfBoundsException{
		this(null, publicKeyString.split(DIVIDER)[0], publicKeyString.split(DIVIDER)[1]);
	}
	
	public RSAsaveKEY(String priv, String publ, String mod) throws InvalidKeySpecException{
		if(priv != null)privateExponent = new BigInteger(priv, 16);
		if(publ != null)publicExponent = new BigInteger(publ, 16);
		modulus = new BigInteger(mod, 16);
		try {
			generateKeys();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		size = modulus.bitLength();
	}
	
	public RSAsaveKEY(BigInteger priv, BigInteger publ, BigInteger mod) throws InvalidKeySpecException{
		publicExponent = publ;
		privateExponent = priv;
		modulus = mod;
		try {
			generateKeys();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		size = modulus.bitLength();
	}
	
	private void generateKeys() throws NoSuchAlgorithmException, InvalidKeySpecException{
		KeyFactory factory = KeyFactory.getInstance("RSA");
		
		if(publicExponent!=null){
			RSAPublicKeySpec publicSpec = new RSAPublicKeySpec(modulus, publicExponent);
			publicKey = factory.generatePublic(publicSpec);
		}
		if(privateExponent!=null){
			RSAPrivateKeySpec privateSpec = new RSAPrivateKeySpec(modulus, privateExponent);
			privateKey = factory.generatePrivate(privateSpec);
			privateSpec = null;
		}
	}
	
	/**
	 * Destroys the private Exponent
	 * Also calls destruction of the privateKey
	 */
	public final void destroy() throws DestroyFailedException{
		if(privateExponent != null){
			privateExponent.xor(new BigInteger(privateExponent.bitCount(), new Random()));
			privateExponent = new BigInteger("12345");
			privateExponent = null;
		}
		if(privateKey != null){
			privateKey.destroy();
			privateKey = null;
		}
		Runtime.getRuntime().gc();
		destroyed = true;
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
	
	public boolean hasPrivate(){
		return privateKey != null;
	}
	
	/**
	 * Key-Generation Method for secure RSA-Keys
	 * @param keySize Bit size of the RSA-Key (recommended 4096 or higher)
	 * @param defaultExponent true: uses 65537 (0x10001) as the publicExponent
	 * @param showInfo Shows Debug-Info
	 * @param runSelfTest number of Self-Test (recommended 1) to ensure Proper Functionality, set to 0 if unused
	 * @param random Gives a pre-Seeded SecureRandom Object to generate Prime-Seeds, null if unused
	 * @return An RSAsaveKey with the public and private Key
	 * @throws KeyException Computing of the Keys produced an Error (self-test or Exponent) you can call the Method again  
	 * @throws Exception shouldn't happen in normal Operation
	 */
	public static RSAsaveKEY generateKey(int keySize, boolean defaultExponent, boolean showInfo, int runSelfTest,
			SecureRandom random)
			throws KeyException, Exception{
		if(random == null)
			random = new SecureRandom();
		
		//The KeySize should be mod 8 = 0
		keySize -= keySize%8;
		
		System.out.println("Starting Key Generation...");
        // Choose two prime numbers p and q.
        BigInteger p = getPrime(keySize/2,random);
        if(showInfo)
        	System.out.println("1st Prime: "+p.toString().substring(0, 6)+"...");//FIXME delet this Line!!!
        
        BigInteger q = getPrime(keySize/2,random);
        if(showInfo)
            System.out.println("2nd Prime: "+q.toString().substring(0, 6)+"...");//FIXME delet this Line!!!
        
        /**
		 * The following Lines of Code are from the stackoverflow.com Forum, published by the user 'albciff'
		 * Thanks for the help! :D
		 */
        
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
        /**
         * END of imported Code
         */
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
	
	/**
	 * Runs an encryption Test
	 * @return key can be used
	 */
	public boolean runTest(){
		try {
			testEncryption(publicKey, privateKey);
			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}
	
	/**
	 * Generates a String to exchange the PublicKey, can be used to generate a new RSAsaveKEY (Public) 
	 * @return Publ.Exponent + "---" + Modulus
	 */
	public String getPublicKeyString(){
		return publicExponent.toString(16) + DIVIDER + modulus.toString(16);
	}
	
	private static void testEncryption(PublicKey publicKey, PrivateKey privateKey) throws Exception{
		String plaintext = cryptoUtility.Random.generateRandomString(34+(int)(Math.random()*40));

		System.out.println("Decypher: "+plaintext+" "+plaintext.getBytes().length);
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
	
	private static BigInteger getPrime(int size, SecureRandom rndm){
		System.out.println(size + " "+ size/8);
		size /= 8;
		
		//Get the seeding bytes...
		byte[] b = new byte[size];
		rndm.nextBytes(b);
		
		b[0] = (byte)(b[0] & 0x4f);
		
		b[0] = (byte)(b[0] | 1<<(rndm.nextInt(3)+4));
		
		System.out.println(b[0]);
		
		BigInteger i = new BigInteger(b);
		
		if(i.mod(new BigInteger("2")).compareTo(BigInteger.ONE) != 0)
			i = i.add(BigInteger.ONE);
		
		while(!i.isProbablePrime(1000)){
			i = i.add(new BigInteger("2"));
		}
		
		return i;
	}

}
