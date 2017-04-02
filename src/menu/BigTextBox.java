package menu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class BigTextBox extends Container{

	private String[] text;
	private BufferedImage ima;
	
	private int xSize;
	private int ySize;
	
	private Button up;
	private Button down;
	private ScrollBar scr;
	
	private int scroll;
	private final int linesToDisplay;
	
	private static final int scrollCap = 3;
	
	public BigTextBox(int x, int y, int xS, int yS) {
		super(x, y);
		xSize = xS;
		ySize = yS;
		linesToDisplay = yS/20;
		
		up = new Button(xS,0,"res/ima/cli/scr/1u") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				scr.scrolleByAmount(-10);
			}
		};
		down = new Button(xS,yS-20,"res/ima/cli/scr/1d") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				scr.scrolleByAmount(10);
			}
		};
		addInContainer(up);
		addInContainer(down);
		
		scr = new ScrollBar(xS, 20, yS-60, 30, 35);
		scr.setDisabled(true);
		up.setDisabled(true);
		down.setDisabled(true);
		addInContainer(scr);
		
		ima = new BufferedImage(xS, yS, BufferedImage.TYPE_INT_RGB);
		
		scrolled(0);
	}

	@Override
	public void paintYou(Graphics g) {
		if(scroll/scrollCap != scr.getScrolled())
			scrolled(scr.getScrolled());
		if(isVisible()){
			g.drawImage(ima, getxPos(), getyPos(), null);
		}
		super.paintYou(g);
	}
	
	public void setText(String[] s){
		text = s;
		
		removeIntern(scr);
		if(text.length<=linesToDisplay){
			scr = new ScrollBar(xSize, 20, ySize-60, 30, 35);
			scr.setDisabled(true);
			up.setDisabled(true);
			down.setDisabled(true);
		}else{
			scr = new ScrollBar(xSize, 20, ySize-60, linesToDisplay/scrollCap, text.length/scrollCap);
			scr.setDisabled(false);
			up.setDisabled(false);
			down.setDisabled(false);
		}
		addInContainer(scr);
		
		scrolled(0);
	}
	
	private void scrolled(int s){
		if(s>=0)
			scroll = s*scrollCap;
		Graphics2D g = (Graphics2D)ima.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, xSize, ySize);
		g.setColor(DataFiled.l1);
		g.drawRect(1, 1, xSize, ySize);
		g.setColor(DataFiled.l2);
		g.drawRect(0, 0, xSize, ySize);
		
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		g.setFont(main.Fonts.font10);
		for (int i = 0; i < linesToDisplay; i++) {
			g.setColor(Color.GRAY);
			if(i == linesToDisplay/2){
				g.setColor(Color.green);
				g.fillRect(2, i*20, xSize-3, 20);
				g.setColor(Color.black);
			}
			g.drawString(""+(i+scroll), 3, i*20+18);
		}
		
		if(text == null)return;
		
		g.setFont(main.Fonts.font14);
		g.setColor(Color.black);
		int u = 0;
		for (int i = 0; i < linesToDisplay; i++) {
			if(i+scroll >= text.length)break;
			if(i+scroll>=100)u = 6;
			if(i+scroll>=1000)u = 12;
			g.drawString(text[i+scroll], 20+u, i*20+18);
		}
	}
	
	public void goTo(int line){
		line-=linesToDisplay/2;
		scroll = line;
		line/=scrollCap;
		scr.scrolleByAmount(line-scr.getScrolled());
		scrolled(-1);
	}
}
