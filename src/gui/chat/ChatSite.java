package gui.chat;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.PicLoader;
import menu.MoveMenu;

public class ChatSite extends MoveMenu{
	
	

	public ChatSite(int x, int y, String t) {
		super(x, y, new BufferedImage(700,500,BufferedImage.TYPE_INT_ARGB), t);
		ima.getGraphics().drawImage(PicLoader.pic.getImage("res/ima/mbe/m700x500.png"), 0, 0, null);
	}

	@Override
	protected void paintSecond(Graphics g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean close() {
		return true;
	}

	@Override
	protected void uppdateIntern() {
		// TODO Auto-generated method stub
		
	}
	
	public void command(String s){
		
	}

}
