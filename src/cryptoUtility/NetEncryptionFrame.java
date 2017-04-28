package cryptoUtility;

import crypto.RSAsaveKEY;

public class NetEncryptionFrame {

	private RSAsaveKEY myKey;
	private RSAsaveKEY userKey;
	
	private boolean myKeyWasVerifyed;
	
	private RSAsaveKEY mySuperKey;
	private RSAsaveKEY userSuperKey;
	
	private boolean superKeyVerifyed;
	
	public final String name;
	public final boolean isServer;
	
	public boolean theOtherSideKnowsTheKey = false;
	
	public NetEncryptionFrame(String n, boolean server){
		name = n;
		isServer = server;
		superKeyVerifyed = false;
		myKeyWasVerifyed = false;
	}

	public RSAsaveKEY getMyKey() {
		return myKey;
	}

	public void setMyKey(RSAsaveKEY key) {
		myKey = key;
	}
	
	public RSAsaveKEY getOtherKey() {
		return userKey;
	}

	public void setOtherKey(RSAsaveKEY key) {
		userKey = key;
	}
	
	public RSAsaveKEY getMySuperKey() {
		return mySuperKey;
	}

	public void setMySuperKey(RSAsaveKEY key) {
		mySuperKey = key;
	}
	
	public RSAsaveKEY getOtherSuperKey() {
		return userSuperKey;
	}

	public void setOtherSuperKey(RSAsaveKEY key) {
		userSuperKey = key;
	}
	
	public boolean isSuperKeyVerifyed(){
		return superKeyVerifyed;
	}
	
	public void verifySuperKey(){
		superKeyVerifyed = true;
	}
	
	public boolean isMyKeyVerifyer(){
		return myKeyWasVerifyed;
	}
	
	public void myKeyWasVerifyed(){
		myKeyWasVerifyed = true;
	}
}
