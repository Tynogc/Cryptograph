package menu;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

public class ScrollBar extends Button {

	public final static int width = 20;
	
	private Button subButtonUp;
	private Button subButtonDo;
	private ButtonInterface subB;
	
	private double sizeOfIteration;
	private int iterated;//Position
	private int iterations;//Number of Iterations
	private int mousePosYStart;
	private int startY;
	
	private long firstTick = 0;
	private byte tickState = 0;
	
	private boolean isState3 = false;
	
	private BufferedImage state3;
	
	public ScrollBar(int x, int y, int hi, int barHight, int iteratio) {
		super(x, y+20, width, (hi-20)/(iteratio)*barHight);
		startY = y+20;
		sizeOfIteration = (double)(hi-20)/(iteratio);
		this.iterations = iteratio-barHight;
		iterated = 0;
		subB = new EndButtonList();
		subButtonUp = new Button(x,y,20,width) {
			
			@Override
			protected void isFocused() {
				isState3 = true;
				if(subButtonUp.isOnClick()){
					if(setATick()){
						scUp();
					}
				}else{
					tickState = 0;
				}
			}
			
			@Override
			protected void isClicked() {
				
			}

			@Override
			protected void uppdate() {
				
			}
		};
		subB = subB.add(subButtonUp);
		subButtonDo = new Button(x,y+hi,20,width) {
			
			@Override
			protected void isFocused() {
				isState3 = true;
				if(subButtonDo.isOnClick()){
					if(setATick()){
						scDo();
					}
				}else{
					tickState = 0;
				}
			}
			
			@Override
			protected void isClicked() {
				
			}

			@Override
			protected void uppdate() {
				
			}
		};
		subB = subB.add(subButtonDo);
		setFilePath("res/ima/cli/scr/1");
	}
	
	public ScrollBar(int x, int y, int hi, int barHight, int iteratio, String filePath){
		this(x, y, hi, barHight, iteratio);
		setFilePath(filePath);
	}
	
	public void setFilePath(String s){
		state1 = generateInternScrollbar(s+"n");
		state2 = generateInternScrollbar(s+"c");
		state3 = generateInternScrollbar(s+"e");
		stateFoc = generateInternScrollbar(s+"f");
		stateDis = generateInternScrollbar(s+"d");
		
		if(stateFoc != null){
			focusOffsetX = (stateFoc.getWidth()-xSize)/2;
			focusOffsetY = (stateFoc.getHeight()-ySize)/2;
		}else{
			focusOffsetX = 0;
			focusOffsetY = 0;
		}
		
		//super.setFilePath(s);
		subButtonUp.setFilePath(s+"u");
		subButtonDo.setFilePath(s+"d");
	}
	
	private BufferedImage generateInternScrollbar(String s){
		ImageIcon imQ = new ImageIcon(s+"q.png");
		ImageIcon imR = new ImageIcon(s+"r.png");
		ImageIcon imS = new ImageIcon(s+"s.png");
		ImageIcon imT = new ImageIcon(s+"t.png");
		
		int x = imQ.getIconWidth();
		if(x < 3)return null;
		BufferedImage bu = new BufferedImage(x, ySize, BufferedImage.TYPE_INT_ARGB);
		
		Graphics g = bu.getGraphics();
		g.drawImage(imQ.getImage(), 0, 0, null);
		g.drawImage(imT.getImage(), 0, ySize-8, null);
		
		for (int i = 8; i < ySize-8; i ++) {
			g.drawImage(imR.getImage(), 0, i, null);
		}
		
		g.drawImage(imS.getImage(), 0, (ySize/2)-7, null);
		
		return bu;
	}
	
	public void setDisabled(boolean state){
		super.setDisabled(state);
		subButtonDo.setDisabled(state);
		subButtonUp.setDisabled(state);
	}

	@Override
	protected void isClicked() {
		
	}

	@Override
	protected void isFocused() {
		
	}
	
	private void scrolled(){
		setyPos((int)(iterated*sizeOfIteration)+startY);
	}
	private void scUp(){
		iterated--;
		if(iterated<0)iterated = 0;
		scrolled();
	}
	private void scDo(){
		iterated++;
		if(iterated>iterations)iterated = iterations;
		scrolled();
	}
	
	public void scrolleByAmount(int am){
		iterated+=am;
		if(iterated>iterations)iterated = iterations;
		if(iterated<0)iterated = 0;
		scrolled();
	}
	
	public void leftClicked(int x, int y){
		super.leftClicked(x, y);
		mousePosYStart = y-getyPos();
		subB.leftClicked(x, y);
		firstTick = System.currentTimeMillis()+500;
	}
	public void leftReleased(int x, int y){
		super.leftReleased(x, y);
		subB.leftReleased(x, y);
	}
	public void checkMouse(int x, int y){
		super.checkMouse(x, y);
		isState3 = focused;
		subB.checkMouse(x, y);
		
		if(isOnClick()){
			focused = true;
			focusedAt = 1;
			//isState3 = true;
			int tgf = (int)((mouseY-startY-mousePosYStart)/sizeOfIteration);
			if(tgf != iterated){
				if(tgf<iterated){
					if(System.currentTimeMillis()-firstTick>1000/iterations){
						scUp();
						firstTick = System.currentTimeMillis();
					}
				}else{
					if(System.currentTimeMillis()-firstTick>1000/iterations){
						scDo();
						firstTick = System.currentTimeMillis();
					}
				}
			}
			
		}
	}
	public void paintYou(Graphics g){
		if(isState3){
			if(!icPaintSmoothImA(xPos, yPos, state3, g)){
				g.setColor(Color.green);
				g.fillRect(xPos, yPos, xSize, ySize);
			}
		}
		super.paintYou(g);
		subB.paintYou(g);
	}
	
	protected boolean icPaintSmoothImA(int x, int y, BufferedImage i, Graphics g){
		if(i == null)return false;
		if(smoothFocusing){
			long thpl;
			if(focusedAt != 0){
				thpl = System.currentTimeMillis()-focusedAt;
			}else if(subButtonUp.focusedAt != 0){
				thpl = System.currentTimeMillis()-subButtonUp.focusedAt;
			}else{
				thpl = System.currentTimeMillis()-subButtonDo.focusedAt;
			}
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
	
	private boolean setATick(){
		switch (tickState) {
		case 0:
			firstTick = System.currentTimeMillis();
			tickState = 1;
			return true;
			
		case 1:
			if(System.currentTimeMillis()-firstTick<1000)return false;
			firstTick = System.currentTimeMillis();
			tickState = 2;
			return true;
			
		case 2:
			if(System.currentTimeMillis()-firstTick<300)return false;
			firstTick = System.currentTimeMillis();
			return true;

		default:
			return false;
		}
	}
	
	public int getScrolled(){
		return iterated;
	}

	@Override
	protected void uppdate() {
		// TODO Auto-generated method stub
		
	}

}
