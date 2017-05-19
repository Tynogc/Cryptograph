package gui.chat;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Bubble {

	public final int xSize;
	public int ySize;
	
	public BufferedImage up;
	public BufferedImage mid;
	public BufferedImage down;
	private String[] lines;
	
	public final String userName;
	public final long timeStamp;
	public final Color color;
	
	public Bubble(int x, String s, Color c, String name){
		this(x, s, c, name, null);
	}
	
	public Bubble(int x, String s, Color c, String name, Bubble same){
		xSize = x;
		userName = name;
		timeStamp = System.currentTimeMillis();
		color = c;
		
		if(same != null){
			if(same.color.hashCode() == color.hashCode()){
				up = same.up;
				mid = same.mid;
				down = same.down;
			}
		}
		if(up == null){
			//TODO generate
		}
	}
}
