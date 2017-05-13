package gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import main.PicLoader;


public class Ping {

	private static int atX;
	private static int atY;
	
	private static int current;
	private static int counter;
	private long lastCount;
	
	public static final int ALARM = 10;
	public static final int WRONG = 11;
	public static final int OK = 50;
	
	public static void ping(int x, int y, int id){
		atX = x;
		atY = y;
		current = id;
		counter = 0;
	}
	
	private BufferedImage[] imas;
	private BufferedImage work;
	
	public Ping(){
		imas = new BufferedImage[]{
				PicLoader.pic.getImage("res/ima/smi/te1.png"),
				PicLoader.pic.getImage("res/ima/smi/te2.png")
		};
		lastCount = System.currentTimeMillis();
	}
	
	public void update(){
		int o = (int)(System.currentTimeMillis()-lastCount)/50;
		lastCount += o*50;
		if(current == 0)
			return;
		
		if(counter == 0)
			work = new BufferedImage(64, 64,  BufferedImage.TYPE_INT_ARGB);
		
		counter += o;
		
		Color c;
		BufferedImage d = new BufferedImage(64, 64,  BufferedImage.TYPE_INT_ARGB);
		int i;
		switch (current) {
		case ALARM:
			i = 0;
			c = Color.red;
			break;

		default:
			i = 0;
			c = Color.black;
			break;
		}
		
		Graphics2D g = (Graphics2D)d.getGraphics();
		
		g.setColor(c);
		int vx = (counter)%60+2;
		
		g.drawOval(32-vx/2, 32-vx/2, vx, vx);
		vx++;
		g.drawOval(32-vx/2, 32-vx/2, vx, vx);
		vx++;
		g.drawOval(32-vx/2, 32-vx/2, vx, vx);
		
		vx = (int)(10*Math.sin(Math.toRadians((double)((counter*8)%360))));
		double vy = 1.0+(double)vx/40;
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
		g.drawImage(work, 0, 0, null);
		work = d;
		g.translate(32, 32);
		g.scale(vy, vy);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(imas[i], -16, -16, null);
		
		if(counter>=240)
			current = 0;
	}
	
	public void paintPing(Graphics2D g){
		if(current != 0)
			g.drawImage(work, atX-32, atY-32, null);
	}
	
}
