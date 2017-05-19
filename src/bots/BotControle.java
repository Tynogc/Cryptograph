package bots;

import gui.TopMenu;

import java.io.BufferedReader;
import java.io.FileReader;

import user.ClientControle;
import user.FriendsControle;
import user.FriendsList;
import user.InboundConnectionHandler;
import main.PicLoader;
import main.UserManager;

public class BotControle extends Thread{

	public user.FriendsControle friends;
	public user.ClientControle servers;
	
	public static String[] initialStatement;
	
	public BotControle(){
		
		String s;
		int i = 0;
		try {
			FileReader fr = new FileReader(UserManager.getUserDir()+"init.txt");
			BufferedReader br = new BufferedReader(fr);
			
			s = br.readLine();
			while (s != null) {
				i++;
				s = br.readLine();
			}
			
			fr = new FileReader(UserManager.getUserDir()+"init.txt");
			br = new BufferedReader(fr);
			
			s = br.readLine();
			initialStatement = new String[i];
			i = 0;
			while (s != null) {
				initialStatement[i] = s;
				i++;
				s = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			debug.Debug.println("*ERROR loading Bot: "+e.getMessage(), debug.Debug.ERROR);
			return;
		}
		debug.Debug.println("-Bot loaded: " + initialStatement.length + " Lines of initial Statement!");
		new PicLoader();
		TopMenu t = new TopMenu();
		servers = new ClientControle(t);
		friends = new FriendsControle(servers, t);
		
		friends.handleKnownFriend = new InboundConnectionHandler() {
			@Override
			public void connectionInbound(FriendsList friend) {
				if(friend.client == null)
					return;
				
				new BotHandler(friend).start();
			}
		};
	}
	
	@Override
	public void run() {
		try {
			while(true){
				loop();
			}
		} catch (Exception e) {
			debug.Debug.println("FATAL: ", debug.Debug.FATAL);
			debug.Debug.printExeption(e);
		}
	}
	
	public void loop(){
		try {
			sleep(100);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		servers.refresh();
		friends.update();
	}
}
