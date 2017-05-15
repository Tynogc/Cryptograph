package gui.start;

import java.awt.Graphics;

import gui.EnterPassword;
import main.GuiControle;
import main.Language;
import main.PicLoader;
import main.SeyprisMain;
import menu.Button;
import menu.MoveMenu;

public class AccountSetup_Final extends MoveMenu{
	
	private Button back;
	private Button ok;
	private NewAccount controle;
	
	public AccountSetup_Final(int x, int y, NewAccount n, String t) {
		super(x, y, PicLoader.pic.getImage("res/ima/mbe/m700x500.png"), t);
		
		ok = new Button(530,450,"res/ima/cli/B") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				controle.finish();
			}
		};
		ok.setText(Language.lang.text(102));
		ok.setTextColor(Button.gray);
		add(ok);
		
		back = new Button(380,450,"res/ima/cli/G") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				if(controle != null)
					controle.prevMenu();
				closeYou();
			}
		};
		back.setText(Language.lang.text(6));
		add(back);
		
		controle = n;
	}

	@Override
	protected void paintSecond(Graphics g) {
		
	}

	@Override
	protected boolean close() {
		return false;
	}

	@Override
	protected void uppdateIntern() {
		
	}

}
