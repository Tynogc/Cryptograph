package debug;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.concurrent.Semaphore;

import javax.swing.JLabel;

import org.omg.CORBA.FREE_MEM;

public class Debug {
	
	public static final int ERROR = 10;
	public static final int SUBERR = 11;
	
	public static final int FATAL = 96;
	
	public static final int COM = 30;
	public static final int SUBCOM = 31;
	public static final int COMERR = 32;
	public static final int PRICOM = 33;
	
	public static final int WARN = 41;
	public static final int SUBWARN = 42;
	
	public static final int REMOTE = 77;
	
	public static final int MESSAGE = 80;
	
	public static final int TEXT = 100;
	
	public static final int sizeX = 500;
	public static final int sizeY = 800;
	
	public static int line = 0;
	private static int letter = 0;
	
	public static DebPanel panel;
	//public static DebugMenu menu;
	
	public static BufferedImage i1;
	public static BufferedImage i2;
	
	public static Font font;
	public static Font font2;
	
	private static long time = 0;
	
	public static final int textWidth = 9;
	
	private static long debMemoryFree = 0;
	private static long debMemoryUsed = 0;
	private static long debMemoryMax = 0;
	
	private static String theLineBefor = "";
	
	public static String logFilepath = "log/def.txt";
	
	private static Runtime runtime;
	
	private static boolean lastThingIsProgressBar;
	
	public static boolean showExtendedBootInfo = false;
	
	private static Semaphore sema;
	
	static{
		sema = new Semaphore(1);
		try {
			sema.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sema.release();
		logFilepath = "log/"+
				new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm").format(new java.util.Date (System.currentTimeMillis()))+
				"-LOG.txt";
		//userInterface.ExitThread.setFilePath(logFilepath);
		
		runtime = Runtime.getRuntime();
		
		i1 = new BufferedImage(sizeX+100, sizeY, BufferedImage.TYPE_INT_ARGB);
		i2 = new BufferedImage(sizeX+100, sizeY, BufferedImage.TYPE_INT_ARGB);
		Graphics g = i1.getGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, sizeX+200, sizeY);
		g = i2.getGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, sizeX+200, sizeY);
		
		try
        {
            font = Font.createFont(Font.TRUETYPE_FONT, new File("res/font/FreeMono.ttf"));
            font = font.deriveFont(16f);
        }
        catch(Exception e)
        {
    		font = new Font(Font.MONOSPACED, Font.BOLD, 14);
            e.printStackTrace();
        }
		try
        {
            font2 = Font.createFont(Font.TRUETYPE_FONT, new File("res/font/FreeSans.ttf"));
            font2 = font2.deriveFont(14f);
        }
        catch(Exception e)
        {
        	font2 = new JLabel().getFont();
            e.printStackTrace();
        }
		
		//font = new Font(Font.MONOSPACED, Font.BOLD, 14);
		//font2 = font;
		
