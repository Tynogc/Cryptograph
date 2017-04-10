package gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.SeyprisMain;
import menu.AbstractMenu;

public abstract class OverswapMenu extends AbstractMenu{
	
	protected BufferedImage[] imas;
	
	public OverswapMenu(){
		super(0,(int)SeyprisMain.getFrame().getSize().getHeight()/2-100,
				(int)SeyprisMain.getFrame().getSize().getWidth(),200);
	}

	@Override
	protected void paintIntern(Graphics g) {
		int u = (int)((System.currentTimeMillis()/100)%51);
		for (int i = 0; i < xSize+54; i+=51) {
			g.drawImage(imas[0], i-u, 0, null);
		}
		
		for (int i = -51; i < xSize; i+=51) {
			g.drawImage(imas[0], i+u, 170, null);
		}
	}

}
