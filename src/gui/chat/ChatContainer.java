package gui.chat;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import user.FriendsList;
import network.Writable;
import network.com.ClientToClient;
import gui.TextEnterField;
import main.Fonts;
import menu.Container;

public class ChatContainer extends Container implements Writable{

	private final TextEnterField tef;
	public final FriendsList comTo;
	
	private ChatSite chatSite;
	
	private BufferedImage[] bubbles;
	
	private String[] text;
	
	public ChatContainer(int x, int y, TextEnterField t, FriendsList c) {
		super(x, y, 400, 400);
		tef = t;
		comTo = c;
		
		text = new String[30];
		for (int i = 0; i < text.length; i++) {
			text[i] = "";
		}
	}
	
	private void addString(String s){
		for (int i = 0; i < text.length; i++) {
			if(text[i].length()<1){
				text[i] = s;
				return;
			}
		}
		for (int i = 0; i < text.length-1; i++) {
			text[i] = text[i+1];
		}
		text[text.length-1] = s;
	}
	
	public void check(){
		if(comTo.client == null)
			return;
		
		String s = comTo.client.getLastMsg();
		if(s == null)
			return;
		
		addString("["+comTo.connectionName+"]: "+s);
	}

	@Override
	public void write(String s) {
		if(comTo == null)
			return;
		addString("[You] "+s);
		if(comTo.client == null){
			addString("       However your friend isn't connected!");
			return;
		}
		
		comTo.client.writeChat(s);
	}
	
	@Override
	protected void paintIntern(Graphics g) {
		g.setColor(Color.white);
		g.setFont(Fonts.font14);
		for (int i = 0; i < text.length; i++) {
			g.drawString(text[i], 20, 40+20*i);
		}
	}
	
	public void setActiv(){
		setVisible(true);
		tef.setWriteChannel(this);
	}
	
	public void setInActiv(){
		setVisible(false);
	}

}
