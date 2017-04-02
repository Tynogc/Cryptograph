package menu;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public abstract class Button implements ButtonInterface {
	
	protected ButtonInterface next;
	
	protected boolean status = false;
	protected boolean focused = false;
	protected boolean disabled = false;
	protected boolean visible = true;
	protected boolean lastClicked = false;
	
	protected  int xPos;
	protected  int yPos;
	protected  int xSize;
	protected  int ySize;
	
	protected int focusOffsetX = 0;
	protected int focusOffsetY = 0;
	
	protected int mouseX;
	protected int mouseY;
	
	protected String text;
	protected String subtext;
	protected String subtext2;
	protected int offsettext;
	protected int offsetSubtext2;
	protected int offsetTextY = 0;
	protected BufferedImage state1;
	protected BufferedImage state2;
	protected BufferedImage stateDis;
	protected BufferedImage stateFoc;
	
	public static boolean smoothFocusing = true;
	public static int smoothFocusingTime = 1000;
	public long focusedAt = 0;
	
	public static final Color grayDisabled = new Color(100,100,100);
	public static final Color gray = new Color(200,200,200);
	public static final Color blue = new Color(10,10,250);
	public static final Color black = new Color(0,0,0);
	public static final Color brown = new Color(185,120,87);
	public static final Color lightBlue = new Color(83,134,200);
	
	protected Color textColor = blue;
	
	protected Font font;
	protected boolean bold = false;
	protected boolean big = false;
	
	public static Font plainFont;
	public static Font boldFont12;
	public static Font boldFont14;
	
	public Button(int x, int y, int wi, int hi){
		xPos = x;
		yPos = y;
		xSize = wi;
		ySize = hi;
		
		if(plainFont == null){
			try
	        {
	            plainFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/font/FreeSerif.ttf"));
	            plainFont = plainFont.deriveFont(14f);
	        }
	        catch(Exception e)
	        {
	        	plainFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
	        	e.printStackTrace();
	        }
			try
	        {
	            boldFont12 = Font.createFont(Font.TRUETYPE_FONT, new File("res/font/FreeSerifBold.ttf"));
	            boldFont12 = boldFont12.deriveFont(12f);
	        }
	        catch(Exception e)
	        {
	        	boldFont12 = new Font(Font.SANS_SERIF, Font.BOLD, 12);
	        	e.printStackTrace();
	        }
			try
	        {
				boldFont14 = Font.createFont(Font.TRUETYPE_FONT, new File("res/font/FreeSerifBold.ttf"));
				boldFont14 = boldFont14.deriveFont(14f);
	        }
	        catch(Exception e)
	        {
	        	boldFont14 = new Font(Font.SANS_SERIF, Font.BOLD, 14);
	        	e.printStackTrace();
	        }
		}
		
		setBold(true);
		setBig(true);
	}
	
	public Button(int x, int y, String filePath){
		xPos = x;
		yPos = y;
		setFilePath(filePath);
		
		setBold(true);
		setBig(true);
	}
	
	public void setBig(boolean big){
		this.big = big;
		setBold(bold);
	}
	
	public void setBold(boolean bolt){
		bold = bolt;
		if(bold){
			int b = 12;
			if(big)b = 14;
			font = new Font(Font.SANS_SERIF, Font.BOLD, b);
		}else{
			font = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
		}
		setTextOffset();
	}
	
	protected void setTextOffset(){
		if(bold){
			if(big){
				if(text != null)
				offsettext = xSize/2-(text.length()*9)/2;
				if(subtext2 != null)
				offsetSubtext2 = xSize/2-(subtext2.length()*9)/2;
			}else{
				if(text != null)
				offsettext = xSize/2-(text.length()*8)/2;
				if(subtext2 != null)
				offsetSubtext2 = xSize/2-(subtext2.length()*8)/2;
			}
		}else{
			if(text != null)
			offsettext = xSize/2-(text.length()*7)/2;
			if(subtext2 != null)
			offsetSubtext2 = xSize/2-(subtext2.length()*7)/2;
		}
		if(offsettext < 3) offsettext = 3;
		if(offsetSubtext2 < 3) offsetSubtext2 = 3;
	}
	
	public Color getTextColor() {
		return textColor;
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}

	public String getSubtext() {
		return subtext;
	}

	public void setSubtext(String subtext) {
		this.subtext = subtext;
		setTextOffset();
	}

	public String getSecondLine() {
		return subtext2;
	}

	public void setSecondLine(String subtext2) {
		this.subtext2 = subtext2;
		setTextOffset();
	}
	
	public void setVisible(boolean vis){
		visible = vis;
	}

	public void setFilePath(String path){
		state1 = createNewBufferedImage(path+"n.png");
		state2 = createNewBufferedImage(path+"c.png");
		stateFoc = createNewBufferedImage(path+"f.png");
		stateDis = createNewBufferedImage(path+"d.png");
		if(state1 != null){
			xSize = state1.getWidth();
			ySize = state1.getHeight();
		}else{
			stateFoc = null;
			stateDis = null;
			state1 = null;
			state2 = null;
		}
		if(stateFoc != null){
			focusOffsetX = (stateFoc.getWidth()-xSize)/2;
			focusOffsetY = (stateFoc.getHeight()-ySize)/2;
		}else{
			focusOffsetX = 0;
			focusOffsetY = 0;
		}
		
		if(getySize()<3)setySize(10);
		if(getxSize()<3)setxSize(10);
	}
	
	public void setNext(ButtonInterface b){
		next = b;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		setTextOffset();
	}

	public int getxPos() {
		return xPos;
	}

	public void setxPos(int xPos) {
		this.xPos = xPos;
	}

	public int getyPos() {
		return yPos;
	}

	public void setyPos(int yPos) {
		this.yPos = yPos;
	}

	public int getxSize() {
		return xSize;
	}

	public void setxSize(int xSize) {
		this.xSize = xSize;
		setTextOffset();
	}

	public int getySize() {
		return ySize;
	}

	public void setySize(int ySize) {
		this.ySize = ySize;
	}

	public ButtonInterface add(ButtonInterface b){
		next = next.add(b);
		return this;
	}
	
	public boolean wasLastClicked(){
		return lastClicked;
	}
	
	public ButtonInterface remove(Button b){
		if(b == this){
			return next;
		}
		next = next.remove(b);
		return this;
	}
	
	public void leftClicked(int x, int y){
		if(isMouseHere(x, y)){
			status = true;
		}
		next.leftClicked(x, y);
	}
	
	public void leftReleased(int x, int y){
		lastClicked = false;
		if(isMouseHere(x, y)){
			if(status){
				if(text != null) debug.Debug.println("* Button Pressed: "+text, debug.Debug.COM);
				else if(subtext != null)debug.Debug.println("* Button Pressed: "+subtext, debug.Debug.COM);
				isClicked();
				lastClicked = true;
			}
		}
		status = false;
		next.leftReleased(x, y);
	}
	
	public void checkMouse(int x, int y){
		uppdate();
		if(isMouseHere(x, y)){
			isFocused();
			focused = true;
			if(focusedAt == 0){
				focusedAt = System.currentTimeMillis();
			}
		}else{
			focused = false;
			focusedAt = 0;
		}
		mouseX = x;
		mouseY = y;
		next.checkMouse(x, y);
	}
	
	public void setDisabled(boolean state){
		disabled = state;
	}
	
	public void paintYou(Graphics g){
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		if(!visible){
			next.paintYou(g);
			return;
		}
		if(disabled){
			if(!icPaintB(xPos, yPos, stateDis, g)){
				g.setColor(Color.gray);
				g.drawRect(xPos, yPos, xSize, ySize);
			}
		}
		else if(status){
			if(!icPaintB(xPos, yPos, state2, g)){
				g.setColor(new Color(100,100,100,100));
				g.fillRect(xPos, yPos, xSize, ySize);
			}
		}else{
			if(!icPaintB(xPos, yPos, state1, g)){
				g.setColor(Color.black);
				g.drawRect(xPos, yPos, xSize, ySize);
			}
		}
		g.setFont(font);
		if(focused){
			if(!icPaintSmooth(xPos-focusOffsetX, yPos-focusOffsetY, stateFoc, g)){
			g.setColor(Color.blue);
			g.drawRect(xPos, yPos, xSize, ySize);
			}
			g.setColor(Color.yellow);
			if(subtext != null)g.drawString(subtext, mouseX+2, mouseY);
		}
		if(text!= null){
			g.setColor(textColor);
			if(disabled)g.setColor(grayDisabled);
			if(subtext2 != null){
				g.drawString(text, xPos+offsettext, yPos+(ySize/2)-2);
				g.drawString(subtext2, xPos+offsetSubtext2, yPos+(ySize/2)+12);
			}else{
				g.drawString(text, xPos+offsettext, yPos+(ySize/2)+5+offsetTextY);
			}
		}
		next.paintYou(g);
	}
	
	protected boolean icPaintB(int x, int y, BufferedImage i, Graphics g){
		if(i== null)return false;
		g.drawImage(i, x, y, null);
		return true;
	}
	
	protected boolean icPaintSmooth(int x, int y, BufferedImage i, Graphics g){
		if(i == null)return false;
		if(smoothFocusing){
			long thpl = System.currentTimeMillis()-focusedAt; 
			if(thpl<smoothFocusingTime){
				BufferedImage im = new BufferedImage(i.getWidth(), i.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = im.createGraphics();
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ((float)thpl/smoothFocusingTime)));
				g2d.drawImage(i, 0, 0, null);
				return icPaintB(x,y,im, g);
			}
		}
		return icPaintB(x,y,i, g);
	}
	
	protected boolean isMouseHere(int x, int y){
		if(disabled)return false;
		if(!visible)return false;
		return x>=xPos&&y>=yPos&&x<=(xPos+xSize)&&y<=(yPos+ySize); 
	}
	
	public boolean isOnClick(){
		return status;
	}
	
	protected abstract void isClicked();
	protected abstract void isFocused();
	protected abstract void uppdate();
	
	public String toString(){
		if(next != null)
		return text+" "+next.toString();
		return text;
	}
	
	public void longTermUpdate(){
		if(next!=null)
			next.longTermUpdate();
	}
	
	private BufferedImage createNewBufferedImage(String filePath){
		BufferedImage b = main.PicLoader.pic.getImage(filePath);
		if(b.getHeight()<2){
			return null;
		}
		
		return b;
	}
	
	public boolean isVisible(){
		return visible;
	}

}
