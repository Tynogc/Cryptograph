package cryptoUtility;

import java.security.SecureRandom;

public class AdvancedSecureRandom extends SecureRandom{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5468341343708096110L;
	
	public AdvancedSecureRandom(byte[] seed) {
		super(seed);
	}
	
	public AdvancedSecureRandom(){
		super();
	}
	
	@Override
	public final synchronized void nextBytes(byte[] arg0) {
		if(arg0 == null)return;
		super.nextBytes(arg0);
		for (int i = 0; i < arg0.length; i++) {
			arg0[i] = (byte)(arg0[i] ^ Random.aRandomByte());
			//System.out.println(arg0[i]);
		}
	}

}
