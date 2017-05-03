package user;

import gui.TopMenu;
import network.TCPclient;
import network.Writable;

public class ClientControle {

	public ClientList list;
	
	private TopMenu topMenu;
	
	public ClientControle(TopMenu t){
		topMenu = t;
		
		String myName = "Test2345";
		
		add(new TCPclient("192.168.178.20", 8001, myName));
		add(new TCPclient("localhost", 8002, myName));
		topMenu.setSideMenuServer(new SideDisplay[]{list.client.getSideDisplay(), list.next.client.getSideDisplay()});
	}
	
	public void refresh(){
		ClientList l = list;
		while(l != null){
			l.client.refresh();
			
			l = l.next;
		}
	}
	
	public void add(TCPclient c){
		if(list == null)
			list = new ClientList(c);
		else
			list.add(c);
	}
	
	public TCPclient getServerByName(String name){
		if(list == null)
			return null;
		
		ClientList l = list;
		while(l != null){
			if(l.client.ip.compareTo(name) == 0)
				return l.client;
			
			l = l.next;
		}
		return null;
	}
}

class ClientList{
	
	public final TCPclient client;
	
	public ClientList next;
	
	public ClientList(TCPclient c){
		client = c;
	}
	
	public void add(TCPclient c){
		if(next == null)
			next = new ClientList(c);
		else
			next.add(c);
	}
}


