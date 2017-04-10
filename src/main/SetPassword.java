package main;

import javax.security.auth.Destroyable;

public class SetPassword implements Destroyable{

	private String pw = "";
	private boolean destroyed = false;
	private boolean filled = false;
	
	public SetPassword(){};
	
	public void setPassword(String p){
		pw = p;
		filled = true;
	}
	
	public void destroy(){
		pw = "";
		destroyed = true;
	}
	
	public String getPassword(){
		if(destroyed)return null;
		return pw;
	}
	
	public void setPW(String s){
		pw = s;
		filled = true;
		destroyed = false;
	}
	
	public boolean isFilled(){
		return filled;
	}
}
