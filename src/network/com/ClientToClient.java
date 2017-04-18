package network.com;

import cryptoUtility.NetEncryptionFrame;
import network.CommunicationProcess;
import network.TCPlinker;

public class ClientToClient extends CommunicationProcess{

	public ClientToClient(TCPlinker l, NetEncryptionFrame n) {
		super(l, n);
	}

	@Override
	protected boolean processIntern(String s) {
		
		return false;
	}

}
