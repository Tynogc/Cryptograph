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
		try{
			FriendsControle.friends.getClientByName(friend);
			debug.Debug.println("-Howerver, your already connected!", debug.Debug.ERROR);
		}catch (Exception e) {
			//The Element dosn't exist, thats good
		}
		String key = st[2];
		String superKey = key.split(COMCONSTANTS.DIV)[0];
		key = key.split(COMCONSTANTS.DIV)[1];
		
		//TODO check friend (+friend's superKey) in database
		debug.Debug.println("-Super Key is Known");
		
		RSAsaveKEY rsaSuperKey = new RSAsaveKEY(superKey);
		RSAsaveKEY rsaKey = new RSAsaveKEY(key);
		
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
			debug.Debug.println("-Server name has no match!", debug.Debug.ERROR);
			return null;
		}
		if(!server.isConnected()){
			debug.Debug.println("-You are not connecte to the Server!", debug.Debug.ERROR);
			return null;
		}
		
		//TODO check server certificate (Time sensitiv!)
		debug.Debug.println("-Certificate checked!");
		
		return new ClientToClient(server, nef, friend);
	}
	
	
}
