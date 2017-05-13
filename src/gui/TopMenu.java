package gui;

import java.awt.Color;
import java.awt.Graphics;

import cryptoUtility.Random;
import gui.sub.SideContainer;
import gui.utility.Emots;
import main.GuiControle;
import main.SetPassword;
import main.SeyprisMain;
import menu.AbstractMenu;
import menu.Button;
import menu.DataFiled;
import user.SideDisplay;

public class TopMenu extends AbstractMenu{
	
	public static int HIGHT_OF_CONTAINERS = 400;
	private static final int DISTANCE= 100;
	
	private SideContainer[] allCont;
	private Button[] allBut;
	
	private long animCount;
	private int position;
	private boolean up;
	
	public TopMenu() {
		super(0,30,300,HIGHT_OF_CONTAINERS+DISTANCE+128);
		
		Button b1 = new Button(20,50,"res/ima/cli/b"){
			@Override
			protected void isClicked() {
				PicturSystem pc = new PicturSystem(30, 100);
				GuiControle.addMenu(pc);
				SetPassword sp = new SetPassword();
				GuiControle.setSuperMenu(new EnterPassword(sp, SeyprisMain.getKL(), true));
				pc.setPassword(sp);
				//new network.TCPserver(1234);
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
		
		Button b2 = new Button(220,50,"res/ima/cli/b"){
			
			@Override
			protected void isClicked() {
				user.FriendsControle.friends.test();
			}
			@Override
			protected void isFocused() {
				
			}
			@Override
			protected void uppdate() {
				
			}
		};
		add(b2);
		b2.setText("TEST");
		
		SideDisplay.generateIcons();
		
		add(new DataFiled(20,30,100,20,Color.GREEN) {
			@Override
			protected void uppdate() {
				setText("R: "+Random.getEntropyFuelGauge());
			}
			
			@Override
			protected void isClicked() {
				
			}
		});
		
		allCont = new SideContainer[4];
		allBut = new Button[4];
		
		allBut[0] = generateButton(0, "Friends");
		allBut[1] = generateButton(1, "Conversations");
		allBut[2] = generateButton(2, "Servers");
		allBut[3] = generateButton(3, "Own Accounts");
		
		for (int i = 0; i < allBut.length; i++) {
			allCont[i] = new gui.sub.SideContainer(0,32*i+DISTANCE+32, HIGHT_OF_CONTAINERS);
			add(allBut[i]);
			add(allCont[i]);
			allCont[i].setVisible(false);
		}
		
		animCount = System.currentTimeMillis();
	}
	
	public void setSideMenuServer(SideDisplay[] sd){
		allCont[2].updateButtons(sd);
	}
	
	public void setSideMenuFriends(SideDisplay[] sd){
		allCont[0].updateButtons(sd);
	}
	
	public void setSideMenuConversations(SideDisplay[] sd){
		allCont[1].updateButtons(sd);
	}
	
	private Button generateButton(final int i, String s){
		Button b = new Button(1,i*32+DISTANCE,"res/ima/cli/spb/DOWN") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				if(i==position)return;
				up = i>position;
				position = i;
				animCount = System.currentTimeMillis();
				for (int j = 0; j < allCont.length; j++) {
					allCont[j].setVisible(false);
				}
			}
		};
		b.setText(s);
		return b;
	}

	@Override
	protected void uppdateIntern() {
		if(animCount!=0){
			main.EventCounter.event();
			int u = (int)(System.currentTimeMillis()-animCount)/2;
			u-=HIGHT_OF_CONTAINERS;
			if(u>0){
				u = 0;
				animCount = 0;
				allCont[position].setVisible(true);
			}
			int p = DISTANCE+32;
			for (int i = 1; i < allBut.length; i++) {
				if(i-1==position){
					p+=HIGHT_OF_CONTAINERS;
				}
				if(up){
					//button.y>p-u;
					if(allBut[i].getyPos()>p-u){
						allBut[i].setyPos(p-u);
					}
				}else{
					if(allBut[i].getyPos()<p+u){
						allBut[i].setyPos(p+u);
					}
				}
				p+=32;
			}
		}
		
	}

	@Override
	protected void paintIntern(Graphics g) {
		//System.out.println(g.getFontMetrics().getStringBounds("abcdefg", g).getWidth());
		//g.drawString("abcdefg", 100, 200);
	}

}
