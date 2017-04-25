package network.com;

import cryptoUtility.NetEncryptionFrame;
import network.CommunicationProcess;
import network.TCPlinker;

public class KeyExchange extends CommunicationProcess {
	
	private static final String newKey = "NewKey";
	
	private final boolean started;
	private final boolean server;
	
	private int state;
	
	public KeyExchange(TCPlinker l, NetEncryptionFrame n, boolean started, boolean server) {
		super(l, n);
		this.started = started;
		this.server = server;
		
		state = 0;
		
		if(started)
			linker.write(COMCONSTANTS.KEY_EXCHANGE);
		else
			linker.write(newKey);
	}

	@Override
	protected boolean processIntern(String s) {
		if(started)
			return processStarter();
		return processReciver();
	}
	
	private boolean processStarter(){
		
		return false;
	}
	
	private boolean processReciver(){
		
		return false;
	}

}
