package gui.start;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;

import gui.FileSelecter;
import main.Fonts;
import main.Language;
import main.PicLoader;
import menu.Button;
import menu.MoveMenu;

public class AccountSetup_Pictur extends MoveMenu{
	
	private Button back;
	private Button ok;
	private NewAccount controle;
	
	private String[] text;
	
	private BufferedImage work;
	private BufferedImage workSmal;
	
	private FileSelecter fs;

	public AccountSetup_Pictur(int x, int y, NewAccount n, String t) {
		super(x, y, PicLoader.pic.getImage("res/ima/mbe/m700x500.png"), t);
		
		Button load = new Button(30,300,"res/ima/cli/G") {
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
		load.setText(Language.lang.text(101));
		add(load);
		
		Button create = new Button(30,350,"res/ima/cli/G") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				create();
			}
		};
		create.setText(Language.lang.text(20222));
		add(create);
		
		ok = new Button(530,450,"res/ima/cli/B") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				ok();
			}
		};
		ok.setText(Language.lang.text(4));
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
		text = new String[]{
				Language.lang.text(20220),
				Language.lang.text(20221)
		};
		
		controle = n;
		
		if(n != null){
			back.setText(Language.lang.text(6));
			work = n.image;
			workSmal = n.imageSmal;
		}else{
			back.setText(Language.lang.text(5));
			//TODO load image
		}
		add(back);
		
		if(work == null)
			create();
	}

	@Override
	protected void paintSecond(Graphics g) {
		g.drawImage(work, 420, 200, null);
		
		g.setColor(Color.white);
		g.setFont(Fonts.fontBold14);
		g.drawString(text[0], 40, 170);
		g.drawString(text[1], 60, 190);
	}

	@Override
	protected boolean close() {
		return true;//TODO ask
	}

	@Override
	protected void uppdateIntern() {
		if(fs != null){
			if(fs.hasSelected()){
				File f = fs.getFile();
				ImageIcon ima = new ImageIcon(f.getAbsolutePath());
				if(ima.getIconHeight()<1){
					fs = null;
					return;
				}
				BufferedImage buf = new BufferedImage(ima.getIconWidth(),
						ima.getIconHeight(), BufferedImage.TYPE_INT_RGB);
				buf.getGraphics().drawImage(ima.getImage(), 0, 0, null);
				remap(buf);
				
				fs = null;
			}
		}
	}
	
	private void ok(){
		if(controle != null){
			controle.image = work;
			controle.imageSmal = workSmal;
			controle.nextMenu();
		}else{
			//TODO save Images
		}
		closeYou();
	}
	
	private void create(){
		String t = controle.name.substring(0, 2);
		if(t.charAt(0) == t.charAt(1))
			t = " "+t.substring(0, 1)+" ";
		
		double rot = Math.random()*Math.PI*2.0;
		double add = Math.PI*2.0/3.0;
		Color c1 = new Color((int)(Math.sin(rot)*125)+125,
				(int)(Math.sin(rot+add)*125)+125,
				(int)(Math.sin(rot-add)*125)+125);
		
		int gray = (int)(Math.random()*100)+100;
		Color c2 = new Color(gray, gray, gray);
		
		Font f;
		BufferedImage buf = new BufferedImage(480, 480, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D)buf.getGraphics();
		int size = 30;
		int v;
		do {
			f = Fonts.createFontBold(size);
			g.setFont(f);
			int with = g.getFontMetrics().stringWidth(t);
			if(with>buf.getWidth()-4){
				size-=2;
			}else if(with<buf.getWidth()-30){
				size+=2;
			}else{
				v = with;
				break;
			}
		} while (true);
		
		v = 480-v;
		v/=2;
		
		int y = 190+size/2;
		
		if(Math.random()>0.5){
			g.setColor(c1);
			g.fillRect(0, 0, 480, 480);
			g.setColor(c2);
			g.drawString(t, v, y);
		}else{
			g.setColor(c2);
			g.fillRect(0, 0, 480, 480);
			g.setColor(c1);
			g.drawString(t, v, y);
		}
		
		remap(buf);
	}
	
	private void remap(BufferedImage buf){
		 work = new BufferedImage(240, 240, BufferedImage.TYPE_INT_RGB);
		 Graphics2D g = (Graphics2D)work.getGraphics();
		 g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		 g.scale((double)work.getWidth()/buf.getWidth(), (double)work.getHeight()/buf.getHeight());
		 g.drawImage(buf, 0, 0, null);
		 
		 workSmal = new BufferedImage(80, 80, BufferedImage.TYPE_INT_RGB);
		 g = (Graphics2D)workSmal.getGraphics();
		 g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		 g.scale(0.5/3.0, 0.5/3.0);
		 g.drawImage(buf, 0, 0, null);
	}
	
	
}
