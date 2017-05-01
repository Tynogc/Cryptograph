package network.com;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.spec.InvalidKeySpecException;

import network.TCPclient;
import network.Writable;
import crypto.RSAcrypto;
import crypto.RSAsaveKEY;
import cryptoUtility.NetEncryptionFrame;
import user.FriendsControle;

/**
 * This class provides two Methods for asking/Answering Connection-Requests for E2EE
 * @author Sven T. Schneider
 */

public class ConnectionBasics {

	/**
	 * Input the Standard Connection-Request-String and returns an ClientToClient-Object,
	 * setup to handle the Comunication.
	 * @param st
	 * @return
	 */
	public static ClientToClient connectionRequested(String[] st, RSAsaveKEY mySuperKey) 
			throws ArrayIndexOutOfBoundsException, InvalidKeySpecException {
		String friend = st[1];
		debug.Debug.println("*Connection is Requested by: "+friend, debug.Debug.MESSAGE);
		String key = st[2];
		String superKey = key.split(COMCONSTANTS.DIV)[0];
		key = key.split(COMCONSTANTS.DIV)[1];
		
		RSAsaveKEY rsaSuperKey = new RSAsaveKEY(superKey);
		RSAsaveKEY rsaKey = new RSAsaveKEY(key);
		debug.Debug.println("-Decrypted Session-Key");
		
		RSAsaveKEY myKey = null;
		do{
			try {
				myKey = RSAsaveKEY.generateKey(2048, true, false, 3, null);
			} catch (Exception e) {
				debug.Debug.printExeption(e);
			}
		}while(myKey == null);
		debug.Debug.println("-Generated Key");
		
		NetEncryptionFrame nef = new NetEncryptionFrame(friend, false);
		nef.setMySuperKey(mySuperKey);
		nef.setMyKey(myKey);
		nef.setOtherSuperKey(rsaSuperKey);
		nef.setOtherKey(rsaKey);
		
		TCPclient server = FriendsControle.friends.openFriendChannel(friend);
		if(server == null){
			debug.Debug.println("-Server name has no conected match!", debug.Debug.ERROR);
			return null;
		}
		
		return new ClientToClient(server, nef, friend);
	}
}
