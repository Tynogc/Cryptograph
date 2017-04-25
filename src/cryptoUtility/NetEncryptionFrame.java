package cryptoUtility;

import crypto.RSAsaveKEY;

public class NetEncryptionFrame {

	private RSAsaveKEY myKey;
	
	public boolean theOtherSideKnowsTheKey = false;
	
	public NetEncryptionFrame(){
		
	}

	public RSAsaveKEY getMyKey() {
		return myKey;
	}

	public void setMyKey(RSAsaveKEY key) {
		myKey = key;
	}
}
