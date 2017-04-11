package gui;

import java.awt.Graphics;

import javax.swing.JFrame;

import main.SeyprisMain;
import menu.AbstractMenu;
import menu.Button;

public class FrameMenu extends AbstractMenu{

	private Button close;
	private Button minimised;
	private Button maximised;
	
	public FrameMenu() {
		super(0,0,SeyprisMain.sizeX(),30);
		close = new Button(SeyprisMain.sizeX()-50,0,"res/ima/cli/spb/R") {
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
		minimised = new Button(SeyprisMain.sizeX()-100,0,"res/ima/cli/spb/B") {
			@Override
			protected void uppdate() {
			}
			@Override
			protected void isFocused() {
			}
			@Override
			protected void isClicked() {
				SeyprisMain.getFrame().setState(JFrame.ICONIFIED);
			}
		};
		add(minimised);
		
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
