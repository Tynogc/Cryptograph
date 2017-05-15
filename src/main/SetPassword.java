package main;

import javax.security.auth.Destroyable;

public class SetPassword implements Destroyable{

	private String pw = "";
	private boolean destroyed = false;
	private boolean filled = false;
	
	public int passwordStrength;
	
	public SetPassword(){};
	
	public SetPassword(String s){
		pw = s;
		filled = true;
	};
	
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
	
	public boolean chackMatch(SetPassword other){
		if(other.pw.compareTo(pw) == 0){
			return true;
		}
		return false;
	}
}
