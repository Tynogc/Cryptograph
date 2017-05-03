package gui.sub;

import main.GuiControle;
import network.TCPclient;
import network.TCPlinker;

public class RCM_Server extends RightClickMenu{

	private static final int CONNECT = 1;
	private static final int DISCONECT = 2;
	
	private static final int VIEW_KEY = 3;
	private static final int VALIDATE_KEY = 4;
	
	private final TCPclient server;
	
	public RCM_Server(TCPclient s) {
		super();
		server = s;
		
		if(!server.isConnected()){
			addRCMbutton("Connect", null, CONNECT);
		}else{
			addRCMbutton("Disconect", null, DISCONECT);
		}
		addRCMbutton("View Key", null, VIEW_KEY);
		if(server.isConnected()){
			addRCMbutton("Validate Key", null, VALIDATE_KEY);
		}
	}
	
	@Override
	protected void wasClicked(int id) {
		switch (id) {
		case CONNECT:
			server.retryConnect();
			break;
		case DISCONECT:
			TCPlinker t = server.getLinker();
			if(t != null)
				t.destroy();
			break;
			
		case VIEW_KEY:
			//TODO
			break;
			
		case VALIDATE_KEY:
			GuiControle.addMenu(new KeyValidation(xPos, yPos, server.ip, server.getNef(), true));
			break;

		default:
			break;
		}
	}

}
