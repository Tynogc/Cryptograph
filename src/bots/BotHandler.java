package bots;

import user.FriendsList;

public class BotHandler extends Thread{

	private final FriendsList friend;
	
	private boolean needInit;
	
	public BotHandler(FriendsList f){
		super(f.connectionName);
		friend = f;
		needInit = true;
	}
	
	@Override
	public void run() {
		try {
			while(true){
				loop();
			}
		} catch (Exception e) {
			debug.Debug.println("*Thread Terminated "+getName(), debug.Debug.ERROR);
			debug.Debug.println("Cause: "+e.toString(), debug.Debug.SUBERR);
		}
	}
	
	private void loop(){
		try {
			sleep(50);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(friend.client.isKeyVerified() && needInit){
			for (int j = 0; j < BotControle.initialStatement.length; j++) {
				friend.client.writeChat(BotControle.initialStatement[j]);
			}
			needInit = false;
		}
	}
}
