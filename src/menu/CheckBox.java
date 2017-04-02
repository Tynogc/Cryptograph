package menu;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import main.PicLoader;

public abstract class CheckBox extends Button{

	protected boolean state;
	
	protected BufferedImage[] dots;
	
	protected String checkBoxTxt;
	
	public CheckBox(int x, int y, String t, int xs) {
		super(x, y, t);
		state = false;
		dots = new BufferedImage[]{
				PicLoader.pic.getImage(t+"qn.png"),
				PicLoader.pic.getImage(t+"qf.png"),
				PicLoader.pic.getImage(t+"qc.png"),
				PicLoader.pic.getImage(t+"qd.png")
		};
		xSize = xs;
		setBold(false);
		setTextColor(gray);
	}

	@Override
	protected void isClicked() {
		state = !state;
		changed(state);
	}

	@Override
	protected void isFocused() {
		
	}

	@Override
	protected void uppdate() {
		
	}
	
	@Override
	public void paintYou(Graphics g) {
		if(state){
			if(disabled){
				g.drawImage(dots[3], xPos, yPos, null);
			}else if(status){
				g.drawImage(dots[2], xPos, yPos, null);
			}else{
				g.drawImage(dots[0], xPos, yPos, null);
			}
			if(focused && !disabled){
				g.drawImage(dots[1], xPos, yPos, null);
			}
		}
		if(checkBoxTxt != null){
			if(checkBoxTxt.length()>0){
				Graphics2D g2d = (Graphics2D)g;
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2d.setFont(font);
				g2d.setColor(textColor);
				g2d.drawString(checkBoxTxt, xPos+30, yPos+20);
			}
		}
		super.paintYou(g);
	}
	
	public abstract void changed(boolean b);
	
	public boolean getState(){
		return state;
	}
	
	public void setState(boolean b){
		state = b;
	}
	
	@Override
	public void setText(String text) {
		checkBoxTxt = text;
	}

}
