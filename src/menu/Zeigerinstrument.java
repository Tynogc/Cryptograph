package menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Zeigerinstrument {

	
	private int type;
	
	public static final int size = 130;
	
	private BufferedImage back;
	
	private int xPos;
	private int yPos;
	
	private final double maxValue;
	private double warnValue;
	private double alarmValue;
	
	private String text = "";
	private String postFix;
	
	public double value = 0;
	
	public int smalImpSize = 5;
	
	public static Color smalImpColor = new Color(250,250,250, 170);
	public static Color arrowColor = new Color(0,160,250);
	
	public static Color firstLineColor = new Color(127,127,127);
	public static Color secondLineColor = new Color(195,195,195);
	
	public static final int roud90Degree = 649;
	public static final int roud120Degree = 5423;
	
	public Zeigerinstrument(int x, int y, double max, double warn, double alarm, int type, String s){
		xPos = x-3;
		yPos = y-3;
		maxValue = max;
		warnValue = warn;
		alarmValue = alarm;
		this.type = type;
		
		postFix = s;
		
		back = new BufferedImage(size+10, size+10, BufferedImage.TYPE_INT_ARGB);
		Graphics g = back.getGraphics();
		g.setFont(main.Fonts.font14);
		switch (type) {
		case roud90Degree:
			for (double i = 0; i < 180; i++) {
				if(i > getValuePercent(warn)*180){
					g.drawRect(size-(int)(cosin(i/2)*100), size-(int)(sinus(i/2)*100), 0, 0);
					g.drawRect(size-(int)(cosin(i/2)*99), size-(int)(sinus(i/2)*99), 0, 0);
					g.setColor(Color.yellow);
				}
				if(i > getValuePercent(alarm)*180) g.setColor(Color.red);
			}
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
			g.setColor(Color.white);
			g.drawString(s, 10, 24);
			break;
			
		case roud120Degree:
			for (double i = 0; i < 270; i++) {
				if(i > getValuePercent(warn)*270){
					g.drawRect(size-40-(int)(cosin(i/2-45)*100/1.8), size-45-(int)(sinus(i/2-45)*100/1.8), 0, 0);
					g.drawRect(size-40-(int)(cosin(i/2-45)*99/1.8), size-45-(int)(sinus(i/2-45)*99/1.8), 0, 0);
					g.setColor(Color.yellow);
				}
				if(i > getValuePercent(alarm)*270) g.setColor(Color.red);
			}
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
			g.setColor(Color.white);
			g.drawString(s, 10, 24);
			break;

		default:
			break;
		}
	}
	
	public void drawImpLines(double steps, double smalSteps,  int markings){
		int c = markings;
		for (double i = 0; i <= maxValue; i += steps) {
			c++;
			if(c >= markings){
				c=0;
				impText(i);
			}
			impLine(i);
		}
		for (double i = 0; i < maxValue; i += smalSteps) {
			impLineSmal(i);
		}
	}
	
	public void markArea(double start, double end, Color c, boolean imps, boolean impText){
		double stepsToMake = 100;
		if(type == roud90Degree) stepsToMake = (double)maxValue/180;
		if(type == roud120Degree) stepsToMake = (double)maxValue/270;
		
		Graphics g = back.getGraphics();
		g.setColor(c);
		for (double i = start; i < end; i+= stepsToMake) {
			g.drawRect(getRadialCompoundCos(i, 100), getRadialCompoundSin(i, 100), 0, 0);
			g.drawRect(getRadialCompoundCos(i, 99), getRadialCompoundSin(i, 99), 0, 0);
		}
		if(imps){
			impLine(start, c);
			impLine(end, c);
			if(impText){
				impText(start);
				impText(end);
			}
		}
	}
	
	private void impLine(double value){
		impLine(value, Color.white);
	}
	private void impLine(double value, Color c){
		Graphics g = back.getGraphics();
		g.setColor(c);
		g.drawLine(getRadialCompoundCos(value, 100), getRadialCompoundSin(value,100),
				getRadialCompoundCos(value,110), getRadialCompoundSin(value,110));
	}
	private void impLineSmal(double value){
		Graphics g = back.getGraphics();
		g.setColor(smalImpColor);
		g.drawLine(getRadialCompoundCos(value,100), getRadialCompoundSin(value,100),
				getRadialCompoundCos(value,100+smalImpSize), getRadialCompoundSin(value,100+smalImpSize));
	}
	private void impText(double value){
		Graphics g = back.getGraphics();
		g.setColor(Color.white);
		String s = ""+(int)value;
		if(value<10)s = "   "+(int)value;
		g.drawString(s, getRadialCompoundCos(value,120)-10, getRadialCompoundSin(value,120)+5);
	}
	
	public void paintYou(Graphics g){
		g.drawImage(back, xPos, yPos, null);
		
		if(value>= alarmValue){
			g.setColor(Color.red);
		}else if(value>= warnValue){
			g.setColor(Color.yellow);
		}else{
			g.setColor(arrowColor);
		}
		
		g.drawLine(xPos+getRadialCompoundCos(value,99), yPos+getRadialCompoundSin(value,99),
				xPos+getRadialCompoundCos(value,30), yPos+getRadialCompoundSin(value,30));
		
		g.setColor(Color.black);
		g.fillRect(xPos+66, yPos+111, 63, 18);
		
		if(value>= alarmValue){
			g.setColor(Color.red);
		}else if(value>= warnValue){
			g.setColor(Color.yellow);
		}else{
			g.setColor(arrowColor);
		}
		g.drawString(text, xPos+70, yPos+126);
		
		g.setColor(firstLineColor);
		g.drawRect(xPos+3, yPos+3, size+5, size+5);
		g.drawRect(xPos+65, yPos+110, 65, 20);
		g.setColor(secondLineColor);
		g.drawRect(xPos+4, yPos+4, size+3, size+3);
		g.drawRect(xPos+66, yPos+111, 63, 18);
	}
	
	public static double sinus(double degree){
		double angle = (Math.PI/180)*degree;
		return Math.sin(angle);
	}
	public static double cosin(double degree){
		double angle = (Math.PI/180)*degree;
		return Math.cos(angle);
	}
	
	private double getValuePercent(double value){
		return (value/maxValue);
	}
	
	public void setValue(double v){
		value = v;
		if(v<0){
			text = (int)(value)+" "+postFix;
		}else if(v<100){
			text = (int)(value)+"."+(int)(value*10)%10+" "+postFix;
		}else{
			text = (int)(value)+" "+postFix;
		}
	}
	
	private int getRadialCompoundCos(double value, int distance){
		switch (type) {
		case roud90Degree:
			return size-(int)(cosin(getValuePercent(value)*90)*distance);

		case roud120Degree:
			return size-40-(int)(cosin(getValuePercent(value)*135-45)*distance/1.8);
			
		default:
			return 0;
		}
	}
	private int getRadialCompoundSin(double value, int distance){
		switch (type) {
		case roud90Degree:
			return size-(int)(sinus(getValuePercent(value)*90)*distance);
			
		case roud120Degree:
			return size-45-(int)(sinus(getValuePercent(value)*135-45)*distance/1.8);

		default:
			return 0;
		}
	}
}
