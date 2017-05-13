package gui.start;

import gui.Ping;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.regex.Pattern;

import main.Fonts;
import main.Language;
import main.PicLoader;
import main.SeyprisMain;
import main.UserManager;
import menu.Button;
import menu.MoveMenu;
import menu.TextEnterButton;

public class AccountSetup_Name extends MoveMenu{
	
	private TextEnterButton teb;
	private Button ok;
	private NewAccount controle;
	
	private String[] text;

	public AccountSetup_Name(int x, int y, NewAccount n) {
		super(x, y, PicLoader.pic.getImage("res/ima/mbe/m700x500.png"), Language.lang.text(20200));
		teb = new TextEnterButton(40, 400, 200, 20, Color.black, SeyprisMain.getKL()) {
			@Override
			protected void textEntered(String text) {
				check();
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
				make();
			}
		};
		ok.setText(Language.lang.text(4));
		ok.setTextColor(Button.gray);
		add(ok);
		
		controle = n;
		
		text = new String[]{
				Language.lang.text(20201),
				Language.lang.text(20202),
				Language.lang.text(20203),
				Language.lang.text(20204),
				""
		};
	}

	@Override
	protected void paintSecond(Graphics g) {
		g.setColor(Color.white);
		g.setFont(Fonts.fontBold18);
		g.drawString(text[0], 50, 130);
		g.drawString(text[1], 50, 150);
		
		g.setFont(Fonts.fontBold14);
		g.drawString(text[2], 40, 370);
		g.drawString(text[3], 60, 390);
		
		g.setColor(Color.red);
		g.drawString(text[4], 45, 440);
	}

	@Override
	protected boolean close() {
		return true; //TODO ask
	}

	@Override
	protected void uppdateIntern() {
		
	}
	
	private boolean check(){
		String dir = teb.getText();
		if(dir.length()<3){
			text[4] = Language.lang.text(20205);
			return false;
		}
		if(!Pattern.matches("[a-zA-Z0-9-_]+", dir)){
			text[4] = Language.lang.text(20206);
			return false;
		}
		File f = new File(UserManager.getPreDirectory()+dir);
		if(f.exists()){//User Already exists!
			text[4] = Language.lang.text(20207);
			return false;
		}
		text[4] = "";
		return true;
	}
	
	private void make(){
		if(!check()){
			Ping.ping(xPos+30, yPos+410, Ping.ALARM);
			return;
		}
		String dir = teb.getText();
		controle.name = dir;
		controle.nextMenu();
		closeYou();
	}

}
