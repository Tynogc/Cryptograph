package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import crypto.PicturCrypto;
import main.SetPassword;
import menu.AbstractMenu;
import menu.Button;
import menu.CheckBox;
import menu.DataFiled;

public class PicturSystem extends AbstractMenu{

	private BufferedImage image;
	
	private File[] currendFiles;
	private int pos;
	
	private SetPassword pw;
	
	private CheckBox enableNoise;
	private Button noise;
	private Button encAndSave;
	
	private Button decrypt;
	private Button left;
	private Button right;
	private Button chooseFiles;
	
	private DataFiled info;
	
	private FileSelecter fs;
	
	private PicturCrypto picCryptoActiv;
	private int picCryptoQue;
	private static final int SAVE = 2;
	private static final int ENCRYPT = 4;
	private static final int DECRYPT = 8;
	
	public PicturSystem(int x, int y) {
		super(x, y, 700, 600);
		left = new Button(10,30,"res/ima/cli/Gs") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				pos--;
				if(pos<0)pos = currendFiles.length-1;
				loadFile();
			}
		};
		left.setText("<");
		add(left);
		right = new Button(235,30,"res/ima/cli/Gs") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				pos++;
				if(pos>=currendFiles.length)pos = 0;
				loadFile();
			}
		};
		right.setText(">");
		add(right);
		decrypt= new Button(350,30,"res/ima/cli/G") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				if(picCryptoActiv == null){
					picCryptoActiv = new PicturCrypto(pw.getPassword());
					picCryptoActiv.processPictur(image, false);
					picCryptoQue = DECRYPT;
				}
			}
		};
		decrypt.setText("Decrypt");
		add(decrypt);
		
		encAndSave = new Button(550,30,"res/ima/cli/G") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				enableNoise.setState(false);
				enableNoise.changed(false);
				if(image != null){
					debug.Debug.println(" Encrypt and Save...", debug.Debug.COM);
					if(picCryptoActiv == null){
						picCryptoActiv = new PicturCrypto(pw.getPassword());
						picCryptoActiv.processPictur(image, true);
						picCryptoQue = ENCRYPT+SAVE;
					}
				}
			}
		};
		encAndSave.setText("Encrypt & Save");
		add(encAndSave);
		encAndSave.setDisabled(true);
		
		noise = new Button(550,70,"res/ima/cli/G") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				enableNoise.setState(false);
				enableNoise.changed(false);
				debug.Debug.println(" -Adding Noise...", debug.Debug.COMERR);
				if(image != null)
				PicturCrypto.addNoise(image);
			}
		};
		noise.setText("Add Noise");
		add(noise);
		noise.setDisabled(true);
		
		enableNoise = new CheckBox(550,0,"res/ima/cli/cbx/CB",100){
			@Override
			public void changed(boolean b) {
				noise.setDisabled(!b);
				encAndSave.setDisabled(!b);
			}
		};
		enableNoise.setText("Enable E&S");
		add(enableNoise);
		
		chooseFiles = new Button(85,30,"res/ima/cli/B") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				if(fs == null)
					fs = new FileSelecter();
			}
		};
		chooseFiles.setText("Open...");
		add(chooseFiles);
		
		chooseFiles.setTextColor(Button.gray);
		
		info = new DataFiled(30,60,180,20,Color.white) {
			@Override
			protected void uppdate() {}
			@Override
			protected void isClicked() {}
		};
		add(info);
		info.setText("");
	}

	@Override
	protected void uppdateIntern() {
		if(fs != null){
			if(!fs.isPending()){
				if(fs.hasSelected()){
					currendFiles = fs.getFile().getParentFile().listFiles();
					for (int i = 0; i < currendFiles.length; i++) {
						System.out.println(currendFiles[i].getName());
						if(currendFiles[i].getName().compareTo(fs.getFile().getName())==0)
							pos = i;
					}
					loadFile();
				}
				fs = null;
			}
		}
		
		//Pictur Que
		if(picCryptoActiv != null){
			if(picCryptoActiv.isDone()){
				if(picCryptoQue>=DECRYPT){
					picCryptoActiv = new PicturCrypto(pw.getPassword());
					picCryptoActiv.processPictur(image, false);
					picCryptoQue -= DECRYPT;
				}else if(picCryptoQue>=ENCRYPT){
					picCryptoActiv = new PicturCrypto(pw.getPassword());
					picCryptoActiv.processPictur(image, true);
					picCryptoQue -= ENCRYPT;
				}else if(picCryptoQue>=SAVE){
					File outputfile = currendFiles[pos];
					try {
						ImageIO.write(image, "png", outputfile);
						debug.Debug.println(" Saved", debug.Debug.COM);
					} catch (IOException e) {
						debug.Debug.println(e.toString(), debug.Debug.COMERR);
					}
					picCryptoQue = 0;
				}
				if(picCryptoQue <= 0)
					picCryptoActiv = null;
			}
		}
	}

	@Override
	protected void paintIntern(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		if(image!=null){
			double sc = (double)xSize/image.getWidth();
			double scY = (double)(ySize-80)/image.getHeight();
			if(scY<sc)sc = scY;
			if(sc<1){
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g2d.translate(0, 80);
				g2d.scale(sc, sc);
				g2d.drawImage(image, 0, 0, null);
			}else{
				g2d.drawImage(image, 0, 80, null);
			}
		}
	}
	
	private void loadFile(){
		if(currendFiles == null)return;
		info.setText(currendFiles[pos].getName());
		if(currendFiles[pos].getName().endsWith(".png")){
			ImageIcon i = new ImageIcon(currendFiles[pos].getPath());
			image = new BufferedImage(i.getIconWidth(), i.getIconHeight(), BufferedImage.TYPE_INT_RGB);
			image.getGraphics().drawImage(i.getImage(), 0, 0, null);
		}else{
			image = null;
		}
	}
	
	public final void setPassword(SetPassword p){
		if(pw != null)pw.destroy();
		pw = p;
	}

}
