package network.com;

import cryptoUtility.NetEncryptionFrame;
import network.CommunicationProcess;
import network.TCPlinker;
import network.Writable;

public class ClientToServer extends CommunicationProcess{

	public ClientToServer(Writable l, NetEncryptionFrame n) {
		super(l, n);
	}

	@Override
	protected boolean processIntern(String s) {
		if(s.compareTo(COMCONSTANTS.PING)==0){
			//Answer the Ping!
			linker.write(COMCONSTANTS.PING_AN);
			return true;
		}

		String[] st = s.split(COMCONSTANTS.DIV_HEADER);
		if(st.length<2)return false;
		
		//Key Validation
		if(st[0].compareTo(COMCONSTANTS.KEY_EXCHANGE_START)==0){
			add(new KeyExchange(linker, key, false, st[1]));
			return true;
		}
		
		return false;
	}

}
