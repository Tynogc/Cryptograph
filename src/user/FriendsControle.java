package user;

import crypto.RSAsaveKEY;
import network.TCPclient;
import network.com.COMCONSTANTS;
import network.com.ClientToClient;
import network.com.ConnectionBasics;

public class FriendsControle {

	public final ClientControle clients;
	
	public static FriendsControle friends;
	
	public FriendsList connectedFriends;
	
	public FriendsControle(ClientControle c){
		clients = c;
		friends = this;
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
	public void add(ClientToClient f){
		FriendsList fl = new FriendsList(f);
		if(connectedFriends == null){
			connectedFriends = fl;
		}else{
			connectedFriends.add(fl);
		}
	}
	
	/**
	 * Returns the ClientToCliet-Object related to the name
	 * @throws Exception 
	 */
	public ClientToClient getClientByName(String name) throws Exception{
		FriendsList fl = connectedFriends;
		while (fl != null) {
			if(fl.com.connectionTo.compareTo(name) == 0)
				return fl.com;
			
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
	
	public void test(){
		TCPclient t = clients.getServerByName("192.168.178.20");
		ConnectionBasics.askConnection("Test1234@192.168.178.20", t, t);
	}
}

class FriendsList{
	public final ClientToClient com;
	public FriendsList next;
	
	public FriendsList(ClientToClient c){
		com = c;
	}
	
	public void add(FriendsList l){
		if(next == null){
			next = l;
			return;
		}
		next.add(l);
	}
	
	public void remove(ClientToClient l, FriendsList befor){
		if(l == com){
			befor.next = next;
			this.next = null;
		}
		if(next != null){
			next.remove(l, this);
		}
	}
}