		PrintWriter writer = null; 
		try { 
			writer = new PrintWriter(new FileWriter(logFilepath)); 
			writer.println("This is the Log of all Console Data: "+
					new java.text.SimpleDateFormat("dd.MM.yy").format(new java.util.Date (System.currentTimeMillis())));
			writer.println("-------------------------------------------");
		} catch (IOException ioe) { 
			ioe.printStackTrace(); 
		} finally { 
			if (writer != null){ 
				writer.flush(); 
				writer.close(); 
			} 
		}
	}
	
	public static void print(String s, int color){
		try {
			sema.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		print(s, color, false);
		sema.release();
	}
	
	private static void print(String s, int color, boolean b){
		if(s == null)return;
		if(!b)
		theLineBefor+=s;
		
		lastThingIsProgressBar = false;
		
		Graphics g1 = i1.getGraphics();
		Graphics g2 = i2.getGraphics();
		
		Graphics2D g2d1 = (Graphics2D)g1;
		g2d1.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		Graphics2D g2d2 = (Graphics2D)g2;
		g2d2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		g2d1.setColor(getCol(color));
		g2d2.setColor(getCol(color));
		
		g2d1.setFont(font);
		g2d2.setFont(font);
		for (int i = 0; i < s.length(); i++) {
			String abc = "";
			abc += s.charAt(i);
			if(line>(sizeY/10)-1){
				g2d2.drawString(abc, letter*textWidth, 10*line+10-sizeY);
			}else{
				g2d1.drawString(abc, letter*textWidth, 10*line+10);
			}
			letter++;
			checkLetter();
		}
		if(panel != null){
			panel.paint(panel.getGraphics());
		}
		/*if(menu != null){
			menu.paintDebug();
		}*/
	}
	
	public static void println(String s, int color){
		if(s == null)return;
		try {
			sema.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		lastThingIsProgressBar = false;
		
		line++;
		letter = 0;
		checkLine();
		
		PrintWriter writer = null;
		if(theLineBefor.length()>2){
		try { 
			writer = new PrintWriter(new FileWriter(logFilepath, true),true);
			
			writer.println("[cont    ][  ]"+theLineBefor);
			theLineBefor = "";
			
		} catch (IOException ioe) { 
			ioe.printStackTrace(); 
		} finally { 
			if (writer != null){ 
				writer.flush(); 
				writer.close(); 
			} 
		}
		}
		
		if(s.length() <= 0){
			sema.release();
			return;
		}
		char c = s.charAt(0);
		if(c == '*' ||c == '/'){
			doTimeStamp();
		}else if(c == ' '){
			doTimProg();
		}else{
			s = " "+s;
			doTimProg();
		}
		
		String kse = getPreString(color);
		
		try { 
			writer = new PrintWriter(new FileWriter(logFilepath, true),true);
			writer.println(kse+" "+s);
		} catch (IOException ioe) { 
			ioe.printStackTrace(); 
		} finally { 
			if (writer != null){ 
				writer.flush(); 
				writer.close(); 
			} 
		}
		print(s, color, true);
		sema.release();
	}
	
	public static void remove(int num){
		letter-=num;
		if(letter < 0) letter = 0;
		if(line>(sizeY/10)-1){
			Graphics g1 = i2.getGraphics();
			g1.setColor(Color.black);
			g1.fillRect(letter*textWidth+1, 10*line-sizeY, num*6+5, 15);
		}else{
			Graphics g1 = i1.getGraphics();
			g1.setColor(Color.black);
			g1.fillRect(letter*textWidth+1, 10*line, num*6+5, 15);
		}
		if(panel != null){
			panel.paint(panel.getGraphics());
		}
		/*if(menu != null){
			menu.paintDebug();
		}*/
		
		if(theLineBefor.length()>=num){
			theLineBefor = theLineBefor.substring(0, theLineBefor.length()-num);
		}else{
			theLineBefor = "";
		}
	}
	
	public static void addString(String s, int color){
		println(s,color);
	}
	
	private static void checkLine(){
		if(line>(sizeY/5)-2){
			line = 0;
			Graphics g = i1.getGraphics();
			g.setColor(Color.black);
			g.fillRect(0, 0, sizeX+200, sizeY);
		}
		if(line==(sizeY/10)-1){
			Graphics g = i2.getGraphics();
			g.setColor(Color.black);
			g.fillRect(0, 0, sizeX+200, sizeY);
		}
	}
	
	private static void checkLetter(){
		if(letter>(sizeX/textWidth)-1){
			line++;
			letter = 0;
			checkLine();
		}
	}
	
	public static void printProgressBar(long amount, long max, int color, boolean num){
		if(lastThingIsProgressBar)remove(40);
		
		double percent = (double)(amount)/max;
		Graphics g1 = i1.getGraphics();
		Graphics g2 = i2.getGraphics();
		
		g1.setColor(getCol(color));
		g2.setColor(getCol(color));
		String strp = "";
		for (int i = 0; i < 20; i++) {
			if(i*0.05 <= percent){
				strp +="|";
				if(line>(sizeY/10)-1){
					g2.fillRect(letter*textWidth+1, 10*line-sizeY+1, textWidth-2, 8);
				}else{
					g1.fillRect(letter*textWidth+1, 10*line+1, textWidth-2, 8);
				}
			}else{
				strp += ".";
				if(line>(sizeY/10)-1){
					g2.drawRect(letter*textWidth, 10*line-sizeY, textWidth-2, 9);
				}else{
					g1.drawRect(letter*textWidth, 10*line, textWidth-2, 9);
				}
			}
			letter++;
			checkLetter();
		}
		theLineBefor += strp;
		if(num){
			try {
				sema.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			print((int)(percent*100)+"%",color, false);
			sema.release();
		}
		lastThingIsProgressBar = true;
	}
	
	private static Color getCol(int i){
		switch(i){
		case ERROR: return Color.red;
		case SUBERR: return new Color(170,0,0);
		
		case COM: return new Color(0,100,250);
		case SUBCOM: return new Color(110,60,250);
		case COMERR: return new Color(200,0,130);
		case PRICOM: return new Color(130,130,130);
		
		case WARN: return new Color(250,250,0);
		case SUBWARN: return new Color(160,200,0);
		
		case MESSAGE: return new Color(0,180,0);
		
		case FATAL: return new Color(250,11,255);
		
		case REMOTE: return new Color(188,167,237);
		
		case TEXT: return Color.white;
		}
		return Color.darkGray;
	}
	
	private static String getPreString(int i){
		switch(i){
		case ERROR: return "[ER]";
		case SUBERR: return "[SE]";
		
		case COM: return "[co]";
		case SUBCOM: return "[sc]";
		case COMERR: return "[CE]";
		case PRICOM: return "[cp]";
		
		case WARN: return "[WA]";
		case SUBWARN: return "[SW]";
		
		case MESSAGE: return "[--]";
		
		case FATAL: return "[XX]";
		
		case TEXT: return "[--]";
		
		case REMOTE: return"[re]";
		}
		return "[??]";
	}
	
	private static void doTimeStamp(){
		time = System.currentTimeMillis();
		String date = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date (time));
		if(line>(sizeY/10)-1){
			Graphics2D g = (Graphics2D)i2.getGraphics();
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setFont(font2);
			g.setColor(Color.white);
			g.drawString("["+date+"]", sizeX, 10*line-sizeY+10);
		}else{
			Graphics2D g = (Graphics2D)i1.getGraphics();
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setFont(font2);
			g.setColor(Color.white);
			g.drawString("["+date+"]", sizeX, 10*line+10);
		}
		
		PrintWriter writer = null; 
		try { 
			writer = new PrintWriter(new FileWriter(logFilepath, true),true); 
			writer.print("["+date+"]");
		} catch (IOException ioe) { 
			ioe.printStackTrace();
		} finally { 
			if (writer != null){ 
				writer.flush(); 
				writer.close(); 
			} 
		}
		
	}
	
	private static void doTimProg(){
		if(line>(sizeY/10)-1){
			Graphics2D g = (Graphics2D)i2.getGraphics();
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setFont(font2);
			g.setColor(Color.white);
			g.drawString("[ +"+(System.currentTimeMillis()-time)+"]", sizeX, 10*line-sizeY+10);
		}else{
			Graphics2D g = (Graphics2D)i1.getGraphics();
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setFont(font2);
			g.setColor(Color.white);
			g.drawString("[ +"+(System.currentTimeMillis()-time)+"]", sizeX, 10*line+10);
		}
		String s = "[ +"+(System.currentTimeMillis()-time);
		for (int i = s.length(); i < 9; i++) {
			s+=" ";
		}
		PrintWriter writer = null; 
		try { 
			writer = new PrintWriter(new FileWriter(logFilepath, true),true); 
			writer.print(s+"]");
		} catch (IOException ioe) { 
			ioe.printStackTrace(); 
		} finally { 
			if (writer != null){ 
				writer.flush(); 
				writer.close(); 
			} 
		}
	}
	
	public static void knowMemory(){
		debMemoryFree = runtime.freeMemory();
		debMemoryUsed = runtime.totalMemory();
		debMemoryMax = runtime.maxMemory();
	}
	
	public static void displayMemory(int color){
		println("  Free Memory: "+debMemoryFree, color);
		println("  Used Memory: "+debMemoryUsed+", Maximum Memory: "+debMemoryMax, color);
		println("  "+((debMemoryUsed-debMemoryFree)/1000000)+"Mb in use:  ", color);
		printProgressBar((debMemoryUsed-debMemoryFree), debMemoryMax, color, true);
		println("  "+(debMemoryUsed)/1000000+"Mb reserved: ", color);
		printProgressBar(debMemoryUsed, debMemoryMax, color, true);
	}
	
	public static void bootMsg(String s, int state){
		int i = s.length();
		for (; i < 20; i++) {
			s+=" ";
		}
		if(state == 0){
			println(s+"[ OK  ]");
			remove(6);
			print(" OK  ",MESSAGE);
			print("]");
		}else if(state == 1){
			println(s+"[ERROR]");
			remove(6);
			print("ERROR",ERROR);
			print("]");
		}else if(state == 2){
			println(s+"[WARN ]");
			remove(6);
			print("WARN ",WARN);
			print("]");
		}else{
			println(s+"[ *** ]");
			remove(6);
			print(" *** ",COM);
			print("]");
		}
		theLineBefor = "";
	}
	
	public static void println(String s){
		println(s,TEXT);
	}
	
	public static void print(String s){
		print(s,TEXT);
	}
	
	public static void printExeption(Exception e){
		PrintStream p = new PrintStream(new OutputStream() {
			
			@Override
			public void write(int b) throws IOException {
				
			}
		}){
			@Override
			public void print(String s) {
				Debug.print(s, ERROR);
				super.print(s);
			}
			@Override
			public void println(String s) {
				Debug.println(s, ERROR);
				super.println(s);
			}
			@Override
			public void println(char[] x) {
				Debug.println(String.copyValueOf(x), ERROR);
				super.println(x);
			}
			public void print(char[] x) {
				Debug.print(String.copyValueOf(x), ERROR);
				super.print(x);
			}
		};
		
		e.printStackTrace(p);
		e.printStackTrace(System.err);
	}
}
