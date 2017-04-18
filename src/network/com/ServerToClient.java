package network.com;

import cryptoUtility.NetEncryptionFrame;
import network.CommunicationProcess;
import network.TCPlinker;

public class ServerToClient extends CommunicationProcess{

	public ServerToClient(TCPlinker l, NetEncryptionFrame n) {
		super(l, n);
	}

	@Override
	protected boolean processIntern(String s) {
		
		return false;
	}

}
