package cryptoUtility;

import crypto.RSAsaveKEY;

public class NetEncryptionFrame {

	private RSAsaveKEY key;
	
	public boolean theOtherSideKnowsTheKey = false;
	
	public NetEncryptionFrame(){
		
	}

	public RSAsaveKEY getKey() {
		return key;
	}

	public void setKey(RSAsaveKEY key) {
		this.key = key;
	}
}
