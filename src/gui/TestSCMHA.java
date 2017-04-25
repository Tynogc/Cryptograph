package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import crypto.SCMHA;
import crypto.SRSHA;
import cryptoUtility.Random;
import main.PicLoader;
import menu.AbstractMenu;
import menu.Button;
import menu.CheckBox;
import menu.DropDownButton;
import menu.DropDownMenu;

public class TestSCMHA extends AbstractMenu{

	private SCMHA hash1;
	private SCMHA hash2;
	//private TextEnterButton teb;
	private int ed = SCMHA.SCMHA_512;
	
	private menu.DropDownMenu ddm;
	private menu.CheckBox cbx;
	private BufferedImage buf;
	private boolean needUpdate;
	
	private BufferedImage ima;
	
	public TestSCMHA(int x, int y, final TextEnterField tef) {
		super(x, y, 600, 400);
		
		ima = PicLoader.pic.getImage("res/SCMHA_Layout.jpg");
		
		Button b1 = new Button(20,50,"res/ima/cli/b"){
			@Override
			protected void isClicked() {
				/*PicturSystem pc = new PicturSystem(30, 100);
				GuiControle.addMenu(pc);
				SetPassword sp = new SetPassword();
				GuiControle.setSuperMenu(new EnterPassword(sp, SeyprisMain.getKL(), true));
				pc.setPassword(sp);*/
				//new network.TCPserver(1234);
				new Thread(){
					public void run() {
						try {
							hash1 = new SCMHA(ed);
							hash1.doCircle(false);
							hash1.update(tef.getText().getBytes());
						} catch (NoSuchAlgorithmException e) {
							e.printStackTrace();
						}
					}
				}.start();
				new Thread(){
					public void run() {
						try {
							hash2 = new SCMHA(ed);
							hash2.doCircle(false);
							hash2.update(flipRandomBit(tef.getText().getBytes()));
						} catch (NoSuchAlgorithmException e) {
							e.printStackTrace();
						}
					}
				}.start();
				needUpdate = true;
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
				try {
					hash1 = new SCMHA(ed);
					hash1.update(tef.getText().getBytes());
					hash1.digest();
					debug.Debug.println("Done1");
					hash2 = new SCMHA(ed);
					hash2.update(flipRandomBit(tef.getText().getBytes()));
					hash2.digest();
					debug.Debug.println("Done2");
					needUpdate = true;
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
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
					hash1.goOn();
				if(hash2 != null)
					hash2.goOn();
				
				needUpdate = true;
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
		
		Button b5 = new Button(20,130,"res/ima/cli/Gs"){
			//network.TCPclient cl;
			@Override
			protected void isClicked() {
				//cl = new network.TCPclient("localhost", 1234);
				if(hash1 != null)
					hash1.digest();
				if(hash2 != null)
					hash2.digest();
				
				needUpdate = true;
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
		add(b5);
		b5.setText("Digest");
		
		ddm = new DropDownMenu(420,70,100) {
			@Override
			protected void changed(int i) {
				switch (i) {
				case 0:
					ed = SCMHA.SCMHA_1024; break;
				case 1:
					ed = SCMHA.SCMHA_768; break;
				case 2:
					ed = SCMHA.SCMHA_512; break;

				default:
					break;
				}
			}
		};
		add(ddm);
		ddm.addSubButton(new DropDownButton(100, 20, "SCMHA_1024"), 0);
		ddm.addSubButton(new DropDownButton(100, 20, "SCMHA_768"), 1);
		ddm.addSubButton(new DropDownButton(100, 20, "SCMHA_512"), 2);
		ddm.setCurrentlyActiv(2);
		
		cbx = new CheckBox(420,30,"res/ima/cli/cbx/CB", 100) {
			@Override
			public void changed(boolean b) {
				needUpdate = true;
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
		if(!needUpdate)
			return;
		needUpdate = false;
		
		buf = new BufferedImage(800, 700, BufferedImage.TYPE_INT_ARGB);
		Graphics g = buf.getGraphics();
		
		if(hash1 != null)
			g.drawImage(hash1.testPaint(5, new Color(150,0,250), ima), 0, 140, null);
		if(hash2 != null && cbx.getState())
			g.drawImage(hash2.testPaint(5, new Color(255,0,0,125), ima), 0, 140, null);
		
		String str = "";
		
		g.setFont(main.Fonts.fontSans12);
		if(hash1 != null){
			g.setColor(new Color(150,0,250));
			byte[] b = hash1.getState();
			b[0] = (byte)(b[0] | 0x40);
			str = new BigInteger(b).toString(16);
			if(str.length()> 128){
				g.drawString(str.substring(0,64), 0, 600);
				g.drawString(str.substring(64, 128), 0, 610);
				g.drawString(str.substring(128), 0, 620);
			}else if(str.length()> 64){
				g.drawString(str.substring(0,64), 0, 600);
				g.drawString(str.substring(64), 0, 610);
			}else{
				g.drawString(str, 0, 600);
			}
			g.drawString("High bits: "+countSetBits(b)+ " of "+b.length*8, 0, 630);
			
		}
		
		if(hash2 != null && cbx.getState()){
			g.setColor(Color.red);
			byte[] b = hash2.getState();
			b[0] = (byte)(b[0] | 0x40);
			str = new BigInteger(b).toString(16);
			if(str.length()> 128){
				g.drawString(str.substring(0,64), 0, 640);
				g.drawString(str.substring(64, 128), 0, 650);
				g.drawString(str.substring(128), 0, 660);
			}else if(str.length()> 64){
				g.drawString(str.substring(0,64), 0, 640);
				g.drawString(str.substring(64), 0, 650);
			}else{
				g.drawString(str, 0, 640);
			}
			g.drawString("High bits: "+countSetBits(b)+ " of "+b.length*8, 0, 670);
		}
	}

	@Override
	protected void paintIntern(Graphics g) {
		if(buf != null)
			g.drawImage(buf, 0, 140, null);
	}
	
	private byte[] flipRandomBit(byte[] b){
		if(b.length == 0)return b;
		SecureRandom s = Random.generateSR();
		int a = s.nextInt(b.length);
		int c = s.nextInt(8);
		
		b[a]+=(1<<c);
		
		return b;
	}

	public static int countSetBits(byte[] array) {
	    int setBits = 0;
	    for (int byteIndex = 0; byteIndex < array.length; byteIndex++) {
            for (int bitIndex = 0; bitIndex < 8; bitIndex++) {
                if (getBit(bitIndex, array[byteIndex])) {
                    setBits++;
                }
            }
        }
	    return setBits;
	}
	
	private static boolean getBit(int index, final byte b) {
	    byte t = setBit(index);
	    return (b & t) != 0;
	}
	
	private static byte setBit(int index) {
	    return (byte)(1 << index);
	}
	
}
