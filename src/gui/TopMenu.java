package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.security.SecureRandom;

import javax.swing.JFrame;

import cryptoUtility.Random;
import main.SeyprisMain;
import menu.AbstractMenu;
import menu.Button;
import menu.DataFiled;

public class TopMenu extends AbstractMenu{

	public TopMenu() {
		super(0,30,300,300);
		Button b1 = new Button(20,50,"res/ima/cli/b"){
			@Override
			protected void isClicked() {
				//System.out.println("a");
				//SeyprisMain.getFrame().setState(JFrame.ICONIFIED);
				SecureRandom s = Random.generateSR();
				
				for (int i = 0; i < 100; i++) {
					System.out.println(""+s.nextInt());
				}
				System.out.println(">>>>>>>>>>End");
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
		
		add(new DataFiled(20,50,100,20,Color.GREEN) {
			@Override
			protected void uppdate() {
				setText("R: "+Random.getEntropyFuelGauge());
			}
			
			@Override
			protected void isClicked() {	
			}
		});
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
