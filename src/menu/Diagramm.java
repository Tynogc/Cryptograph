package menu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Diagramm extends ScrollBar{
	
	private static final int hight = 240;
	private static final int totalHight = 1000;
	private final int width;
	
	private double i1;
	private double i2;
	private double i3;
	private Color c1;
	private Color c2;
	private Color c3;
	
	private String s1;
	private String s2;
	private String s3;
	
	private double multi1 = 1;
	private double multi2 = 1;
	private double multi3 = 1;
	
	private static final int zeroLine = 30;
	
	private boolean mark;
	
	private int smalMarks;
	private int largeMarks;
	private int text;
	
	private int continuusCount = 0;
	
	private int actY;

	private BufferedImage image;
	
	public Diagramm(int x, int y, int wi) {
		super(x+wi, y, hight-20, hight/20, totalHight/20);
		width = wi;
		actY = y;
		
		image = new BufferedImage(width, totalHight, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		g.setColor(Color.red);
		g.drawLine(0, 0, wi, 0);
		g.drawLine(0, 2, wi, 2);
	}

	public void paintYou(Graphics g){
		super.paintYou(g);
		if(image != null){
			g.drawImage(image.getSubimage(0,getScrolled()*20, width, hight-20), xPos-width, actY+20, null);
		}
		g.setFont(main.Fonts.font14);
		g.setColor(Color.red);
		if(s1 != null){
			g.setColor(c1);
			g.drawString(i1+" "+s1, (int)(xPos-i1*multi1)-zeroLine, actY+20);
		}
		if(s2 != null){
			g.setColor(c2);
			g.drawString(i2+" "+s2, (int)(xPos-i2*multi2)-zeroLine, actY+20);
		}
		if(s3 != null){
			g.setColor(c3);
			g.drawString(i3+" "+s3, (int)(xPos-i3*multi3)-zeroLine, actY+20);
		}
	}
	
	public void mark(){
		mark = true;
	}
	
	public void setColor(Color c1, Color c2, Color c3){
		this.c1 = c1;
		this.c2 = c2;
		this.c3 = c3;
	}
	
	public void setPostfixes(String pf1, String pf2, String pf3){
		s1 = pf1;
		s2 = pf2;
		s3 = pf3;
	}
	
	public void setMultipliers(double m1, double m2, double m3){
		multi1 = m1;
		multi2 = m2;
		multi3 = m3;
	}
	
	public void setImps(int large, int smal, int text){
		largeMarks = large;
		smalMarks = smal;
		this.text = text;
	}
	
	public void drawStatRect(double d1, int num){
		BufferedImage ima = new BufferedImage(width, totalHight, BufferedImage.TYPE_INT_RGB);
		Graphics g = ima.getGraphics();
		g.drawImage(image, 0, 20, null);
		g.setColor(c1);
		g.fillRect(1, 1, (int)(d1*multi1), 15);
		g.setColor(new Color(0,0,0,100));
		g.drawRect(1, 1, (int)(d1*multi1), 15);
		g.setColor(c2);
		g.setFont(main.Fonts.font14);
		g.drawString(d1+" ", 10, 14);
		g.drawString("#"+num, 170, 14);
		g.setColor(c3);
		g.drawLine(0,18, width,18);
		
		image = null;
		image = ima;
		
		continuusCount++;
	}
	
	public void drawStatLine(double l1, double l2, double l3){
		BufferedImage ima = new BufferedImage(width, totalHight, BufferedImage.TYPE_INT_RGB);
		Graphics g = ima.getGraphics();
		g.drawImage(image, 0, 1, null);
		g.setColor(Color.gray);
		for (int i = zeroLine; i < width; i+= largeMarks) {
			g.drawRect(width-i, 0, 0, 0);
		}
		if(continuusCount%10 < 5){
			for (int i = zeroLine; i < width; i+= smalMarks) {
				g.drawRect(width-i, 0, 0, 0);
			}
		}
		
		if(continuusCount%text == 0 || mark){
			g.setColor(c1);
			g.drawString(i1+"", width-(int)(i1*multi1)-zeroLine+1, 10);
			g.setColor(c2);
			g.drawString(i2+"", width-(int)(i2*multi2)-zeroLine+1, 10);
			g.setColor(c3);
			g.drawString(i3+"", width-(int)(i3*multi3)-zeroLine+1, 10);
			if(mark){
				mark = false;
				g.setColor(Color.red);
				g.drawLine(0, 0, width, 0);
			}
		}
		
		g.setColor(c3);
		g.drawLine(width-(int)(i3*multi3)-zeroLine, 0, width-(int)(l3*multi3)-zeroLine, 0);
		g.setColor(c2);
		g.drawLine(width-(int)(i2*multi2)-zeroLine, 0, width-(int)(l2*multi2)-zeroLine, 0);
		g.setColor(c1);
		g.drawLine(width-(int)(i1*multi1)-zeroLine, 0, width-(int)(l1*multi1)-zeroLine, 0);
		
		image = null;
		image = ima;
		i1 = l1;
		i2 = l2;
		i3 = l3;
		
		continuusCount++;
	}

}
