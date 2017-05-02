package menu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.PicLoader;

public class ComArea implements ButtonInterface{

	private ButtonInterface next;
	private BufferedImage image;
	private BufferedImage rawImage;
	
	private int xPos;
	private int yPos;
	private int xSize;
	private int ySize;

	public ComArea(int x, int y, String ima) {
		rawImage = PicLoader.pic.getImage(ima);
	}
	
	public void setText(String text){
		
	}
	
	public int getxPos() {
		return xPos;
	}

	public void setxPos(int xPos) {
		this.xPos = xPos;
	}

	public int getyPos() {
		return yPos;
	}

	public void setyPos(int yPos) {
		this.yPos = yPos;
	}

	public int getxSize() {
		return xSize;
	}
	
	public void setxSize(int xSize) {
		this.xSize = xSize;
	}
	
	public int getySize() {
		return ySize;
	}
	
	@Override
	public ButtonInterface add(ButtonInterface b) {
		next = next.add(b);
		return this;
	}

	@Override
	public void leftClicked(int x, int y) {
		next.leftClicked(x, y);
		
	}

	@Override
	public void leftReleased(int x, int y) {
		next.leftReleased(x, y);
		
	}

	@Override
	public void checkMouse(int x, int y) {
		next.checkMouse(x, y);
		
	}

	@Override
	public void paintYou(Graphics g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ButtonInterface remove(Button b) {
		next = next.remove(b);
		return this;
	}

	@Override
	public void longTermUpdate() {
		next.longTermUpdate();
	}

	@Override
	public void setNext(ButtonInterface b) {
		next = b;		
	}

	@Override
	public void rightReleased(int x, int y) {
		next.rightReleased(x, y);
	}


	

}
