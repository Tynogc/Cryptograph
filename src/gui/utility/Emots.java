package gui.utility;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import main.PicLoader;

public class Emots {

	public static Emots emots;
	
	private BufferedImage loadingCircle;
	private BufferedImage loadingCircleRaw;
	private BufferedImage processingCircle;
	private BufferedImage processingCircleRaw;
	private byte loadingWasUsed;
	
	public Emots(){
		loadingCircleRaw = PicLoader.pic.getImage("res/ima/smi/cir1.png");
		loadingCircle = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		
		processingCircleRaw = PicLoader.pic.getImage("res/ima/smi/cir1.png");
		processingCircle = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		
		emots = this;
	}
	
	public void update(){
		if(loadingWasUsed == 1){
			loadingCircle = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
			loadingWasUsed = 0;
			return;
		}else if(loadingWasUsed == 0){
			return;
		}
		BufferedImage b = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D)b.getGraphics();
		g.translate(16, 16);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		double l = ((double)System.currentTimeMillis()/400)%(Math.PI*4)+Math.PI;
		g.rotate(l);
		g.drawImage(loadingCircleRaw, -16, -16, null);
		g.rotate(-l);
		g.translate(-16, -16);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.95f));
		g.drawImage(loadingCircle, 0, 0, null);
		loadingCircle = b;
		loadingWasUsed = 1;
		
		//////////////////////////////////////////////////////////////////////////////
		//Processing-Circle
		
		b = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		g = (Graphics2D)b.getGraphics();
		g.translate(16, 16);
		l = ((double)System.currentTimeMillis()/1200-0.15)%(Math.PI);
		l = Math.cos(l)*(Math.PI*4)+Math.PI;
		g.rotate(-l);
		g.drawImage(processingCircleRaw, -16, -16, null);
		g.rotate(l);
		l = ((double)System.currentTimeMillis()/1200)%(Math.PI);
		l = Math.cos(l)*(Math.PI*4)+Math.PI;
		g.rotate(-l);
		g.drawImage(processingCircleRaw, -16, -16, null);
		g.rotate(l);
		l = ((double)System.currentTimeMillis()/1200+0.15)%(Math.PI);
		l = Math.cos(l)*(Math.PI*4)+Math.PI;
		g.rotate(-l);
		g.drawImage(processingCircleRaw, -16, -16, null);
		g.rotate(l);
		g.translate(-16, -16);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
		g.drawImage(processingCircle, 0, 0, null);
		processingCircle = b;
		loadingWasUsed = 1;
	}
	
	public void drawLoadingCircle(Graphics g, int x, int y){
		g.drawImage(loadingCircle, x, y, null);
		main.EventCounter.event();
		loadingWasUsed = 2;
	}
	
	public void drawProcessingCircle(Graphics g, int x, int y){
		g.drawImage(processingCircle, x, y, null);
		main.EventCounter.event();
		loadingWasUsed = 2;
	}
}
