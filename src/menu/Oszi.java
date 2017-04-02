package menu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Oszi extends Button{

	private boolean isRunning = true;
	
	private double resulutionY;
	
	private int xInOsz = 0;
	private int lastY;
	
	private Color line;
	
	private static final Color overPaint = new Color(0,0,0,3);
	
	private static final int tHight = 60;
	private static final int tWidth = 100;
	
	private BufferedImage buffer;
	
	public Oszi(int x, int y, Color l, int maxY) {
		super(x, y+tHight+2, "res/win/gui/cli/Gs");
		line = l;
		resulutionY = tHight/maxY;
		setText("Stop");
		clear();
	}
	
	public void clear(){
		buffer = new BufferedImage(tWidth, tHight, BufferedImage.TYPE_INT_ARGB);
		Graphics g = buffer.getGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, tWidth, tHight);
	}
	
	public void drawData(int data){
		if(!isRunning)return;
		xInOsz++;
		Graphics g = buffer.getGraphics();
		g.setColor(overPaint);
		g.fillRect(0, 0, tWidth, tHight);
		if(xInOsz >= tWidth)xInOsz = 0;
		g.setColor(line);
		int currY = (int)(data*resulutionY);
		g.drawLine(xInOsz,currY, xInOsz, lastY);
		lastY = currY;
	}
	
	public void paintYou(Graphics g){
		super.paintYou(g);
		g.drawImage(buffer, xPos, yPos-2-tHight, null);
	}

	@Override
	protected void isClicked() {
		isRunning = !isRunning;
		if(isRunning){setText("Stop");}
		else{setText("Rec");}
	}

	@Override
	protected void isFocused() {
	}

	@Override
	protected void uppdate() {
		
	}

}
