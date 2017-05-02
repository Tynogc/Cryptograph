package network.com;

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
		addToSubsets(new KeyExchange(this, key, true, null, ownName));
		connectionTo = conTo;
	}

	@Override
	protected boolean processIntern(String s) {
		String[] st = s.split(COMCONSTANTS.DIV_HEADER);
		if(st.length<2)return false;
		
		
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
		s = RSAcrypto.encrypt(s, key.getMyKey(), false);
		s = RSAcrypto.encrypt(s, key.getOtherKey(), true);
		linker.write(ConnectionBasics.generateHeader(clientName, connectionTo) + COMCONSTANTS.DIV_HEADER + s);
	}

}
