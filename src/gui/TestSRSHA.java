package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.security.SecureRandom;
import java.util.Base64;

import crypto.SRSHA;
import cryptoUtility.Random;
import menu.AbstractMenu;
import menu.Button;
import menu.CheckBox;
import menu.DropDownButton;
import menu.DropDownMenu;

public class TestSRSHA extends AbstractMenu{

	private SRSHA hash1;
	private SRSHA hash2;
	//private TextEnterButton teb;
	private int ed = SRSHA.SRSHA_1024;
	
	private menu.DropDownMenu ddm;
	private menu.CheckBox cbx;
	
	
	public TestSRSHA(int x, int y, final TextEnterField tef) {
		super(x, y, 600, 400);
		
		Button b1 = new Button(20,50,"res/ima/cli/b"){
			@Override
			protected void isClicked() {
				/*PicturSystem pc = new PicturSystem(30, 100);
				GuiControle.addMenu(pc);
				SetPassword sp = new SetPassword();
				GuiControle.setSuperMenu(new EnterPassword(sp, SeyprisMain.getKL(), true));
				pc.setPassword(sp);*/
				//new network.TCPserver(1234);
				
				hash1 = new SRSHA(ed);
				hash1.noAutomaticLoop();
				hash1.update(tef.getText().getBytes());
				
				hash2 = new SRSHA(ed);
				hash2.noAutomaticLoop();
				hash2.update(flipRandomBit(tef.getText().getBytes()));
			}
			@Override
			protected void isFocused() {
				
			}
			@Override
			protected void uppdate() {
				
			}
		};
		add(b1);
		b1.setTextColor(Button.gray);
		b1.setText("Hash Step By Step");
		
		Button b2 = new Button(220,50,"res/ima/cli/b"){
			//network.TCPclient cl;
			@Override
			protected void isClicked() {
				//cl = new network.TCPclient("localhost", 1234);
				hash1 = new SRSHA(ed);
				hash1.update(tef.getText().getBytes());
				debug.Debug.println("Done1");
				hash2 = new SRSHA(ed);
				hash2.update(flipRandomBit(tef.getText().getBytes()));
				debug.Debug.println("Done2");
			}
			@Override
			protected void isFocused() {
				
			}
			@Override
			protected void uppdate() {
				//if(cl != null)
					//cl.refresh();
			}
		};
		add(b2);
		b2.setTextColor(Button.gray);
		b2.setText("Hash");
		
		Button b3 = new Button(20,90,"res/ima/cli/G"){
			//network.TCPclient cl;
			@Override
			protected void isClicked() {
				//cl = new network.TCPclient("localhost", 1234);
				if(hash1 != null)
					hash1.doLoop();
				if(hash2 != null)
					hash2.doLoop();
			}
			@Override
			protected void isFocused() {
				
			}
			@Override
			protected void uppdate() {
				//if(cl != null)
					//cl.refresh();
			}
		};
		add(b3);
		b3.setText("Do A Step");
		
		ddm = new DropDownMenu(420,70,100) {
			@Override
			protected void changed(int i) {
				switch (i) {
				case 0:
					ed = SRSHA.SRSHA_64; break;
				case 1:
					ed = SRSHA.SRSHA_128; break;
				case 2:
					ed = SRSHA.SRSHA_256; break;
				case 3:
					ed = SRSHA.SRSHA_512; break;
				case 4:
					ed = SRSHA.SRSHA_1024; break;

				default:
					break;
				}
			}
		};
		add(ddm);
		ddm.addSubButton(new DropDownButton(100, 20, "SRSHA_64"), 0);
		ddm.addSubButton(new DropDownButton(100, 20, "SRSHA_128"), 1);
		ddm.addSubButton(new DropDownButton(100, 20, "SRSHA_256"), 2);
		ddm.addSubButton(new DropDownButton(100, 20, "SRSHA_512"), 3);
		ddm.addSubButton(new DropDownButton(100, 20, "SRSHA_1024"), 4);
		ddm.setCurrentlyActiv(4);
		
		cbx = new CheckBox(420,30,"res/ima/cli/cbx/CB", 100) {
			@Override
			public void changed(boolean b) {
				
			}
		};
		add(cbx);
		cbx.setText("Show Bit Mutation");
		
		
		/*teb = new TextEnterButton(180,30,100,20,Color.white,SeyprisMain.getKL()) {
			@Override
			protected void textEntered(String text) {
				
			}
		};
		add(teb);*/
	}

	@Override
	protected void uppdateIntern() {
		
	}

	@Override
	protected void paintIntern(Graphics g) {
		if(hash1 != null)
			g.drawImage(hash1.testPaint(9, new Color(150,0,250)), 0, 140, null);
		if(hash2 != null && cbx.getState())
			g.drawImage(hash2.testPaint(9, new Color(255,0,0,125)), 0, 140, null);
		
		String str = "";
		
		g.setFont(main.Fonts.fontSans14);
		if(hash1 != null){
			g.setColor(new Color(150,0,250));
			byte[] b = hash1.digest();
			str = Base64.getEncoder().encodeToString(b);
			g.drawString(str, 0, 500);
		}
		
		if(hash2 != null && cbx.getState()){
			g.setColor(Color.red);
			byte[] b = hash2.digest();
			str = Base64.getEncoder().encodeToString(b);
			g.drawString(str, 0, 520);
		}
	}
	
	private byte[] flipRandomBit(byte[] b){
		SecureRandom s = Random.generateSR();
		int a = s.nextInt(b.length);
		int c = s.nextInt(8);
		
		b[a]+=(1<<c);
		
		return b;
	}

	
}
