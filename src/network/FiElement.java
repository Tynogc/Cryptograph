package network;

import java.net.SocketAddress;

public class FiElement{
	
	public FiElement next;
	public String str;
	public long time;
	public SocketAddress adress;
	
	public FiElement(String s, long t, SocketAddress a){
		str = s;
		next = null;
		time = t;
		adress = a;
	}
	
	public void add(FiElement f){
		if(next == null) next = f;
		else next.add(f);
	}
	
	public int lenght(){
		if(next == null)return 1;
		return next.lenght()+1;
	}

}
