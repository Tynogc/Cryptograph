package menu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import menu.AbstractMenu;

public abstract class MoveMenu extends AbstractMenu{

	protected BufferedImage ima;
	
	protected String titel;
	
	protected menu.Button close;
	
	public MoveMenu(int x, int y, BufferedImage i, String t) {
		super(x, y, i.getWidth(), i.getHeight());
		ima = i;
		titel = t;
		
		close = new menu.Button(xSize-22,3,"res/ima/cli/X"){
			@Override
			protected void isClicked() {
				if(close())
					closeYou();
			}
			@Override
			protected void isFocused() {
				
			}
			@Override
			protected void uppdate() {
				
			}
		};
		add(close);
	}

	@Override
	protected void paintIntern(Graphics g) {
		g.drawImage(ima, 0, 0, null);
		g.setColor(Color.white);
		g.setFont(main.Fonts.fontBold14);
		g.drawString(titel, 5, 18);
		paintSecond(g);
	}
	
	protected abstract void paintSecond(Graphics g);
	/**
	 * @return can be closed
	 */
	protected abstract boolean close();

}
