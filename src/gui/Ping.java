package gui;

import java.awt.image.BufferedImage;


public class Ping {

	private static int atX;
	private static int atY;
	
	private static int current;
	private static int counter;
	
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
	
	public Ping(){
		imas = new BufferedImage[]{};
	}
	
}
