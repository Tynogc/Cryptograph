package user;

import crypto.RSAsaveKEY;
import network.TCPclient;
import network.com.COMCONSTANTS;
import network.com.ClientToClient;

public class FriendsControle {

	public final ClientControle clients;
	
	public static FriendsControle friends;
	
	public FriendsControle(ClientControle c){
		clients = c;
	}
	
	public TCPclient openFriendChannel(String friend){
		//TODO test for match with friends-Database
		try {
			String server = friend.split("@")[1];
			return clients.getServerByName(friend);
		} catch (Exception e) {
			debug.Debug.println("*Friend can't be found..."+e.toString(), debug.Debug.WARN);
		}
		return null;
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
}
