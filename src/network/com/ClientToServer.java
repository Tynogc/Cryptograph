package network.com;

import user.FriendsControle;
import user.SideDisplay;
import cryptoUtility.NetEncryptionFrame;
import network.CommunicationProcess;
import network.TCPlinker;
import network.Writable;

public class ClientToServer extends CommunicationProcess{

	private final SideDisplay sideDisplay;
	
	public ClientToServer(Writable l, NetEncryptionFrame n, String ownName, SideDisplay d) {
		super(l, n, ownName);
		sideDisplay = d;
	}

	@Override
	protected boolean processIntern(String s) {
		if(s.startsWith(COMCONSTANTS.PING)){
			try {
				String k = s.substring(COMCONSTANTS.PING.length()+1);
				int p = Integer.parseInt(k);
				sideDisplay.secondLine = "Ping: "+p;
			} catch (Exception e) {}
			//Answer the Ping!
			linker.write(COMCONSTANTS.PING_AN);
			return true;
		}

		String[] st = s.split(COMCONSTANTS.DIV_HEADER);
		if(st.length<2)return false;
		
		//Key Validation
		if(st[0].compareTo(COMCONSTANTS.KEY_EXCHANGE_START)==0){
			add(new KeyExchange(linker, key, false, st[1], clientName));
			return true;
		}
		
		
		//Ask start of connection
		if(st[1].compareTo(COMCONSTANTS.CONNECTION_ASK_START)==0){
			try {
				add(ConnectionBasics.connectionRequested(st, key.getMySuperKey(), clientName));
			} catch (Exception e) {
				debug.Debug.println("*Connection was Requested, but Failed: "+e.toString(), debug.Debug.WARN);
			}
			return true;
		}
		
		return false;
	}

}
