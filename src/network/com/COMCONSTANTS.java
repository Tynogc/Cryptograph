package network.com;

public class COMCONSTANTS {

	public static final int UNENCRYPTED = 0;
	public static final int ENCRYPT_SERVER = 1;
	public static final int ENCRYPT_CLIENT = 2;
	
	public static final String DIV = "_";
	public static final String DIV_HEADER = "-:-";
	
	public static final String KEY_EXCHANGE_START = "KeyExchange-Start";
	public static final String KEY_EXCHANGE = "KeyExchange";
	public static final String KEY_VERIFY = "KeyVerify";
	public static final String KEY = "KEY";
	public static final String KEY_SUPER = "SUPERKEY";
	
	public static final String PING = "PING";
	public static final String PING_AN = "PING-AN";
	
	public static final String CONNECTION_ASK_START = "AskConnectionStart";
	public static final String CONNECTION_RESPONSE = "ConnectionStartResponse";
	
	//Respond on the same server as send, is used by one-Side local-Network-Connections
	public static final String SERVER_SAMESERVER = "server$same$";
	
	public static final String CHAT_MSG = "chatMessage";
}
