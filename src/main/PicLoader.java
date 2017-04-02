package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.ImageIcon;

public class PicLoader {
	
	private String[] names;
	private BufferedImage[] imas;
	private int count;
	
	private BufferedImage empty;
	
	public static PicLoader pic;
	
	private static final String filepath = "res/images.txt";
	
	public PicLoader(){
		pic = this;
		count  = 0;
		names = new String[500];
		imas = new BufferedImage[500];
		debug.Debug.println("Loading images...");
		
		empty = new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB);
		Graphics g = empty.getGraphics();
		g.setColor(Color.red);
		g.fillRect(0, 0, 10, 10);
		
		for (int i = 0; i < imas.length; i++) {
			imas[i] = empty;
		}
		
		try{
		FileReader fr = new FileReader(filepath);
	    BufferedReader br = new BufferedReader(fr);

	    
	    for (int i = 0; i < names.length; i++) {
			names[i] = br.readLine();
			if(names[i] == null)break;
			count++;
		}
	    count--;
	    
	    br.close();
		}catch (FileNotFoundException e) {
			debug.Debug.println(e.getMessage(), debug.Debug.ERROR);
			debug.Debug.bootMsg("Images Loaded", 1);
			return;
		}catch (IOException e) {
			debug.Debug.println(e.getMessage(), debug.Debug.ERROR);
			debug.Debug.bootMsg("Images Loaded", 1);
		}
		debug.Debug.println(" "+count+" Images found. Loading...");
		debug.Debug.println("0%");
		for (int i = 0; i <= count; i++) {
			loadImage(i);
			if(i%10==0 && !debug.Debug.showExtendedBootInfo){
				debug.Debug.printProgressBar(i, count, debug.Debug.TEXT, true);
			}
		}
		if(!debug.Debug.showExtendedBootInfo)
		debug.Debug.printProgressBar(count, count, debug.Debug.TEXT, true);
		
		debug.Debug.bootMsg("Images Loaded", 0);
	}
	
	private void loadImage(int c){
		if(c>=imas.length){
			debug.Debug.println("ERROR PicLoader01: Array OOB", debug.Debug.ERROR);
			return;
		}
		String s = names[c];
		if(s == null){
			debug.Debug.println("ERROR PicLoader02: String is null", debug.Debug.ERROR);
			return;
		}
		ImageIcon im = new ImageIcon(s);
		if(im.getIconHeight() <= 1){
			if(debug.Debug.showExtendedBootInfo)
				debug.Debug.println("Image "+s+" not found!", debug.Debug.SUBWARN);
			return;
		}
		imas[c] = new BufferedImage(im.getIconWidth(), im.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		imas[c].getGraphics().drawImage(im.getImage(), 0,0,null);
	}
	
	public BufferedImage getImage(String s){
		if(s == null){
			debug.Debug.println("Error PicLoader03: String is null!");
			return empty;
		}
		if(s.length()<=1){
			return empty;
		}
		for (int i = 0; i < count; i++) {
			if(s.compareTo(names[i])==0){
				return imas[i];
			}
		}
		count++;
		names[count] = s;
		debug.Debug.println("Image "+s+" is now being loaded!", debug.Debug.MASSAGE);
		loadImage(count);
		
		PrintWriter writer = null; 
		try { 
			writer = new PrintWriter(new FileWriter(filepath, true),true); 
			writer.println(s);
		} catch (IOException ioe) { 
			ioe.printStackTrace(); 
		} finally { 
			if (writer != null){ 
				writer.flush(); 
				writer.close(); 
			} 
		}
		
		return imas[count];
	}

}
