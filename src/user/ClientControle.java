package user;

import java.io.BufferedReader;
import java.io.FileReader;

import gui.TopMenu;
import main.UserManager;
import network.TCPclient;

public class ClientControle {

	public ClientList list;
	
	private TopMenu topMenu;
	
	public ClientControle(TopMenu t){
		topMenu = t;
		
		int port = 8001;
		
		try {
			FileReader fr = new FileReader(UserManager.getUserDir()+"Servers.set");
			BufferedReader br = new BufferedReader(fr);
			
			String s = br.readLine();
			while (s != null) {
				if(!s.startsWith("//")){
					String[] st = s.split(" ");
					System.out.println(s);
					if(st.length>=2){
						String ser = st[0];
						String name = st[1];
						boolean sameServer = false;
						if(st.length>=3){
							if(st[2].contains("same")){
								sameServer = true;
							}
						}
						add(new TCPclient(ser, port, name), sameServer);
						port++;
					}
				}
				s = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			debug.Debug.println("*ERROR loading Servers: "+e.getMessage(), debug.Debug.ERROR);
		}
		
		refreshSideDisplays();
	}
	
	private void refreshSideDisplays(){
		if(list == null)return;
		int i = list.count();
		SideDisplay[] sd = new SideDisplay[i];
		i = 0;
		ClientList fl = list;
		while (fl != null) {
			sd[i] = fl.client.getSideDisplay();
			i++;
			fl = fl.next;
		}
		
		topMenu.setSideMenuServer(sd);
	}
	
	public void refresh(){
		ClientList l = list;
		while(l != null){
			l.client.refresh();
			
			l = l.next;
		}
	}
	
	public void add(TCPclient c, boolean sameServer){
		if(list == null)
			list = new ClientList(c, sameServer);
		else
			list.add(c, sameServer);
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
	
	public boolean isServerASameServer(String name){
		if(list == null)
			return false;
		
		ClientList l = list;
		while(l != null){
			if(l.client.ip.compareTo(name) == 0)
				return l.needsToBeSameServer;
			
			l = l.next;
		}
		return false;
	}
}

class ClientList{
	
	public final TCPclient client;
	public final boolean needsToBeSameServer;
	
	public ClientList next;
	
	public ClientList(TCPclient c, boolean sameServer){
		client = c;
		needsToBeSameServer = sameServer;
	}
	
	public void add(TCPclient c, boolean sameServer){
		if(next == null)
			next = new ClientList(c, sameServer);
		else
			next.add(c, sameServer);
	}
	
	public int count(){
		if(next == null)
			return 1;
		return next.count()+1;
	}
}


