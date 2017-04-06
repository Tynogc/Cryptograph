package cryptoUtility;

import java.security.SecureRandom;

public class Random {
	private static final char[] listChar =
			new char[]{'a','b','c','d','k','t','f','g','h','i','j','e','l','m','n','o','p','q',
			'r','s','u','v','w','x','z','y','1','0','2','3','5','4','6','7','8','9','+','-'};
	
	private static int[] entropy;
	private static int pos;
	private static int entropyFuelGauge = -10;
	
	private static int subCount;
	
	//A Secure-Random, to use, if there is are not enough Seed-Values,
	//it is also used for entropy XOr ect.
	private static SecureRandom additionalRnd;
	
	/**
	 * Feed entropy to the Random-Generator
	 * @param i entropy, please be random :D
	 */
	public static void enterEntropy(int i){
		if(entropy == null){
			entropy = new int[1000];
			pos = 0;
			entropyFuelGauge = -1;
		}
		
		if(subCount<5){
			subCount++;
			return;
		}
		subCount = 0;
		
		//XOr of existing entropy with new value
		entropy[pos] = entropy[pos] ^ i;
		
		pos++;
		if(pos>=entropy.length){
			pos = 0;
			entropyFuelGauge++;
		}
	}
	
	/**
	 * Generates a random String.
	 * @param lenght of the String
	 * @param random A SecureRandom to generate the String
	 * @return
	 */
	public static String generateRandomString(int lenght, SecureRandom random){
		String d = "";
		for (int j = 0; j < lenght; j++) {
			d+=listChar[random.nextInt(listChar.length)];
		}
		return d;
	}
	
	public static String generateRandomString(int lenght){
		return generateRandomString(lenght, generateSR());
	}
	
	/**
	 * Generates a Fresh-Seeded SecureRandom.
	 * If there is not enough entropy, it is self-seeded; use the entropyFuelGauge to check the
	 * status of the farmed Entropy.
	 * @return Fresh-Seeded SecureRandom
	 */
	public static SecureRandom generateSR(){
		if(additionalRnd == null)additionalRnd = new SecureRandom();
		//Not enough entropy!
		if(entropyFuelGauge < 0)
			return additionalRnd;
		
		return new SecureRandom(generateSeed());
	}
	
	/**
	 * Generates a Random Seed, by using an existing SecureRandom and the entropy stored in the database.
	 * If the entropyFuelGauge is lower than 0 this should not be called!
	 * @return seed of 125-170 byte lenght
	 */
	public static byte[] generateSeed(){
		if(additionalRnd == null)additionalRnd = new SecureRandom();
		byte[] ba = additionalRnd.generateSeed(additionalRnd.nextInt(45)+125);
		for (int i = 0; i < ba.length; i++) {
			int p = additionalRnd.nextInt(entropy.length); //Position to take
			
			int shift = additionalRnd.nextInt(9);//BitShift up to 9 bit
			ba[i] = (byte)(ba[i] ^ (byte)(entropy[p]>>shift));
		}
		entropyFuelGauge--;
		return ba;
	}
	
	/**
	 * Returns an pseudo-Value do determine Entropy quality, higher = better!
	 * If its lower than 0, there is not enough to generate SecureRandom.
	 * @return
	 */
	public static int getEntropyFuelGauge(){
		return entropyFuelGauge*1000+pos;
	}
}
