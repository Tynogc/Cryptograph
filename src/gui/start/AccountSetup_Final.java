package gui.start;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.concurrent.Semaphore;

import gui.utility.Emots;
import main.Fonts;
import main.Language;
import main.PicLoader;
import menu.Button;
import menu.MoveMenu;
import network.Writable;

public class AccountSetup_Final extends MoveMenu implements Writable{
	
	private Button back;
	private Button ok;
	private NewAccount controle;
	
	private String[] info;
	private Semaphore sema;
	
	private boolean isProcessing;
	
	private Writable ifft;
	
	public AccountSetup_Final(int x, int y, NewAccount n, String t) {
		super(x, y, PicLoader.pic.getImage("res/ima/mbe/m700x500.png"), t);
		
		ok = new Button(530,450,"res/ima/cli/B") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				if(isProcessing)return;
				isProcessing = true;
				setDisabled(true);
				new Thread(){
					public void run() {
						controle.finish(ifft);
						closeYou();
					};
				}.start();
			}
			
			@Override
			public void paintYou(Graphics2D g) {
				super.paintYou(g);
				if(isProcessing)
					Emots.emots.drawLoadingCircle(g, xPos+115, yPos);
			}
		};
		ok.setText(Language.lang.text(102));
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
		
		info = new String[12];
		for (int i = 0; i < info.length; i++) {
			info[i] = "";
		}
		
		sema = new Semaphore(1);
		ifft = this;
	}

	@Override
	protected void paintSecond(Graphics g) {
		g.drawImage(controle.imageSmal, 50, 50, null);
		
		g.setFont(Fonts.fontSans12);
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
		sema.release();
	}

	@Override
	protected boolean close() {
		return true;//TODO ask
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

}
