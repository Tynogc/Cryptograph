package menu;

import java.awt.Graphics;
import java.awt.Graphics2D;

public interface ButtonInterface {

	public ButtonInterface add(ButtonInterface b);
	public void leftClicked(int x, int y);
	public void leftReleased(int x, int y);
	public void rightReleased(int x, int y);
	public void checkMouse(int x, int y);
	public void paintYou(Graphics2D g);
	public ButtonInterface remove(Button b);
	public void longTermUpdate();
	public void setNext(ButtonInterface b);
}
