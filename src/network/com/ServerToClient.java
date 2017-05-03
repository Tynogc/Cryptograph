package network.com;

import java.io.IOException;

import cryptoUtility.NetEncryptionFrame;
import main.Server;
import network.CommunicationProcess;
import network.TCPlinker;
import network.Writable;

public class ServerToClient extends CommunicationProcess{

	private long lastPingAt;
	public int pingTime;
	private boolean pingPending;
	private static final int PINGTIME = 5000;
	private int deadPings;
	
	private final Server server;
	
	public ServerToClient(Writable l, NetEncryptionFrame n, String ownName, Server s) {
		super(l, n, ownName);
		server = s;
	}
	
	public void refresh() throws IOException{
		if(System.currentTimeMillis()-lastPingAt > PINGTIME){
			if(pingPending){
				//Connection is Probably Dead...
				deadPings++;
				debug.Debug.println("*Dead Ping..."+deadPings);
				if(deadPings >= 3)
					throw new IOException("Connection Dead: No Answer to Ping");
			}
			linker.write(COMCONSTANTS.PING);
			lastPingAt = System.currentTimeMillis();
			pingPending = true;
		}
	}

	@Override
	protected boolean processIntern(String s) {
		if(s.compareTo(COMCONSTANTS.PING_AN)==0){
			pingTime = (int)(System.currentTimeMillis()-lastPingAt);
			pingPending = false;
			deadPings = 0;
			return true;
		}
		
		String[] st = s.split(COMCONSTANTS.DIV_HEADER);
		if(st.length<2)return false;
		
		//Key Validation
		if(st[0].compareTo(COMCONSTANTS.KEY_EXCHANGE_START)==0){
			add(new KeyExchange(linker, key, false, st[1], clientName));
			return true;
		}
		
		if(s.startsWith("[")){
			try {
				server.send(s);
			} catch (Exception e) {
				debug.Debug.println("*Error forwarding Message: "+e.toString(), debug.Debug.WARN);
			}
			return true;
		}
		
		return false;
	}

}
