package gui.sub;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.GuiControle;
import main.PicLoader;
import menu.AbstractMenu;
import menu.Button;

public abstract class RightClickMenu extends AbstractMenu{

	private BufferedImage back1;
	
	public RightClickMenu() {
		super(GuiControle.mouseX, GuiControle.mouseY, 200,10);
		closeOutside = true;
		moveAble = false;
		
		back1 = PicLoader.pic.getImage("res/ima/cli/spb/RCMB.png");
		
		GuiControle.addMenu(this);
	}
	
	public void addRCMbutton(String text, String icon, final int id){
		ySize+=25;
		add(new ButtonForRCM(0, ySize-25, id, this, icon, text));
	}

	@Override
	protected void uppdateIntern() {
		
	}

	@Override
	protected void paintIntern(Graphics g) {
		g.drawImage(back1, 0, 0, null);
	}
	
	protected void buttonWasPressed(int id){
		wasClicked(id);
		closeYou();
	}
	
	protected abstract void wasClicked(int id);

}

class ButtonForRCM extends Button{

	private final int id;
	private final RightClickMenu rcm;
	
	private BufferedImage icon;
	
	public ButtonForRCM(int x, int y, int id, RightClickMenu r, String ic, String text) {
		super(x, y, "res/ima/cli/spb/RCMB");
		this.id = id;
		rcm = r;
		if(ic != null){
			icon = PicLoader.pic.getImage(ic);
		}
		setText(text+"          ");
		setBig(false);
		setBold(false);
		setTextColor(Color.white);
	}

	@Override
	protected void isClicked() {
		rcm.buttonWasPressed(id);
	}

	@Override
	protected void isFocused() {}

	@Override
	protected void uppdate() {}
	
	@Override
	public void paintYou(Graphics2D g) {
		if(icon != null)
			g.drawImage(icon, xPos+160, yPos, null);
		super.paintYou(g);
	}
	
}
