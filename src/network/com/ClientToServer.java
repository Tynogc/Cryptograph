package network.com;

import cryptoUtility.NetEncryptionFrame;
import network.CommunicationProcess;
import network.TCPlinker;

public class ClientToServer extends CommunicationProcess{

	public ClientToServer(TCPlinker l, NetEncryptionFrame n) {
		super(l, n);
	}

	@Override
	protected boolean processIntern(String s) {
		// TODO Auto-generated method stub
		return false;
	}

}
