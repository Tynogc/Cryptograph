package gui;

import java.awt.Graphics;

import main.SeyprisMain;
import menu.AbstractMenu;
import menu.Button;

public class FrameMenu extends AbstractMenu{

	private Button close;
	private Button minimised;
	private Button maximised;
	
	public FrameMenu() {
		super(0,0,SeyprisMain.sizeX(),30);
		close = new Button(SeyprisMain.sizeX()-40,0,"res/ima/cli/R") {
			@Override
			protected void uppdate() {
			}
			@Override
			protected void isFocused() {
			}
			@Override
			protected void isClicked() {
				System.exit(1);
			}
		};
		add(close);
		
		moveAble = false;
	}

	@Override
	protected void uppdateIntern() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void paintIntern(Graphics g) {
		// TODO Auto-generated method stub
		
	}

}
