package gui.sub;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import menu.Button;
import menu.Container;
import menu.ScrollBar;
import user.SideDisplay;

public class SideContainer extends Container{

	private Button[] buttons;
	private ScrollBar scr;
	
	private int lastScr;
	
	private SideDisplay[] sd;
	
	private boolean group;
	
	private Button[] groupButt;
	
	public static boolean moreInformation = true;
	
	private final int ySize;
	
	public SideContainer(int x, int y, int yS) {
		super(x, y, 400, yS);
		ySize = yS;
		groupButt = new Button[10];
		for (int i = 0; i < groupButt.length; i++) {
			groupButt[i] = new GroupButton(1, -100){
				@Override
				protected void isClicked() {
					updateGrouping(!group);
				}
			};
			groupButt[i].setText(SideDisplay.names[i]);
			addInContainer(groupButt[i]);
		}
	}
	
	public void updateButtons(SideDisplay[] d){
		if(scr != null)removeIntern(scr);
		if(buttons != null)
		for (int i = 0; i < buttons.length; i++) {
			removeIntern(buttons[i]);
		}
		sd = d;
		scr = null;
		buttons = new Button[sd.length];
		for (int i = 0; i < sd.length; i++) {
			buttons[i] = new ClickButton(1, 0, d[i]);
			addInContainer(buttons[i]);
		}
		updateGrouping(group);
		scr = new ScrollBar(251, 0, ySize-20,10,50);
		addInContainer(scr);
		scr.setScrollBy(4);
	}
	
	public void updateGrouping(boolean g){
		group = g;
		for (int i = 0; i < groupButt.length; i++) {
			groupButt[i].setyPos(-100);
		}
		if(g){
			doGroup();
		}else{
			dontGrup();
		}
		lastScr = 0;
	}
	
	private void dontGrup(){
		groupButt[0].setyPos(0);
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setyPos(i*32+16);
		}
	}
	
	private void doGroup(){
		int z = 0;
		for (int i = 0; i <= 9; i++) {
			boolean has = false;
			for (int j = 0; j < buttons.length; j++) {
				if(sd[j].status >= i*1000 && sd[j].status <= i*1000+999){
					//Found for grouping
					if(!has){
						has = true;
						groupButt[i].setyPos(z);
						z+=16;
					}
					buttons[j].setyPos(z);
					z+=32;
				}
			}
		}
	}
	
	@Override
	public void update() {
		int z = 0;
		if(scr != null)
			z = scr.getScrolled()*8;
		
		if(z != lastScr){
			z-=lastScr;
			for (int i = 0; i < buttons.length; i++) {
				buttons[i].setyPos(buttons[i].getyPos()-z);
			}
			for (int i = 0; i < groupButt.length; i++) {
				groupButt[i].setyPos(groupButt[i].getyPos()-z);
			}
			lastScr += z;
		}
		
		super.update();
	}
	
	@Override
	public void paintIntern(Graphics g) {
		
	}

}

class ClickButton extends Button{

	private SideDisplay sd;
	private BufferedImage overpaint;
	private int lastIma;
	
	public ClickButton(int x, int y, SideDisplay d) {
		super(x, y, "res/ima/cli/spb/siw/EMPTY");
		sd = d;
		lastIma = -1;
	}
	
	@Override
	public void paintYou(Graphics g) {
		super.paintYou(g);
		if(!isVisible())return;
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setFont(main.Fonts.fontBold14);
		g2d.setColor(Color.white);
		if(SideContainer.moreInformation && sd.secondLine != null){
			g2d.drawString(sd.mainString, xPos+32, yPos+15);
			g2d.setFont(main.Fonts.fontSans12);
			g2d.setColor(Color.white);
			g2d.drawString(sd.secondLine, xPos+32, yPos+28);
		}else{
			g2d.drawString(sd.mainString, xPos+32, yPos+21);
		}
		
		if(lastIma != sd.status){
			lastIma = sd.status;
			overpaint = SideDisplay.getImage(lastIma);
		}
		g.drawImage(overpaint, xPos+2, yPos+2, null);
	}
	
	@Override
	public void longTermUpdate() {
		super.longTermUpdate();
		setSubtext(sd.mainString+": "+SideDisplay.getStatusText(sd.status));
	}

	@Override
	protected void isClicked() {
		
	}
	@Override
	protected void isFocused() {}
	@Override
	protected void uppdate() {}
}

abstract class GroupButton extends Button{

	public GroupButton(int x, int y) {
		super(x, y, "res/ima/cli/spb/siw/GR");
		setBig(false);
		setBold(false);
		setTextColor(Color.gray);
		setSubtext("Toggle Grouping");
	}

	@Override
	protected void isFocused() {}
	@Override
	protected void uppdate() {}
	
}
