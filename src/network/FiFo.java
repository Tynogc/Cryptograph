package network;

import java.net.SocketAddress;

public class FiFo {
	
	private FiElement first;
	
	public FiFo(){
		first = null;
	}
	
	public String out(){
		if(first != null){
			String s = first.str;
			first = first.next;
			return s;
		}
		return null;
	}
	
	public FiElement outElement(){
		if(first != null){
			FiElement s = first;
			first = first.next;
			s.next = null;
			return s;
		}
		return null;
	}
	
	public boolean contains(){
		return first != null;
	}
	
	public void in(String s, long t, SocketAddress a){
		if(first == null){
			first = new FiElement(s, t, a);
		}else{
			first.add(new FiElement(s, t, a));
		}
	}
	public void in(String s){
		in(s, 0, null);
	}
	public void in(String s, long t){
		in(s, t, null);
	}
	
	public int lenght(){
		if(first == null)return 0;
		return first.lenght();
	}

}
