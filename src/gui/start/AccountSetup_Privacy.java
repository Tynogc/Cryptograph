package gui.start;

import gui.EnterPassword;
import gui.Ping;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Fonts;
import main.GuiControle;
import main.Language;
import main.PicLoader;
import main.SetPassword;
import main.SeyprisMain;
import menu.Button;
import menu.CheckBox;
import menu.MoveMenu;

public class AccountSetup_Privacy extends MoveMenu{
	
	private Button back;
	private Button ok;
	private NewAccount controle;
	
	private Button enterPw;
	
	private String[] text;
	
	private BufferedImage warn;
	
	private SetPassword sp1;
	private SetPassword sp2;
	private boolean faultPw;
	
	private CheckBox encrKey;
	private CheckBox encrKeyHandler;
	private CheckBox encrLists;
	
	public AccountSetup_Privacy(int x, int y, NewAccount n, String t) {
		super(x, y, PicLoader.pic.getImage("res/ima/mbe/m700x500.png"), t);
		
		ok = new Button(530,450,"res/ima/cli/B") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				if(!encrKey.getState()){
					done();
				}else if(sp1.isFilled()){
					GuiControle.setSuperMenu(new EnterPassword(sp2, SeyprisMain.getKL(), false,
							Language.lang.text(20248)));
				}
			}
		};
		ok.setText(Language.lang.text(4));
		ok.setTextColor(Button.gray);
		add(ok);
		
		enterPw = new Button(40,450,"res/ima/cli/B") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				faultPw = false;
				GuiControle.setSuperMenu(new EnterPassword(sp1, SeyprisMain.getKL(), true));
			}
		};
		enterPw.setText(Language.lang.text(10310));
		enterPw.setTextColor(Button.gray);
		add(enterPw);
		
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
		
		text = new String[]{
				Language.lang.text(20241),
				Language.lang.text(20242),
				Language.lang.text(20246),
				Language.lang.text(20247),
				Language.lang.text(20233),
				Language.lang.text(20243),
				Language.lang.text(20244),
				Language.lang.text(20245),
				Language.lang.text(20249),
				Language.lang.text(20250)
		};
		
		warn = PicLoader.pic.getImage("res/ima/smi/warnwin.png");
		
		sp1 = new SetPassword();
		sp2 = new SetPassword();
		
		encrKey = new CheckBox(50,243, "res/ima/cli/cbx/CB", 100) {
			@Override
			public void changed(boolean b) {
				if(b){
					setTextColor(Color.white);
				}else{
					setTextColor(Color.red);
				}
				encrKeyHandler.setDisabled(!b);
				encrKeyHandler.setState(b);
				encrLists.setDisabled(!b);
				enterPw.setDisabled(!b);
			}
		};
		encrKey.setText(Language.lang.text(20235));
		add(encrKey);
		encrKey.setTextColor(Color.white);
		encrKey.setState(true);
		
		encrKeyHandler = new CheckBox(50,273, "res/ima/cli/cbx/CB", 100) {
			@Override
			public void changed(boolean b) {
				if(b){
					setTextColor(Color.white);
				}else{
					setTextColor(Color.red);
				}
			}
		};
		encrKeyHandler.setText(Language.lang.text(20236));
		add(encrKeyHandler);
		encrKeyHandler.setTextColor(Color.white);
		encrKeyHandler.setState(true);
		
		encrLists = new CheckBox(50,303, "res/ima/cli/cbx/CB", 100) {
			@Override
			public void changed(boolean b) {
				
			}
		};
		encrLists.setText(Language.lang.text(20237));
		add(encrLists);
		encrLists.setTextColor(Color.white);
	}

	@Override
	protected void paintSecond(Graphics g) {
		g.setColor(Color.white);
		g.setFont(Fonts.fontBold14);
		g.drawString(text[0], 40, 170);
		g.drawString(text[1], 60, 190);
		
		g.setFont(Fonts.fontSans12);
		g.drawString(text[2], 40, 210);
		g.drawString(text[3], 40, 222);
		g.drawString(text[4], 40, 234);
		
		if(encrKey.getState()){
			g.drawImage(warn, 40, 336, null);
			g.setColor(Color.red);
			if(faultPw){
				g.setFont(Fonts.fontBold14);
				g.drawString(text[8], 40, 440);
			}
			g.setFont(Fonts.fontSans14);
			g.drawString(text[5], 130, 362);
			g.drawString(text[6], 130, 380);
			g.drawString(text[7], 130, 394);
			
			if(sp1.isFilled()){
				g.setColor(Color.white);
				g.drawString(text[9], 43, 430);
				g.setFont(Fonts.fontBold14);
				g.setColor(EnterPassword.analysisColor[sp1.passwordStrength]);
				g.drawString(EnterPassword.analysisString[sp1.passwordStrength], 180, 430);
			}
		}
	}

	@Override
	protected boolean close() {
		return true; //TODO ask
	}

	@Override
	protected void uppdateIntern() {
		ok.setDisabled(!sp1.isFilled() && encrKey.getState() );
		
		if(sp2.isFilled()){
			//Check match
			if(sp2.chackMatch(sp1)){
				done();
			}else{
				Ping.ping(xPos+20, yPos+435, Ping.ALARM);
				sp1.destroy();
				sp2.destroy();
				sp1 = new SetPassword();
				sp2 = new SetPassword();
				faultPw = true;
			}
		}
	}
	
	private void done(){
		controle.setPrivacy(sp1, encrKey.getState(), encrKeyHandler.getState(), encrLists.getState());
		sp2.destroy();
		if(!encrKey.getState())
			sp1.destroy();
		
		closeYou();
		controle.nextMenu();
	}

}
