package gui.start;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.security.KeyException;
import java.util.concurrent.Semaphore;

import crypto.RSAsaveKEY;
import network.Writable;
import gui.FileSelecter;
import gui.utility.Emots;
import main.Fonts;
import main.Language;
import main.PicLoader;
import menu.Button;
import menu.DropDownButton;
import menu.DropDownMenu;
import menu.MoveMenu;

public class AccountSetup_KeyGen extends MoveMenu implements Writable{
	
	private Button back;
	private Button ok;
	private NewAccount controle;
	
	private String[] text;
	
	private String[] info;
	
	private BufferedImage work;
	private BufferedImage workSmal;
	
	private DropDownMenu bitLenghtChoose;
	private int bitLenght;
	
	private Semaphore sema;
	
	private RSAsaveKEY rsa;
	private boolean isProcessing = false;
	private AccountSetup_KeyGen askg;

	public AccountSetup_KeyGen(int x, int y, NewAccount n, String t, RSAsaveKEY k) {
		super(x, y, PicLoader.pic.getImage("res/ima/mbe/m700x500.png"), t);
		
		ok = new Button(530,450,"res/ima/cli/B") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				try {
					sema.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
				if(rsa != null && !isProcessing){
					controle.setKey(rsa);
					closeYou();
					controle.nextMenu();
				}
				sema.release();
			}
		};
		ok.setText(Language.lang.text(4));
		ok.setTextColor(Button.gray);
		add(ok);
		ok.setDisabled(true);
		
		Button gen = new Button(35,450,"res/ima/cli/B") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				generate();
			}
		};
		gen.setText(Language.lang.text(20238));
		gen.setTextColor(Button.gray);
		add(gen);
		
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
		
		info = new String[12];
		for (int i = 0; i < info.length; i++) {
			info[i] = "";
		}
		
		sema = new Semaphore(1);
		
		askg = this;
		
		rsa = k;
		if(rsa!=null){
			int i = rsa.size/1000-2;
			bitLenghtChoose.setCurrentlyActiv(i);
			ok.setDisabled(false);
			bitLenght = 2048+(i*1024);
		}else{
			bitLenghtChoose.setCurrentlyActiv(2);
		}
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
		
		g.setColor(Color.black);
		g.fillRect(35, 290, 300, 160);
		g.setColor(Color.gray);
		g.drawRect(35, 290, 300, 160);
		g.setColor(Color.white);
		try {
			sema.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
		for (int i = 0; i < info.length; i++) {
			g.drawString(info[i], 40, 304+i*12);
		}
		if(isProcessing)
			Emots.emots.drawProcessingCircle(g, 190, 450);
		sema.release();
	}

	@Override
	protected boolean close() {
		return true; //TODO ask
	}

	@Override
	protected void uppdateIntern() {
		
	}

	@Override
	public void write(String s) {
		debug.Debug.println(s, debug.Debug.SUBCOM);
		try {
			sema.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
		for (int i = 0; i < info.length; i++) {
			if(info[i].length()<1){
				info[i] = s;
				sema.release();
				return;
			}
		}
		for (int i = 0; i < info.length-1; i++) {
			info[i] = info[i+1];
		}
		info[info.length-1] = s;
		sema.release();
	}
	
	private void generate(){
		if(isProcessing)
			return;
		
		isProcessing = true;
		new Thread("Key-Gen"){
			public void run() {
				write("Starting Key-Generation...");
				try {
					RSAsaveKEY k = RSAsaveKEY.generateKey(bitLenght, true, true, 10, null, askg);
					sleep(1000);
					write("Key Created; "+k.size+" bit");
					String fp = cryptoUtility.RSAkeyFingerprint.getFingerprint(k);
					write("Fingerprint:");
					write(fp.substring(0, 32));
					write(fp.substring(33));
					sema.acquire();
					rsa = k;
					ok.setDisabled(false);
					sema.release();
				} catch (Exception e) {
					debug.Debug.printExeption(e);
					write("ERROR: "+e.getMessage());
				}
				isProcessing = false;
			};
		}.start();
	}

}
