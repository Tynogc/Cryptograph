package user;

import java.io.BufferedReader;
import java.io.FileReader;

import crypto.RSAsaveKEY;
import main.UserManager;
import network.TCPclient;
import network.com.COMCONSTANTS;
import network.com.ClientToClient;
import network.com.ConnectionBasics;

public class FriendsControle {

	public final ClientControle clients;
	
	public static FriendsControle friends;
	
	public FriendsList connectedFriends;
	
	private final gui.TopMenu topMenu;
	
	/**
	 * How to handle a friend in the List
	 */
	public InboundConnectionHandler handleKnownFriend;
	
	/**
	 * How to handle a manually started connection
	 */
	public InboundConnectionHandler handleClickedFriend;
	
	public FriendsControle(ClientControle c, gui.TopMenu t){
		clients = c;
		friends = this;
		
		topMenu = t;
		
		//LOAD friends...
		try {
			FileReader fr = new FileReader(UserManager.getUserDir()+"Friends.set");
			BufferedReader br = new BufferedReader(fr);
			
			String s = br.readLine();
			while (s != null) {
				if(!s.startsWith("//")){
					String[] st = s.split(" ");
					if(st.length>=2){
						boolean sameS = st[1].compareTo(COMCONSTANTS.SERVER_SAMESERVER)==0;
						String[] st2 = new String[st.length-1];
						for (int i = 1; i < st.length; i++) {
							st2[i-1] = st[i];
						}
						UserFriendConnection ufc = new UserFriendConnection(st[0], st2, sameS);
						if(connectedFriends == null)
							connectedFriends = new FriendsList(ufc, this);
						else
							connectedFriends.add(new FriendsList(ufc, this));
					}
					
				}
				s = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			debug.Debug.println("*ERROR loading Friends: "+e.getMessage(), debug.Debug.ERROR);
		}
		if(connectedFriends != null)
			debug.Debug.println(connectedFriends.count()+" Friends loaded");
		refreshSideDisplays();
	}
	
	private void refreshSideDisplays(){
		if(connectedFriends == null)return;
		int i = connectedFriends.count();
		SideDisplay[] sd = new SideDisplay[i];
		i = 0;
		FriendsList fl = connectedFriends;
		while (fl != null) {
			sd[i] = fl.sideDisplay;
			i++;
			fl = fl.next;
		}
		
		topMenu.setSideMenuFriends(sd);
	}
	
	/**
	 * Finds the given Server and returns it's TCPclient-Object
	 * @param friend
	 * @return the TCPclient connecting to the Server, null if the server can't be found.
	 */
	public TCPclient openFriendChannel(String friend){
		//TODO test for match with friends-Database
		try {
			String server = friend.split("@")[1];
			return clients.getServerByName(server);
		} catch (Exception e) {
			debug.Debug.println("*Friend can't be found! Error: "+e.toString(), debug.Debug.WARN);
		}
		return null;
	}
	
	/**
	 * Adds a new friend with an connection to be started
	 * @param f
	 */
	public boolean add(ClientToClient f, String name){
		if(connectedFriends == null)
			return false;
		
		if(connectedFriends.addC2C(f, name)){
			if(handleKnownFriend != null)
				handleKnownFriend.connectionInbound(connectedFriends.getFriendByName(name));
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the ClientToCliet-Object related to the name
	 * @throws Exception 
	 */
	public ClientToClient getClientByName(String name) throws Exception{
		FriendsList fl = connectedFriends;
		while (fl != null) {
			if(fl.connectionName.compareTo(name) == 0)
				if(fl.client != null)
					return fl.client;
			
			fl = fl.next;
		}
		throw new Exception("Element not found!");
	}
	
	/**
	 * Runs the loop
	 */
	public void update(){
		
	}
	
	/**
	 * Checks the given Public-Key to the Friends Data-Base, returns true if the friend match with the Key
	 * @param key
	 * @param friend
	 * @return
	 */
	public boolean checkKeyMatch(RSAsaveKEY key, String friend){
		
		return true; //TODO
	}
	
	public void askConnection(String friendsName, TCPclient recivingEnd, boolean sameServer){
		String friend = friendsName;
		
		TCPclient t = clients.getServerByName(friendsName.split("@")[1]);
		if(t == null){
			debug.Debug.println("* Can't open Connection to "+friendsName, debug.Debug.ERROR);
			debug.Debug.println(" Friend's Server not available!", debug.Debug.SUBERR);
			return;
		}
		if(!recivingEnd.isConnected()){
			debug.Debug.println("* Can't open Connection to "+friendsName, debug.Debug.ERROR);
			debug.Debug.println(" Server is not connected!", debug.Debug.SUBERR);
			return;
		}
		sameServer = sameServer | clients.isServerASameServer(friendsName);
		if(sameServer){
			recivingEnd = t;
			friend = friendsName.split("@")[0]+"@"+COMCONSTANTS.SERVER_SAMESERVER;
		}
		ClientToClient cl = ConnectionBasics.askConnection(friend, recivingEnd, t);
		
		if(!add(cl, friendsName))
			debug.Debug.println("*ERROR FriendsControle: Connection was Requested,"
					+ " but friend not found!", debug.Debug.ERROR);
		
		recivingEnd.addToYourComProcess(cl);
	}
	
	public void askConnection(UserFriendConnection conn){
		if(conn.sameServer){
			askConnection(conn.connectionName, null, true);
			return;
		}
		TCPclient c = null;
		for (int i = 0; i < conn.yourServer.length; i++) {
			debug.Debug.println("Probing server: "+conn.yourServer[i]);
			c = clients.getServerByName(conn.yourServer[i]);
			if(c != null)
				break;
		}
		if(c == null){
			debug.Debug.println("* Can't open Connection to "+conn.connectionName, debug.Debug.ERROR);
			debug.Debug.println(" No Reciving Server available!", debug.Debug.SUBERR);
			return;
		}
		askConnection(conn.connectionName, c, false);
	}
	
	public void test(){
		askConnection("Test1234@localhost", null, true);
	}

}

class UserFriendConnection{
	
	public final String connectionName;
	public String[] yourServer;
	public final boolean sameServer;
	
	public UserFriendConnection(String name, String[] server, boolean same){
		connectionName = name;
		yourServer = server;
		sameServer = same;
	}
}
