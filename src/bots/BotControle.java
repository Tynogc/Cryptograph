package bots;

import gui.TopMenu;
import gui.chat.ChatSite;

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
	
	public static final String bot = "#BOT# ";
	public static final String windowStart = "WIN_START";
	public static final String windowEnd = "WIN_END";
	
	public BotControle(){
		this(null, null);
	}
	
	public BotControle(String dir, ChatSite test){
		
		String s;
		int i = 0;
		if(dir == null)
			dir = UserManager.getUserDir()+"init.txt";
		try {
			FileReader fr = new FileReader(dir);
			BufferedReader br = new BufferedReader(fr);
			
			s = br.readLine();
			while (s != null) {
				if(!s.startsWith(windowStart) && !s.startsWith(windowEnd) && !s.startsWith("//"))
					i++;
				s = br.readLine();
			}
			
			fr = new FileReader(dir);
			br = new BufferedReader(fr);
			
			s = br.readLine();
			initialStatement = new String[i];
			i = 0;
			boolean win = false;
			while (s != null) {
				if(s.startsWith(windowStart)){
					win = true;
				}else if(s.startsWith(windowEnd)){
					win = false;
				}else if(s.startsWith("//")){
					
				}else{
					if(win)
						initialStatement[i] = bot+s;
					else
						initialStatement[i] = s;
					
					i++;
				}
				s = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			debug.Debug.println("*ERROR loading Bot: "+e.getMessage(), debug.Debug.ERROR);
			return;
		}
		debug.Debug.println("-Bot loaded: " + initialStatement.length + " Lines of initial Statement!");
		
		if(test != null){
			for (int j = 0; j < initialStatement.length; j++) {
				if(initialStatement[j].startsWith(bot))
					test.command(initialStatement[j].substring(bot.length()));
			}
			return;
		}
		
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
