package gui.start;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import gui.FileSelecter;
import main.Fonts;
import main.Language;
import main.PicLoader;
import menu.Button;
import menu.DropDownButton;
import menu.DropDownMenu;
import menu.MoveMenu;

public class AccountSetup_KeyGen extends MoveMenu{
	
	private Button back;
	private Button ok;
	private NewAccount controle;
	
	private String[] text;
	
	private BufferedImage work;
	private BufferedImage workSmal;
	
	private DropDownMenu bitLenghtChoose;
	private int bitLenght;

	public AccountSetup_KeyGen(int x, int y, NewAccount n, String t) {
		super(x, y, PicLoader.pic.getImage("res/ima/mbe/m700x500.png"), t);
		
		ok = new Button(530,450,"res/ima/cli/B") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				
			}
		};
		ok.setText(Language.lang.text(20238));
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
		
		bitLenghtChoose = new DropDownMenu(100,200,100) {
			@Override
			protected void changed(int i) {
				bitLenght = 2048+(i*1024);
			}
		};
		bitLenght = 4096;
		for (int i = 0; i < 7; i++) {
			DropDownButton b = new DropDownButton(100, 20, (2048+1024*i)+" bit");
			bitLenghtChoose.addSubButton(b, i);
			if(i == 0){
				b.setTextColor(Color.red);
			}else if(i == 1){
				b.setTextColor(Color.orange);
			}
		}
		bitLenghtChoose.setCurrentlyActiv(2);
		add(bitLenghtChoose);
		
		text = new String[]{
				Language.lang.text(20230),
				Language.lang.text(20231),
				Language.lang.text(20232),
				Language.lang.text(20233),
				Language.lang.text(20234),
				Language.lang.text(20239),
				Language.lang.text(20240)
		};
	}

	@Override
	protected void paintSecond(Graphics g) {
		g.setColor(Color.white);
		g.setFont(Fonts.fontBold14);
		g.drawString(text[0], 40, 170);
		g.drawString(text[1], 60, 190);
		
		if(bitLenght < 4000){
			g.setColor(Color.red);
			g.drawString(text[6], 240, 215);
			g.setColor(Color.white);
		}
		
		g.setFont(Fonts.fontSans12);
		g.drawString(text[2], 40, 240);
		g.drawString(text[3], 40, 252);
		g.drawString(text[4], 40, 264);
		g.drawString(text[5], 40, 215);
	}

	@Override
	protected boolean close() {
		return true; //TODO ask
	}

	@Override
	protected void uppdateIntern() {
		
	}

}
