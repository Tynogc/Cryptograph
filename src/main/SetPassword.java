package main;

import javax.security.auth.Destroyable;

public class SetPassword implements Destroyable{

	private String pw = "";
	private boolean destroyed = false;
	
	public SetPassword(){};
	
	public void setPassword(String p){
		pw = p;
	}
	
	public void destroy(){
		pw = "";
		destroyed = true;
	}
	
	public String getPassword(){
		if(destroyed)return null;
		return pw;
	}
}
