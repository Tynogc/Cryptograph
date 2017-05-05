package main;

import java.io.BufferedReader;
import java.io.FileReader;

import network.com.COMCONSTANTS;
import user.FriendsList;

public class Language {
	
	private LanguageText first;
	
	public static Language lang;

	public Language(){
		this("res/lan/English.lang");
	}
	
	public Language(String file){
		first = new LanguageText(" ", 0);
		
		String s = "";
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			
			s = br.readLine();
			while (s != null) {
				if(!s.startsWith("//")){
					String[] st = s.split(": ");
					int id  = Integer.parseInt(st[0]);
					String t = s.substring(st[0].length()+2);
					first.add(new LanguageText(t, id));
				}
				s = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			debug.Debug.println("*ERROR loading Language: "+s+" "+e.getMessage(), debug.Debug.ERROR);
		}
		lang = this;
	}
	
	public String text(int id){
		LanguageText l = first;
		while(l != null){
			if(l.id == id)
				return l.text;
			l = l.next;
		}
		debug.Debug.println("*ERROR Language: ID not found "+id, debug.Debug.ERROR);
		return "";
	}
}

class LanguageText{
	public final String text;
	public final int id;
	
	public LanguageText next;
	
	public LanguageText(String t, int i){
		text = t;
		id = i;
	}
	
	public void add(LanguageText l){
		if(l.id == id){
			debug.Debug.println("*ERROR Loading Language: ID twice "+id, debug.Debug.ERROR);
			return;
		}
		if(next == null)
			next = l;
		else
			next.add(l);
	}
}
