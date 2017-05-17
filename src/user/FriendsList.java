package user;

import network.com.ClientToClient;

public class FriendsList {

	public final String connectionName;
	public final UserFriendConnection userFriendConn;
	public final FriendsControle controle;
	public final SideDisplay sideDisplay;
	
	public ClientToClient client;
	
	public FriendsList next;
	
	public FriendsList(UserFriendConnection u, FriendsControle c){
		connectionName = u.connectionName;
		userFriendConn = u;
		
		controle = c;
		
		sideDisplay = new SideDisplay(connectionName){
			@Override
			public void wasClicked() {
				if(client == null){
					controle.askConnection(userFriendConn);
				}else{
					//TODO open chat
				}
			}
			
			@Override
			public void wasRightClicked() {
				
			};
		};
	}
	
	public void add(FriendsList f){
		if(next == null)
			next = f;
		else
			next.add(f);
	}
	
	public boolean addC2C(ClientToClient c, String handleAs){
		if(handleAs.compareTo(connectionName) == 0){
			client = c;
			return true;
		}
		if(next == null)
			return false;
		
		return next.addC2C(c, handleAs);
	}
	
	public int count(){
		if(next == null)
			return 1;
		
		return next.count()+1;
	}
	
	public FriendsList getFriendByName(String name){
		if(name.compareTo(connectionName) == 0)
			return this;
		if(next == null)
			return null;
		return next.getFriendByName(name);
	}
}