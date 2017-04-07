package cryptoUtility;

import java.security.SecureRandom;

public class Random {
	private static final char[] listChar =
			new char[]{'a','b','c','d','k','t','f','g','h','i','j','e','l','m','n','o','p','q',
			'r','s','u','v','w','x','z','y','1','0','2','3','5','4','6','7','8','9','+','-'};
	
	private static int[] entropy;
	//Assignment Position of Entropy
	private static int pos = 0;
	//Stack-Traverse Number, if >0 there is enough Entropy 
	private static int entropyFuelGauge = -10;
	//Counts the Entropy "Usage", every 1000 times, it decreases the EntropyFuelGauge
	private static int entropyUsed = 0;
	
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
	public static AdvancedSecureRandom generateSR(){
		if(additionalRnd == null)additionalRnd = new SecureRandom();
		//Not enough entropy!
		if(entropyFuelGauge < 0)
			return new AdvancedSecureRandom();
		
		return new AdvancedSecureRandom(generateSeed(125,45));
	}
	
	/**
	 * Generates a Random Seed, by using an existing SecureRandom and the entropy stored in the database.
	 * If the entropyFuelGauge is lower than 0 this should not be called!
	 * @return seed of the minimum length l1, maximum length l1+l2
	 */
	public static byte[] generateSeed(int l1, int l2){
		if(additionalRnd == null)additionalRnd = new SecureRandom();
		byte[] ba = generateBlankSeed(l1, l2);
		byte[] be = additionalRnd.generateSeed(ba.length); 
		for (int i = 0; i < ba.length; i++) {
			ba[i] = (byte)(ba[i] ^ be[i]);
		}
		return ba;
	}
	
	/**
	 * Generates a Pseudo-Random Seed,by using ONLY the entropy stored in the database.
	 * If the entropyFuelGauge is lower than 0 this should not be called!
	 * @return seed of the minimum length l1, maximum length l1+l2
	 */
	public static byte[] generateBlankSeed(int l1, int l2){
		byte[] ba = new byte[additionalRnd.nextInt(l2)+l1];
		for (int i = 0; i < ba.length; i++) {
			int p = additionalRnd.nextInt(entropy.length); //Position to take
			
			int shift = additionalRnd.nextInt(9);//BitShift up to 9 bit
			ba[i] = (byte)(entropy[p]>>shift);
		}
		return ba;
	}
	
	/**
	 * Generates A Random Byte, by traversing the stack of Entropy
	 * If there is not enough Entropy, it may return 0
	 * @return Random Byte
	 */
	public static byte aRandomByte(){
		entropyUsed++;
		if(entropyUsed>=1000){
			entropyUsed = 0;
			entropyFuelGauge--;
			if(entropyFuelGauge<-1)
				entropyFuelGauge = -1;
		}
		
		int p = additionalRnd.nextInt(entropy.length); //Position to take
		
		int shift = additionalRnd.nextInt(9);//BitShift up to 9 bit
		return (byte)(entropy[p]>>shift);
	}
	
	/**
	 * Returns an pseudo-Value do determine Entropy quality, higher = better!
	 * If its lower than 0, there is not enough to generate AdvancedSecureRandom Objects.
	 * @return
	 */
	public static int getEntropyFuelGauge(){
		return entropyFuelGauge*1000+(pos-entropyUsed);
	}
}
