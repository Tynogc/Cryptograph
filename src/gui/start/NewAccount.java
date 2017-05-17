package gui.start;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import crypto.RSAsaveKEY;
import main.GuiControle;
import main.Language;
import main.SetPassword;
import main.SeyprisMain;
import main.UserManager;
import network.Writable;

public class NewAccount{
	
	//Step 0: User Name
	//Step 1: Images
	//Step 2: Keys
	//Step 3: Servers
	private int currentStep = 0;
	
	private int x;
	private int y;
	
	public String name = "";
	public BufferedImage image;
	public BufferedImage imageSmal;
	private RSAsaveKEY key;
	
	private SetPassword password;
	public boolean encrKey;
	public boolean encrTrusted;
	public boolean encrLists;

	public NewAccount() {
		x = (SeyprisMain.sizeX()-700)/2;
		y = (SeyprisMain.sizeY()-500)/2+30;
		openMenu();
	}
	
	public void nextMenu(){
		currentStep++;
		openMenu();
	}
	
	public void prevMenu(){
		currentStep--;
		openMenu();
	}
	
	public final void setKey(RSAsaveKEY k){
		key = k;
	}
	
	public void setPrivacy(SetPassword sp, boolean key, boolean trust, boolean list){
		password = sp;
		encrKey = key;
		encrTrusted = trust;
		encrLists = list;
	}
	
	private void openMenu(){
		if(currentStep<0)
			currentStep = 0;
		
		if(currentStep == 0){
			GuiControle.addMenu(new AccountSetup_Name(x, y, this));
		}
		if(currentStep == 1){
			GuiControle.addMenu(new AccountSetup_Pictur(x, y, this, Language.lang.text(20200)));
		}
		if(currentStep == 2){
			GuiControle.addMenu(new AccountSetup_KeyGen(x, y, this, Language.lang.text(20200), key));
		}
		if(currentStep == 3){
			GuiControle.addMenu(new AccountSetup_Privacy(x, y, this, Language.lang.text(20200)));
		}
		if(currentStep == 4){
			GuiControle.addMenu(new AccountSetup_Final(x, y, this, Language.lang.text(20200)));
		}
	}
	
	public void finish(Writable info){
		info.write("Creating directory...");
		String dir = UserManager.getPreDirectory()+name;
		if(new File(dir).exists()){
			debug.Debug.println("ERROR: cleared path is now Obstructed! (NewAccount)", debug.Debug.ERROR);
			return;
		}
		new File(dir).mkdirs();
		dir+="/";
		
		info.write("Saving Profile-Picture");
		File outputfile = new File(dir+"ima.png");
		try {
			ImageIO.write(imageSmal, "png", outputfile);
			debug.Debug.println("Saved small Image", debug.Debug.COM);
		} catch (IOException e) {
			debug.Debug.println(e.toString(), debug.Debug.COMERR);
		}
		outputfile = new File(dir+"imaBig.png");
		try {
			ImageIO.write(image, "png", outputfile);
			debug.Debug.println("Saved Image", debug.Debug.COM);
		} catch (IOException e) {
			debug.Debug.println(e.toString(), debug.Debug.COMERR);
		}
		
		info.write("Saving Private-Key (This may take a few seconds...)");
		
		if(encrKey)
			new crypto.KeySaveLoad().saveKeyEncrypted(key, new File(dir+"Private.key"), password.getPassword());
		else
			new crypto.KeySaveLoad().saveKey(key, new File(dir+"Private.key"), false);
		
		info.write("Saving Public-Key");
		new crypto.KeySaveLoad().saveKey(key, new File(dir+"Public.key"), true);
		
		info.write("Creating Server-Lists");
	}

}
