package gui.start;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Language;
import main.PicLoader;
import main.SeyprisMain;
import menu.Button;
import menu.MoveMenu;
import menu.TextEnterButton;

public class AccountSetup_Name extends MoveMenu{
	
	private TextEnterButton teb;
	private Button ok;
	private NewAccount controle;

	public AccountSetup_Name(int x, int y, NewAccount n) {
		super(x, y, PicLoader.pic.getImage("res/ima/mbe/m700x500.png"), Language.lang.text(20100));
		teb = new TextEnterButton(40, 200, 200, 20, Color.black, SeyprisMain.getKL()) {
			@Override
			protected void textEntered(String text) {
				
			}
		};
		teb.setTextColor(Color.white);
		add(teb);
		
		ok = new Button(530,450,"res/ima/cli/B") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				
			}
		};
		ok.setText(Language.lang.text(4));
		ok.setTextColor(Button.gray);
		add(ok);
	}

	@Override
	protected void paintSecond(Graphics g) {
		
	}

	@Override
	protected boolean close() {
		return true; //TODO ask
	}

	@Override
	protected void uppdateIntern() {
		
	}

}
