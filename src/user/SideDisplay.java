package user;

import java.awt.image.BufferedImage;

import main.PicLoader;

public class SideDisplay {

	public static final int SERVER_NO_CONNECTION = 2000;
	public static final int SERVER_CONNECTING = 2001;
	public static final int SERVER_ONLINE = 1000;
	public static final int SERVER_MIRROW = 1001;
	public static final int SERVER_USER = 1002;
	
	public static final int ADITION_SERVER_MIRROW = 100;
	public static final int ADITION_SERVER_USER = 200;

	public static final int FRIEND_NO_CONNECTION = 5000;
	public static final int FRIEND_CONNECTING = 5001;
	public static final int FRIEND_ONLINE = 3002;
	public static final int FRIEND_OFFLINE = 5002;
	public static final int FRIEND_NOT_HERE = 3003;
	public static final int FRIEND_BUSY = 4000;
	
	public static final int NO_STATUS = 9999;
	
	public static final String[] names = new String[]{
			"All","Conected Servers","No Connection","Online","Busy","Offline","6","7","8","No Status"
	};
	
	private static BufferedImage[] icons;
	private static final int[] iconNumbers = new int[]{
		NO_STATUS, SERVER_NO_CONNECTION, SERVER_CONNECTING, SERVER_ONLINE, SERVER_MIRROW, SERVER_USER	
	};
	
	public int status = NO_STATUS;
	public int addition;
	public String mainString;
	public String secondLine;
	
	public SideDisplay(){};
	public SideDisplay(String s){
		mainString  =s;
	}
	public SideDisplay(String s1, String s2){
		mainString = s1;
		secondLine = s2;
	}
	public SideDisplay(String s1, String s2, int s){
		mainString = s1;
		secondLine = s2;
		status = s;
	}
	public SideDisplay(String s1, String s2, int s, int a){
		mainString = s1;
		secondLine = s2;
		status = s;
		addition = a;
	}
	
	public static String getStatusText(int a){
		switch (a) {
		case FRIEND_ONLINE: return "Online";
		case FRIEND_OFFLINE: return "Offline";
		case FRIEND_NOT_HERE: return "I'm not here";
		case FRIEND_NO_CONNECTION: return "No Connection to Server";
		case FRIEND_CONNECTING: return "Connecting...";
		case FRIEND_BUSY: return "Please don't disturb";

		default:
			return "";
		}
	}
	
	public static void generateIcons(){
		icons = new BufferedImage[iconNumbers.length];
		for (int i = 0; i < iconNumbers.length; i++) {
			icons[i] = PicLoader.pic.getImage("res/ima/cli/spb/siw/D"+iconNumbers[i]+".png");
		}
	}
	
	public static BufferedImage getImage(int a){
		for (int i = 0; i < iconNumbers.length; i++) {
			if(iconNumbers[i] == a)return icons[i];
		}
		return icons[0];
	}
}
