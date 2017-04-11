package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;

import javax.swing.JFrame;

import crypto.PicturCrypto;
import cryptoUtility.AdvancedSecureRandom;
import cryptoUtility.Random;
import main.GuiControle;
import main.SetPassword;
import main.SeyprisMain;
import menu.AbstractMenu;
import menu.Button;
import menu.Container;
import menu.DataFiled;

public class TopMenu extends AbstractMenu{

	private BufferedImage test;
	private int testCounter;
	
	private Container[] allCont;
	private Button[] allBut;
	
	public TopMenu() {
		super(0,30,300,300);
		Button b1 = new Button(20,50,"res/ima/cli/b"){
			@Override
			protected void isClicked() {
				PicturSystem pc = new PicturSystem(30, 100);
				GuiControle.addMenu(pc);
				SetPassword sp = new SetPassword();
				GuiControle.setSuperMenu(new EnterPassword(sp, SeyprisMain.getKL(), true));
				pc.setPassword(sp);
			}
			@Override
			protected void isFocused() {
				
			}
			@Override
			protected void uppdate() {
				
			}
		};
		add(b1);
		b1.setText("TEST");
		
		add(new DataFiled(20,30,100,20,Color.GREEN) {
			@Override
			protected void uppdate() {
				setText("R: "+Random.getEntropyFuelGauge());
			}
			
			@Override
			protected void isClicked() {	
			}
		});
		
		allCont = new Container[4];
		allBut = new Button[4];
		
		allBut[0] = generateButton(0, "Friends");
		allBut[1] = generateButton(1, "Conversations");
		allBut[2] = generateButton(2, "Servers");
		allBut[3] = generateButton(3, "Own Accounts");
		
		for (int i = 0; i < allBut.length; i++) {
			add(allBut[i]);
			//add(allCont[i]);
		}
		
		test = new BufferedImage(400, 256, BufferedImage.TYPE_INT_RGB);
	}
	
	private Button generateButton(final int i, String s){
		Button b = new Button(0,i*32+100,"res/ima/cli/spb/DOWN") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				//TODO
			}
		};
		b.setText(s);
		return b;
	}

	@Override
	protected void uppdateIntern() {
		if(testCounter>0){
			testCounter--;
			AdvancedSecureRandom s = Random.generateSR();
			
			byte[] b = new byte[125];
			s.nextBytes(b);
			
			Graphics g = test.getGraphics();
			if(testCounter%20<=10)
				g.setColor(Color.green);
			else
				g.setColor(Color.blue);
			for (int i = 0; i < b.length; i++) {
				int k = b[i]+128;
				for (int j = 0; j < 400; j++) {
					if((test.getRGB(j, k)&0xffff)<1){
						g.drawRect(j, k, 0, 0);
						break;
					}
				}
			}
		}
		
	}

	@Override
	protected void paintIntern(Graphics g) {
		g.drawImage(test, 0, 90, null);
		//System.out.println(g.getFontMetrics().getStringBounds("abcdefg", g).getWidth());
		//g.drawString("abcdefg", 100, 200);
	}

}
