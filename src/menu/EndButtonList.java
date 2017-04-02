package menu;

import java.awt.Graphics;

import debug.Debug;

public class EndButtonList implements ButtonInterface{

	@Override
	public ButtonInterface add(ButtonInterface b) {
		b.setNext(this);
		return b;
	}

	@Override
	public void leftClicked(int x, int y) {
		
	}

	@Override
	public void leftReleased(int x, int y) {
		
	}

	@Override
	public void checkMouse(int x, int y) {
		
	}

	@Override
	public void paintYou(Graphics g) {
		
	}

	@Override
	public ButtonInterface remove(Button b) {
		Debug.println("* MinorERROR: Button 01.", Debug.ERROR);
		Debug.println("Tryed to remove a Button that didn't exist!", Debug.SUBERR);
		return this;
	}

	@Override
	public void longTermUpdate() {}

	@Override
	public void setNext(ButtonInterface b) {}

}
