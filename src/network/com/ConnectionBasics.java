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
	public static ClientToClient connectionRequested(String[] st, RSAsaveKEY mySuperKey, String currentServer) 
			throws ArrayIndexOutOfBoundsException, InvalidKeySpecException {
		String friend = divideHeader(st[0])[1];
		debug.Debug.println("*Connection is Requested by: "+friend, debug.Debug.MESSAGE);
		debug.Debug.println("  "+st[0], debug.Debug.MESSAGE);
		try{
			FriendsControle.friends.getClientByName(friend);
			debug.Debug.println("-Howerver, your already connected!", debug.Debug.ERROR);
		}catch (Exception e) {
			//The Element dosn't exist, thats good
		}
		String key = st[2];
		String superKey = key.split(COMCONSTANTS.DIV)[0];
		String certifikat = key.split(COMCONSTANTS.DIV)[2];
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
		
		String friendIntern = friend;
		if(friend.split("@")[1].compareTo(COMCONSTANTS.SERVER_SAMESERVER) == 0){
			friendIntern = friend.split("@")[0] + "@" + currentServer.split("@")[1];
			debug.Debug.println("-Connections was asked on server$Same$; Is treated as: "+friendIntern);
		}
		
		TCPclient server = FriendsControle.friends.openFriendChannel(friendIntern);
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
		
		////////////////////////////////////////////////////////////////////
		//Send response
		String response = generateHeader(divideHeader(st[0])[0], friend);
		response += COMCONSTANTS.DIV_HEADER+COMCONSTANTS.CONNECTION_RESPONSE;
		response += COMCONSTANTS.DIV_HEADER+nef.getMySuperKey().getPublicKeyString();
		response += COMCONSTANTS.DIV+nef.getMyKey().getPublicKeyString();
		server.write(response);
		
		ClientToClient cl = new ClientToClient(server, nef, friend, divideHeader(st[0])[0]);
		debug.Debug.println("-Response send: Channel open!", debug.Debug.MESSAGE);
		debug.Debug.println("  "+response.split(COMCONSTANTS.DIV_HEADER)[0], debug.Debug.MESSAGE);
		cl.addToSubsets(new KeyExchange(cl, nef, true, null, cl.clientName));
		return cl;
	}
	
	public static void processResponse(ClientToClient cl, String[] st)
			throws ArrayIndexOutOfBoundsException, InvalidKeySpecException{
		debug.Debug.println("*Got response for connection request to "+cl.connectionTo);
		String keys = st[2];
		NetEncryptionFrame nef = cl.getNef();
		
		if(nef.getOtherKey() != null)
			debug.Debug.println("This channel seamse already open!", debug.Debug.ERROR);//TODO
		
		nef.setOtherSuperKey(new RSAsaveKEY(keys.split(COMCONSTANTS.DIV)[0]));
		//TODO check superkey vs. Database
		nef.setOtherKey(new RSAsaveKEY(keys.split(COMCONSTANTS.DIV)[1]));
		debug.Debug.println("Conected!", debug.Debug.MESSAGE);
		debug.Debug.println("  "+generateHeader(cl.clientName, cl.connectionTo), debug.Debug.MESSAGE);
		cl.addToSubsets(new KeyExchange(cl, nef, true, null, cl.clientName));
	}

	public static ClientToClient askConnection(String friend, TCPclient responseChannel, Writable sendChannel){
		String s;
		if(friend.endsWith(COMCONSTANTS.SERVER_SAMESERVER)){
			String mn = responseChannel.myName;
			mn = mn.split("@")[0]+"@"+COMCONSTANTS.SERVER_SAMESERVER;
			s = generateHeader(mn, friend);
		}else{
			s = generateHeader(responseChannel.myName, friend);
		}
		NetEncryptionFrame nef = new NetEncryptionFrame(friend, false);
		RSAsaveKEY myKey = null;
		do{
			try {
				myKey = RSAsaveKEY.generateKey(2048, true, false, 3, null);
			} catch (Exception e) {
				debug.Debug.printExeption(e);
			}
		}while(myKey == null);
		nef.setMyKey(myKey);
		nef.setMySuperKey(responseChannel.getNef().getMySuperKey());
		
		s+=COMCONSTANTS.DIV_HEADER+COMCONSTANTS.CONNECTION_ASK_START;
		s+=COMCONSTANTS.DIV_HEADER+nef.getMySuperKey().getPublicKeyString();
		s+=COMCONSTANTS.DIV+nef.getMyKey().getPublicKeyString();
		s+=COMCONSTANTS.DIV+"Certificate";
		
		sendChannel.write(s);
		
		debug.Debug.println("* Asking connection: "+s.split(COMCONSTANTS.DIV_HEADER)[0], debug.Debug.COM);
		
		return new ClientToClient(responseChannel, nef, friend, responseChannel.myName);
	}
	
	private static final String FROM = "[FROM: ";
	private static final String TO = "[TO: ";
	
	/**
	 * Divides the Header-String into Two parts: From and To
	 * @param header
	 * @return [To][From], null if the Header is wrong
	 */
	public static String[] divideHeader(String header){
		String[] s = header.split("]");
		
		String from = "";
		String to = "";
		
		for (int i = 0; i < s.length; i++) {
			if(s[i].startsWith(FROM)){
				from = s[i].substring(FROM.length());
			}
			if(s[i].startsWith(TO)){
				to = s[i].substring(TO.length());
			}
		}
		
		System.out.println("-"+from+"-"+to);
		return new String[]{to, from};
	}
	
	/**
	 * Generates standard-Header-Notation of the sender and reciver
	 * @param from
	 * @param to
	 * @return
	 */
	public static String generateHeader(String from, String to){
		return new String(TO+to+"]"+FROM+from+"]");
	}
}
