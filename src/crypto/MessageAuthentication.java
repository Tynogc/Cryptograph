package crypto;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class MessageAuthentication {
	/**
	 * This class provides a Hash-based Message Authentifikation System based on my SRSHA Algorithm
	 * @author Sven T. Schneider
	 * @version 0.1
	 */
	
	private byte[] secretKeyOuter;
	private byte[] secretKeyInnerHashed;
	private SRSHA hash;
	private RSAsaveKEY rsaKey;
	
	public static final int SRSHA_256 = 0;
	public static final int SRSHA_512= 1;
	public static final int SRSHA_1024 = 2;
	public static final int SRSHA_256_WITH_RSA = 1000;
	public static final int SRSHA_512_WITH_RSA = 1001;
	public static final int SRSHA_1024_WITH_RSA = 1002;
	public static final int SRSHA_256_WITH_SECRET_KEY = 2000;
	public static final int SRSHA_512_WITH_SECRET_KEY = 2001;
	public static final int SRSHA_1024_WITH_SECRET_KEY = 2002;
	
	private final int algorithm;
	private final boolean verify;
	private final int hashSize;
	
	private boolean wasInitialised;
	
	private String state;
	
	/**
	 * A Message Athentification System to create a Message Signature, based on the SRSH-Algorithm by Sven T. Schneider.
	 * To use this class proceed as follows:<p>
	 * <u>Create a signature: (verify = false)</u><p>
	 * -if a Key is used call <B>init(key)</B> with eighter an RSAsaveKEY or an (secret) byte[]<p>
	 * -call <B>update(byte[])</B> and feed your Message<p>
	 * -to end the operation call <B>doFinal()</B> and get the Signatur<p><p>
	 * <u>Verify a Signature: (verify = false)</u><p>
	 * -if a Key is used call <B>init(key)</B> with eighter an RSAsaveKEY or an (secret) byte[]<p>
	 * -call <B>update(byte[])</B> and feed the original your Message<p>
	 * -at the End call <B>verify(String)</B> with the Signature<p>
	 * -if there was a Problem, you can use <B>report()</B> to get informations<p><p>
	 * @param algorithm the Algorithm to use
	 * @param verify <b>true</b>: Verify a given String, <b>false</b>: Create a Signature
	 * @throws NoSuchAlgorithmException
	 */
	public MessageAuthentication(int algorithm, boolean verify) throws NoSuchAlgorithmException{
		this.algorithm = algorithm;
		this.verify = verify;
		switch (this.algorithm) {
		case SRSHA_256: case SRSHA_256_WITH_RSA: case SRSHA_256_WITH_SECRET_KEY:
			hashSize = SRSHA.SRSHA_256;
			break;
		case SRSHA_512: case SRSHA_512_WITH_RSA: case SRSHA_512_WITH_SECRET_KEY:
			hashSize = SRSHA.SRSHA_512;
			break;
		case SRSHA_1024: case SRSHA_1024_WITH_RSA: case SRSHA_1024_WITH_SECRET_KEY:
			hashSize = SRSHA.SRSHA_1024;
			break;

		default:
			throw new NoSuchAlgorithmException("This algorithm dosn't exist!");
		}
		
		hash = new SRSHA(hashSize);
		
		wasInitialised = false;
		
		state = "Needs Init";
		
		if(algorithm != SRSHA_256 ||
				algorithm != SRSHA_512 ||
				algorithm != SRSHA_1024 )
			wasInitialised = true;
	}
	
	/**
	 * Initialize the Algorithm with a Key.<p>
	 * Only use with the Algorithms <b>SRSHA_[...]_WITH_SECRET_KEY</b>
	 * @param key a secret Key to create/verify the Signature
	 * @throws NoSuchAlgorithmException if the Algorithm dosn't support byte[] as secretKey
	 */
	public void init(byte[] key) throws NoSuchAlgorithmException{
		if(algorithm != SRSHA_256_WITH_SECRET_KEY &&
				algorithm != SRSHA_512_WITH_SECRET_KEY &&
				algorithm != SRSHA_1024_WITH_SECRET_KEY )
			throw new NoSuchAlgorithmException("This Algorithm dosn't suport byte[] keys!");
		
		key = key.clone();
		
		//If the Key is to big, it gets hashed first
		if(key.length>hashSize/8){
			SRSHA s = new SRSHA(hashSize);
			s.update(key);
			key = s.digest();
		}
		
		//Create Outer and Inner Padded Key
		byte[] opad = new byte[hashSize/8];
		byte[] ipad = new byte[hashSize/8];
		for (int i = 0; i < hashSize/8-1; i++) {
			opad[i] = 0x5c;
			ipad[i] = 0x36;
		}
		for (int i = 0; i < key.length && i < hashSize/8; i++) {
			opad[i] = (byte)(opad[i] ^ key[i]);
			ipad[i] = (byte)(ipad[i] ^ key[i]);
		}
		//Outer Padding is done
		secretKeyOuter = opad;
		//Hash inner Padding
		SRSHA s = new SRSHA(hashSize);
		s.update(ipad);
		secretKeyInnerHashed = s.digest();
		
		wasInitialised = true;
		
		state = "Processing";
	}
	
	/**
	 * Initialize the Algorithm with a Key.<p>
	 * Only use with the Algorithms <b>SRSHA_[...]_WITH_RSA</b>
	 * @param key an RSAsaveKEY to create/verify the Signature
	 * @throws NoSuchAlgorithmException if the Algorithm dosn't support RSA-Keys
	 */
	public void init(RSAsaveKEY key) throws NoSuchAlgorithmException{
		if(algorithm != SRSHA_256_WITH_RSA &&
				algorithm != SRSHA_512_WITH_RSA &&
				algorithm != SRSHA_1024_WITH_RSA )
			throw new NoSuchAlgorithmException("This Algorithm dosn't suport RSA keys!");
		
		rsaKey = key;
		wasInitialised = true;
	}
	
	/**
	 * Adds the Message m to the Hash-Function
	 * @param m the Message (or a Part of it) to Sign
	 * @throws SecurityException if the Algorithm uses a Key and wasn't initialised
	 */
	public void update(byte[] m) throws SecurityException{
		if(!wasInitialised)
			throw new SecurityException("This Algorithm needs to be initialised with a Key!");
		
		if(algorithm != SRSHA_256_WITH_SECRET_KEY &&
				algorithm != SRSHA_512_WITH_SECRET_KEY &&
				algorithm != SRSHA_1024_WITH_SECRET_KEY ){
			
			hash.update(secretKeyOuter);
			hash.update(secretKeyInnerHashed);
			hash.update(m);
		}else{
			hash.update(m);
		}
	}
	
	/**
	 * Finalizes the Hash-Creation and returns the Signature
	 * @return The Signature (in Base64 notation)
	 * @throws SecurityException if the Algorithm uses a Key and wasn't initialized
	 */
	public String doFinal() throws SecurityException{
		if(!wasInitialised)
			throw new SecurityException("This Algorithm needs to be initialised with a Key!");
		
		//Digest the hash
		byte[] b = hash.digest();
		
		state = "Final";
		
		if(algorithm != SRSHA_256_WITH_RSA &&
				algorithm != SRSHA_512_WITH_RSA &&
				algorithm != SRSHA_1024_WITH_RSA ){
			
			return RSAcrypto.encryptByte(b, rsaKey, false);
		}
		
		return Base64.getEncoder().encodeToString(b);
	}
	
	/**
	 * @return Weather or not the Algorithm has expired
	 */
	public boolean isFinal(){
		return hash.isFinal();
	}
	
	/**
	 * Verify Message to the given Signature
	 * @param signatur The Signature to verify
	 * @return true if the Signature is Valid
	 */
	public boolean verify(String signature){
		byte[] b1 = hash.digest();
		byte[] b2;
		if(algorithm != SRSHA_256_WITH_RSA &&
				algorithm != SRSHA_512_WITH_RSA &&
				algorithm != SRSHA_1024_WITH_RSA ){
			
			try {
				b2 = RSAcrypto.decryptByte(signature, rsaKey, true);
			} catch (UnsupportedEncodingException | GeneralSecurityException e) {
				state = "Decryption-Error Key dosn't match";
				return false;
			}
		}else{
			b2 = Base64.getDecoder().decode(signature);
		}
		
		if(b1.length != b2.length){
			state = "Hash lenght wrong!";
			return false;
		}
		
		for (int i = 0; i < b1.length; i++) {
			if(b1[i] != b2[i]){
				state = "The Message had been tempered with!";
				return false;
			}
		}
		state = "Valid!";
		return true;
	}
	
	public String getState(){
		return state;
	}
	
}
