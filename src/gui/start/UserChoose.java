package gui.start;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import crypto.KeySaveLoad;
import main.PicLoader;
import main.SeyprisMain;
import main.UserManager;
import menu.Button;
import menu.Container;
import menu.TextEnterButton;
import user.KeyHandler;
import gui.OverswapMenu;

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
			users[i] = new UserB(30+i*buttonDistance+20,40,"res/ima/cli/001", far[i].getName()) {
				@Override
				protected void isClicked() {
					lastActiv = activ;
					activ = k;
					animCount = System.currentTimeMillis();
					System.out.println(name);
				}
			};
			add(users[i]);
			users[i].setText(""+k);
		}
		animCount = System.currentTimeMillis();
		
		cont = new Container(30,40);
		
		logIn = new Button(90,70,"res/ima/cli/Gsk") {
			
			@Override
			protected void uppdate() {		}
			
			@Override
			protected void isFocused() {}
			
			@Override
			protected void isClicked() {
				closeIntern();
			}
		};
		cont.addInContainer(logIn);
		logIn.setText("Log In");
		
		password = new TextEnterButton(90,40,150,20,Color.white, SeyprisMain.getKL()) {
			@Override
			protected void textEntered(String text) {
				System.out.println(text);
			}
			@Override
			protected void textEnteredDirectly(String text) {
				System.out.println("Enter "+text);
				closeIntern();
			}
		};
		password.setPwMode(true);
		cont.addInContainer(password);
		
		add(cont);
	}
	
	@Override
	protected void uppdateIntern() {
		super.uppdateIntern();
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
					password.setVisible(KeySaveLoad.isKeyEncrypted(new File(
							UserManager.getPreDirectory()+users[activ].name+"/"+KeyHandler.fileNamePrivate)));
				} catch (IOException e) {
					e.printStackTrace();
				}
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
		//TODO
	}

}

abstract class UserB extends Button{
	
	public final String name;

	public UserB(int x, int y, String s, String name) {
		super(x, y, s);
		this.name = name;
	}

	@Override
	protected void isFocused() {
		
	}

	@Override
	protected void uppdate() {
		
	}
	
}
