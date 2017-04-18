package menu;

import java.awt.Graphics;

public class Container implements ButtonInterface{

	private ButtonInterface next;
	private ButtonInterface content;
	
	private boolean visible;
	
	private int xPos;
	private int yPos;
	
	private int boundaryX = -1;
	private int boundaryY = -1;
	private boolean doClip;
	
	public Container(int x, int y) {
		xPos = x;
		yPos = y;
		content = new EndButtonList();
		visible = true;
		doClip = false;
	}
	
	public Container(int x, int y, int xS, int yS) {
		this(x,y);
		boundaryX = xS;
		boundaryY = yS;
		doClip = true;
	}
	
	public int getBoundaryX() {
		return boundaryX;
	}

	public void setBoundaryX(int boundaryX) {
		this.boundaryX = boundaryX;
		doClip = boundaryX>0;
	}

	public int getBoundaryY() {
		return boundaryY;
	}

	public void setBoundaryY(int boundaryY) {
		this.boundaryY = boundaryY;
		doClip = boundaryY>0;
	}

	@Override
	public ButtonInterface add(ButtonInterface b) {
		next = next.add(b);
		return this;
	}

	@Override
	public void leftClicked(int x, int y) {
		if(!doClip||(x>=xPos&&x<xPos+boundaryX&&y>=yPos&&x<yPos+boundaryY))
			content.leftClicked(x-xPos, y-yPos);
		next.leftClicked(x, y);
	}

	@Override
	public void leftReleased(int x, int y) {
		content.leftReleased(x-xPos, y-yPos);
		next.leftReleased(x, y);	
	}

	@Override
	public void checkMouse(int x, int y) {
		update();
		content.checkMouse(x-xPos, y-yPos);
		next.checkMouse(x, y);
	}

	@Override
	public void paintYou(Graphics g) {
		if(visible){
			if(doClip)
				g.setClip(xPos, yPos, boundaryX, boundaryY);
			g.translate(xPos, yPos);
			content.paintYou(g);
			paintIntern(g);
			g.translate(-xPos, -yPos);
			if(doClip)
				g.setClip(null);
		}
		next.paintYou(g);
	}

	@Override
	public ButtonInterface remove(Button b) {
		next = next.remove(b);
		return this;
	}
	
	public void removeIntern(Button b){
		content = content.remove(b);
	}

	@Override
	public void longTermUpdate() {
		content.longTermUpdate();
		next.longTermUpdate();
	}
	
	public void update() {
	}

	@Override
	public void setNext(ButtonInterface b) {
		next = b;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
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
	
	public void addInContainer(ButtonInterface b){
		content = content.add(b);
	}
	
	protected void paintIntern(Graphics g){};
}
