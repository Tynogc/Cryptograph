package gui;

import java.awt.Graphics;
import javax.swing.JTextArea;

import menu.AbstractMenu;

public class TextEnterAssist extends AbstractMenu{

	private static final int xS = 340;
	private static final int yS = 200;
	
	private TextEnterField jta;
	private boolean hasFocus;
	private boolean wantsFocus;
	
	private int cs = -1;
	
	public TextEnterAssist(int x, int y, TextEnterField t) {
		super(x, y, xS, yS);
		jta = t;
	}

	@Override
	protected void uppdateIntern() {
		if(xPos+yPos*1000 != cs){
			cs = xPos+yPos*1000;
			jta.setLocation(xPos+40, yPos+20);
		}
	}

	@Override
	protected void paintIntern(Graphics g) {
		g.translate(40, 20);
		jta.paintTheImage(g);
		g.translate(-40, -20);
	}
	
	@Override
	public void maousAtOnlyIfFocused(int x, int y) {
		if(x >= 0 && x <= xSize && y >= 0 && y <= ySize){
			if(!hasFocus){
				jta.setVisible(true);
				hasFocus = true;
			}
		}else{
			if(!wantsFocus && hasFocus){
				hasFocus = false;
				jta.setVisible(false);
			}
		}
	}
	
	@Override
	public void leftClickForFocus(int x, int y) {
		if(x >= 0 && x <= xSize && y >= 0 && y <= ySize){
			wantsFocus = true;
		}else{
			wantsFocus = false;
			jta.setVisible(false);
			hasFocus = false;
		}
	}
	
}
