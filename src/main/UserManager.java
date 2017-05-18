package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import debug.DebugFrame;

public class UserManager {

	private static final String userPre = "data/user/";
	private static final String serverPre = "data/server/";
	private static final String botPre = "data/bot/";
	
	private static String userDirectory = "";
	private static String preDirectory = userPre;
	
	public static void setUserName(String dir){
		userDirectory = dir+"/";
	}
	
	public static String getUserDir(){
		return preDirectory+userDirectory;
	}
	
	public static String getUserName(){
		if(userDirectory.length()<1)
			return "";
		return userDirectory.substring(0, userDirectory.length()-1);
	}
	
	public static String getPreDirectory(){
		return preDirectory;
	}
	
	public UserManager(boolean server, boolean bot, DebugFrame f){
		if(server){
			preDirectory = serverPre;
		}
		if(bot){
			preDirectory = botPre;
		}
		File[] far = new File(preDirectory).listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		
		String lastUser = null;
		try {
			FileReader fr = new FileReader(preDirectory+"default.set");
			BufferedReader br = new BufferedReader(fr);
			
			lastUser = br.readLine();
			br.close();
		} catch (Exception e) {
			debug.Debug.println("*ERROR loading User-Pref: "+e.getMessage(), debug.Debug.ERROR);
		}
		
		if(far == null){
			userChosen("");
		}else if(far.length == 0){
			userChosen("");
		}else if(far.length == 1){
			userChosen(far[0].getName()+"/");
		}else{
			if(lastUser == null)
				lastUser = far[0].getName();
				
			int i = askChoose(far, f, 0, lastUser);
			if(i==100){
				userChosen(lastUser+"/");
			}else{
				userChosen(far[i].getName()+"/");
			}
		}
		
		PrintWriter writer = null; 
		try { 
			writer = new PrintWriter(new FileWriter(preDirectory+"default.set")); 
			writer.println(getUserName());
			
			writer.flush();
			writer.close();
		}catch(Exception e){
			debug.Debug.println("*ERROR saving User-Pref: "+e.getMessage(), debug.Debug.ERROR);
		}
	}
	
	private void userChosen(String name){
		userDirectory = name;
		debug.Debug.println("*User-Directory is: "+getUserDir());
	}
	
	private int askChoose(File[] far, DebugFrame f, int q, String enter){
		debug.Debug.println("Choose User Directory:", debug.Debug.REMOTE);
		int z = 0;
		boolean lm = false;
		for (int i = q; i < far.length; i++) {
			if(i>=9+q){
				debug.Debug.println("[0] ... ", debug.Debug.REMOTE);
				lm = true;
				break;
			}
			debug.Debug.println("["+(i+1-q)+"] "+far[i].getName(), debug.Debug.REMOTE);
			
		}
		debug.Debug.println("[ENTER] "+enter, debug.Debug.REMOTE);
		f.setNumState(true);
		while(f.canState() <= 0){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		int i = f.canState();
		f.setNumState(false);
		if(i-1<far.length){
			return i-1+q;
		}
		//TODO '0' = 91;
		if(i == 91 && lm)
			return askChoose(far, f, q+9, enter);
		
		//ENTER:
		return 100;
	}
	
}
