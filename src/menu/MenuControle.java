package menu;

import java.awt.Graphics;

public class MenuControle {
	
	private AbstractMenu menus;
	
	private boolean lef;
	private boolean rig;
	
	private long lastLongTermUpdate;
	
	private int xm;
	private int ym;
	private boolean moving;
	
	public MenuControle(){
		lastLongTermUpdate = System.currentTimeMillis()/1000+(int)(Math.random()*1000);
	}
	
	public void paintYou(Graphics g){
		if(menus != null){
			menus.paintYou(g);
		}
	}
	
	public void setActivMenu(AbstractMenu m){
		menus = m;
		if(menus != null)
		menus.setControle(this);
	}
	
	public boolean isActiv(){
		return menus != null;
	}
	
	public void scrolled(int i){
		if(menus!= null)
			menus.scrolled(i);
	}
	
	public boolean mouseState(int x, int y, boolean left, boolean right){
		if(menus == null)return false;
		
		boolean lPress = !lef && left;
		boolean rPress = !rig && right;
		boolean lReleas = lef && !left;
		lef = left;
		rig = right;
		
		if(!menus.activ){
			lPress = false;
			lReleas = false;
			rPress = false;
		}
		
		boolean inside = false;
		if(x>=menus.xPos && x<= menus.xPos+menus.xSize){
			if(y>=menus.yPos && y<= menus.yPos+menus.ySize){
				inside = true;
				if(menus.moveAble){
					if(y>=menus.yPos && y <= menus.yPos+23 && x<= menus.xPos+menus.xSize-25
							&& lPress && !moving){
						moving = true;
						xm = menus.xPos-x;
						ym = menus.yPos-y;
					}else if(!left){
						moving = false;
					}
				}
			}
		}
		if(moving && left){
			menus.xPos = xm+x;
			menus.yPos = ym+y;
			if(menus.xPos+menus.xSize>main.SeyprisMain.sizeX())
				menus.xPos = main.SeyprisMain.sizeX()-menus.xSize;
			if(menus.yPos+menus.ySize>main.SeyprisMain.sizeY())
				menus.yPos = main.SeyprisMain.sizeY()-menus.ySize;
			if(menus.xPos<0)menus.xPos = 0;
			if(menus.yPos<0)menus.yPos = 0;
		}
		if(lastLongTermUpdate != System.currentTimeMillis()/1000){
			lastLongTermUpdate = System.currentTimeMillis()/1000;
			menus.getActivButtons().longTermUpdate();
			menus.longTermUpdate();
		}
		
		
		x-=menus.xPos;
		y-=menus.yPos;
		menus.updateMenu();
		menus.maousAt(x, y);
		if(rPress)
			menus.reightClick(x, y);
		if(lPress)
			menus.leftClick(x, y);
		menus.getActivButtons().checkMouse(x, y);
		if(lPress && inside)menus.getActivButtons().leftClicked(x, y);
		if(lReleas)menus.getActivButtons().leftReleased(x, y);
		if(menus == null)
			return inside;
		if(menus.closeOutside && lPress && !inside){
			menus = null;
		}
		
		return inside;
	}

}
