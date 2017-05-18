package gui.chat;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.concurrent.Semaphore;

import gui.TextEnterField;
import main.Fonts;
import menu.AbstractMenu;
import menu.Button;
import network.Writable;
import user.FriendsList;

public class ChatControle extends AbstractMenu{
	
	private TextEnterField enter;
	
	private static final int maxNumOfOpenChannels = 10;//TODO
	
	private ChatContainer[] containers;
	private ChatButton[] contButton;
	
	private int currentlyActiv = -1;
	
	public ChatControle(int x, int y, TextEnterField t) {
		super(x, y, 500, 400);
		enter = t;
		
		containers = new ChatContainer[maxNumOfOpenChannels];
		contButton = new ChatButton[maxNumOfOpenChannels];
	}

	@Override
	protected void uppdateIntern() {
		for (int i = 0; i < containers.length; i++) {
			if(containers[i] != null)
				containers[i].check();
		}
	}

	@Override
	protected void paintIntern(Graphics g) {
		
	}
	
	public void openChannel(FriendsList f){
		//Check if already there...
		for (int i = 0; i < contButton.length; i++) {
			if(containers[i] != null){
				if(containers[i].comTo == f){
					setActiv(i);
					return;
				}
			}
		}
		
		int i = 0;
		for (; i < contButton.length; i++) {
			if(containers[i] == null)
				break;
		}
		containers[i] = new ChatContainer(40, 40, enter, f);
		contButton[i] = new ChatButton(100*i, 0, i, f.connectionName) {
			@Override
			protected void isClicked() {
				setActiv(pos);
			}
		};
		add(containers[i]);
		add(contButton[i]);
		
		if(currentlyActiv<0)
			setActiv(i);
	}
	
	private void setActiv(int pos){
		currentlyActiv = pos;
		
		for (int j = 0; j < containers.length; j++) {
			if(containers[j] == null)
				continue;
			if(j == pos){
				containers[j].setActiv();
			}else{
				containers[j].setInActiv();
			}
		}
	}

}

abstract class ChatButton extends Button{

	public int pos;
	
	public ChatButton(int x, int y, int p, String t) {
		super(x, y, "res/ima/cli/Gsk");
		pos = p;
		setText(t);
	}

	@Override
	protected void isFocused() {
	}

	@Override
	protected void uppdate() {
	}
	
}
