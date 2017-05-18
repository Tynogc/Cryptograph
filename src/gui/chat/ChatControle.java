package gui.chat;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.concurrent.Semaphore;

import gui.TextEnterField;
import main.Fonts;
import menu.AbstractMenu;
import network.Writable;
import user.FriendsList;

public class ChatControle extends AbstractMenu implements Writable{

	public FriendsList currentFriend;
	
	private TextEnterField enter;
	
	private String[] text;
	
	public ChatControle(int x, int y, TextEnterField t) {
		super(x, y, 500, 400);
		enter = t;
		
		text = new String[30];
		for (int i = 0; i < text.length; i++) {
			text[i] = "";
		}
	}

	@Override
	protected void uppdateIntern() {
		if(currentFriend == null)
			return;
		if(currentFriend.client == null)
			return;
		
		String s = currentFriend.client.getLastMsg();
		if(s == null)
			return;
		
		addString("[Friend] "+s);
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

	@Override
	protected void paintIntern(Graphics g) {
		g.setColor(Color.white);
		g.setFont(Fonts.font14);
		for (int i = 0; i < text.length; i++) {
			g.drawString(text[i], 20, 20+20*i);
		}
	}

	@Override
	public void write(String s) {
		if(currentFriend == null)
			return;
		addString("[You] "+s);
		if(currentFriend.client == null){
			addString("       However your friend isn't connected!");
			return;
		}
		
		currentFriend.client.writeChat(s);
	}
	
	public void setCurrentChannel(FriendsList f){
		currentFriend = f;
		enter.setWriteChannel(this);
		
		addString("Started Conversation with: "+f.connectionName);
	}

}
