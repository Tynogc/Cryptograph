package network.com;

import java.security.spec.InvalidKeySpecException;

import crypto.RSAcrypto;
import cryptoUtility.NetEncryptionFrame;
import network.CommunicationProcess;
import network.Writable;

public class ClientToClient extends CommunicationProcess implements Writable{

	private CommunicationProcess subCom;
	
	/**
	 * Hasn't send response
	 */
	public boolean needsConnection;
	
	/**
	 * Connection Partner for this object, is layed in by the Response-Stream.
	 * Follows Header-Notation: [user@server.com]
	 */
	public final String connectionTo;
	
	/**
	 * Generates a connection to a Client, the other Client must have been previously accepted.
	 * @param l Writable for the Response-Stream, must be on the Server for return connection.
	 * @param n the Frame to carry the Session- and Super-Keys for this connection, key will be verified as soon
	 * as this Object is created.
	 * @param conTo The Server-Header to call for this connection, must follow Header-Notation
	 */
	public ClientToClient(Writable l, NetEncryptionFrame n, String conTo, String ownName) {
		super(l, n, ownName);
		connectionTo = conTo;
	}

	@Override
	protected boolean processIntern(String s) {
		String[] st = s.split(COMCONSTANTS.DIV_HEADER);
		if(st.length<2)return false;
		
		if(s.startsWith("[")){
			if(ConnectionBasics.divideHeader(st[0])[0].compareTo(connectionTo) == 0){
				//This is for you :)
				if(st[1].compareTo(COMCONSTANTS.CONNECTION_RESPONSE) == 0){
					try {
						ConnectionBasics.processResponse(this, st);
					} catch (ArrayIndexOutOfBoundsException | InvalidKeySpecException e) {
						debug.Debug.println("*Error reciving connection response: "+e.toString(), debug.Debug.ERROR);
					}
					return true;
				}
				
				String sdc = s;
				if(key.getOtherKey() == null)
					return false;
				
				try {
					sdc = RSAcrypto.decrypt(sdc, key.getMyKey(), false);
					sdc = RSAcrypto.decrypt(sdc, key.getMyKey(), false);
				} catch (Exception e) {
					debug.Debug.println("*ERROR decrypting incomming Message: "+e.toString(), debug.Debug.ERROR);
					return false;
				}
				
				return useSubsets(sdc);
			}
		}
		
		return false;
	}
	
	protected boolean useSubsets(String s){
		if(subCom != null)
			if(subCom.hasTerminated())
				subCom = subCom.sort();
		
		if(subCom != null)
			return subCom.processString(s);
		
		return false;
	}
	
	public void addToSubsets(CommunicationProcess c){
		if(subCom == null){
			subCom = c;
		}else{
			subCom.add(c);
		}
	}

	/**
	 * All Methods further up should request the Write from this Object,
	 * so the Message is encrypted for the other client.
	 */
	@Override
	public void write(String s) {
		System.out.println(s);
		if(key.getOtherKey() == null){
			debug.Debug.println(s+", should be send, however ther is no key to do so...", debug.Debug.WARN);
			return;
		}
		s = RSAcrypto.encrypt(s, key.getMyKey(), false);
		s = RSAcrypto.encrypt(s, key.getOtherKey(), true);
		linker.write(ConnectionBasics.generateHeader(clientName, connectionTo) + COMCONSTANTS.DIV_HEADER + s);
	}
	
	public NetEncryptionFrame getNef(){
		return key;
	}

}
