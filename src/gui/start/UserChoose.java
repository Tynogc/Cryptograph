package gui.start;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.concurrent.Semaphore;

import javax.swing.ImageIcon;

import crypto.KeySaveLoad;
import main.Fonts;
import main.Language;
import main.PicLoader;
import main.SetPassword;
import main.SeyprisMain;
import main.UserManager;
import menu.Button;
import menu.Container;
import menu.TextEnterButton;
import user.KeyHandler;
import gui.OverswapMenu;
import gui.utility.Emots;

public class UserChoose extends OverswapMenu{

	private SeyprisMain main;
	
	private UserB[] users;
	private int activ;
	private int lastActiv;
	private long animCount;
	
	private int fadeCounter;
	private Container cont;
	
	private final int buttonDistance = 60;
	
	private Button logIn;
	private TextEnterButton password;
	
	private Semaphore sema;
	
	private boolean wasRed;
	private boolean red;
	
	private boolean encryptedKey;
	
	private final String[] text;
	
	public UserChoose(main.SeyprisMain m) {
		imas = new BufferedImage[]{
			PicLoader.pic.getImage("res/ima/ote/pw2.png")	
		};
		main = m;
		moveAble = false;
		
		lastActiv = -1;
		
		File[] far = new File(UserManager.getPreDirectory()).listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		
		users = new UserB[far.length];
		for (int i = 0; i < users.length; i++) {
			final int k = i;
			users[i] = new UserB(30+i*buttonDistance+20,40,"res/ima/cli/spb/STU", far[i].getName()) {
				@Override
				protected void isClicked() {
					if(sema.availablePermits()<1)
						return;
					
					lastActiv = activ;
					activ = k;
					animCount = System.currentTimeMillis();
					System.out.println(name);
					for (int j = 0; j < users.length; j++) {
						users[j].setActiv(false);
					}
				}
			};
			add(users[i]);
			users[i].setText(""+k);
		}
		animCount = System.currentTimeMillis();
		
		cont = new Container(30,40);
		
		logIn = new Button(90,90,"res/ima/cli/Gsk") {
			
			@Override
			protected void uppdate() {		}
			
			@Override
			protected void isFocused() {}
			
			@Override
			protected void isClicked() {
				processLoad();
			}
		};
		cont.addInContainer(logIn);
		logIn.setText(Language.lang.text(10301));
		
		password = new TextEnterButton(90,60,150,20,Color.white, SeyprisMain.getKL()) {
			@Override
			protected void textEntered(String text) {
			}
			@Override
			protected void textEnteredDirectly(String text) {
				processLoad();
			}
		};
		password.setPwMode(true);
		cont.addInContainer(password);
		
		add(cont);
		
		sema = new Semaphore(1);
		
		text = new String[]{
				Language.lang.text(10300),
				Language.lang.text(10302),
				Language.lang.text(10303),
				Language.lang.text(10304),
				Language.lang.text(10305)
		};
		
		Button newUser = new Button(5, 30, "res/ima/cli/R") {//TODO Color/Icon + Subtext
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				if(sema.availablePermits()>0){
					closeIntern();
					new NewAccount();
				}
			}
		};
		add(newUser);
	}
	
	@Override
	protected void uppdateIntern() {
		super.uppdateIntern();
		if(wasRed != red){
			wasRed = red;
			if(red){
				imas[0] = PicLoader.pic.getImage("res/ima/ote/pw1.png");	
			}else{
				imas[0] = PicLoader.pic.getImage("res/ima/ote/pw2.png");
			}
		}
		
		if(lastActiv != activ){
			int o = (int)((System.currentTimeMillis()-animCount)/3);
			boolean isDone = true;
			int to = xSize/2-190;
			if(!moveButton(activ, to, o)){
				isDone = false;
			}
			to = users[activ].getxPos();
			for (int i = activ-1; i >= 0; i--) {
				to -= buttonDistance;
				if(i == lastActiv){
					if(users[i].getxPos()<to)
						to = users[i].getxPos();
				}
				if(!moveButton(i, to, o)){
					isDone = false;
				}
			}
			to = xSize/2+100;
			for (int i = activ+1; i < users.length; i++) {
				to += buttonDistance;
				if(!moveButton(i, to, o)){
					isDone = false;
				}
			}
			animCount += o*3;
			if(isDone){
				lastActiv = activ;
				System.out.println("DONE");
				cont.setxPos(users[activ].getxPos());
				try {
					encryptedKey = KeySaveLoad.isKeyEncrypted(new File(
							UserManager.getPreDirectory()+users[activ].name+"/"+KeyHandler.fileNamePrivate));
				} catch (IOException e) {
					e.printStackTrace();
				}
				password.setVisible(encryptedKey);
				
				password.leftClicked(password.getxPos()+1, password.getyPos()+1);
				password.leftReleased(password.getxPos()+1, password.getyPos()+1);
				
				users[activ].setActiv(true);
			}
			if(fadeCounter > 0){
				fadeCounter -= o;
				if(fadeCounter<0)
					fadeCounter = 0;
				cont.setFade((float)fadeCounter/100f);
			}
		}
		if(fadeCounter < 150){
			int o = (int)((System.currentTimeMillis()-animCount)/3);
			fadeCounter += o;
			animCount += o*3;
			cont.setFade((float)fadeCounter/100f);
		}
	}
	
	private boolean moveButton(int i, int to, int o){
		if(users[i].getxPos()<to){
			users[i].setxPos(users[i].getxPos()+o);
			if(users[i].getxPos()>to)
				users[i].setxPos(to);
		}
		if(users[i].getxPos()>to){
			users[i].setxPos(users[i].getxPos()-o);
			if(users[i].getxPos()<to)
				users[i].setxPos(to);
		}
		return users[i].getxPos() == to;
	}
	
	private void processLoad(){
		final String path = users[activ].name;
		try {
			sema.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
		password.setDisabled(true);
		logIn.setDisabled(true);
		
		new Thread("Process-Load"){
			public void run() {
				
				UserManager.setUserName(path);
				KeyHandler kh = new KeyHandler();
				if(kh.isPrivateKeyEncrypted()){
					kh.decryptPrivateKey(new SetPassword(password.getText()));
				}
				if(kh.isPrivateKeyOK()){
					red = false;
					main.startClientAndServerControle();
					closeIntern();
				}else{
					red = true;
				}
				
				password.setDisabled(false);
				logIn.setDisabled(false);
				
				sema.release();
			};
		}.start();
	}

	@Override
	protected void paintIntern(Graphics g) {
		super.paintIntern(g);
		if(lastActiv != activ)
			return;
		g.setColor(Color.white);
		g.setFont(Fonts.fontBold18);
		g.drawString(text[0], cont.getxPos()+100, 60);
		g.setFont(Fonts.fontSans14);
		g.drawString(users[activ].name, cont.getxPos()+100, 75);
		if(sema.availablePermits()<=0){
			Emots.emots.drawProcessingCircle(g, cont.getxPos()+245, 96);
			g.drawString(text[4], cont.getxPos()+90, 94);
		}else if(red){
			g.setColor(Color.red);
			g.drawString(text[2], cont.getxPos()+90, 94);
		}else if(encryptedKey){
			g.drawString(text[1], cont.getxPos()+90, 94);
		}else{
			g.drawString(text[3], cont.getxPos()+90, 114);
		}
	}
}

abstract class UserB extends Button{
	
	public final String name;
	
	private BufferedImage ima1;
	private BufferedImage ima2;
	
	private boolean activ = false;

	public UserB(int x, int y, String s, String name) {
		super(x, y, s);
		this.name = name;
		
		ImageIcon i = new ImageIcon(UserManager.getPreDirectory()+name+"/ima.png");
		if(i.getIconHeight()<1)
			return;
		ima1 = new BufferedImage(i.getIconWidth(), i.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D)ima1.getGraphics();
		g.drawImage(i.getImage(), 0, 0, null);
		ima2 = new BufferedImage(i.getIconWidth(), i.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		g = (Graphics2D)ima2.getGraphics();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
		g.drawImage(i.getImage(), 0, 0, null);
	}

	@Override
	protected void isFocused() {
		
	}

	@Override
	protected void uppdate() {
		
	}
	
	public void setActiv(boolean b){
		activ = b;
	}
	
	@Override
	public void paintYou(Graphics2D g) {
		if(activ || focused){
			g.drawImage(ima1, xPos+4,yPos+4,null);
		}else{
			g.drawImage(ima2, xPos+4,yPos+4,null);
		}
		super.paintYou(g);
	}
	
}
