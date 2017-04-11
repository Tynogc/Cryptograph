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
import menu.DataFiled;

public class TopMenu extends AbstractMenu{

	private BufferedImage test;
	private int testCounter;
	
	public TopMenu() {
		super(0,30,300,300);
		Button b1 = new Button(20,50,"res/ima/cli/b"){
			@Override
			protected void isClicked() {
				//System.out.println("a");
				//SeyprisMain.getFrame().setState(JFrame.ICONIFIED);
				/*SecureRandom s = Random.generateSR();
				for (int i = 0; i < 100; i++) {
					System.out.println(s.nextInt());
				}
				try {
					crypto.RSAsaveKEY.generateKey(2048, true, true, 5, s);
				} catch (Exception e) {
					e.printStackTrace();
				}*/
				
				//testCounter = 20;
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
		
		test = new BufferedImage(400, 256, BufferedImage.TYPE_INT_RGB);
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
		
	}

}
