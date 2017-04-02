package menu;

import java.awt.Graphics;

public interface ButtonInterface {

	public ButtonInterface add(ButtonInterface b);
	public void leftClicked(int x, int y);
	public void leftReleased(int x, int y);
	public void checkMouse(int x, int y);
	public void paintYou(Graphics g);
	public ButtonInterface remove(Button b);
	public void longTermUpdate();
	public void setNext(ButtonInterface b);
}
