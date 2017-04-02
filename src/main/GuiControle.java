package main;

import java.awt.Graphics;

import menu.AbstractMenu;
import menu.MenuControle;

public class GuiControle {

	private main.MouseListener mouse;
	
	private MenuControle debugMenu;
	private MenuControle topMenu;
	private static MenuControle[] menus;
	
	public GuiControle(main.MouseListener m, main.KeyListener k){
		mouse = m;
		topMenu = new MenuControle();
		debugMenu = new MenuControle();
		menus = new MenuControle[10];
		for (int i = 0; i < menus.length; i++) {
			menus[i] = new MenuControle();
		}
	}
	
	public boolean loop(){
		boolean left = mouse.left||mouse.leftClicked;
		boolean right = mouse.right || mouse.rightClicked;
		boolean clicked = false;
		
		for (int i = menus.length-1; i >= 0; i--) {
			if(menus[i].mouseState(mouse.x, mouse.y, left, right)){
				left = false;
				right = false;
				clicked = true;
			}
		}
		
		if(debugMenu.mouseState(mouse.x, mouse.y, left, right)){
			left = false;
			right = false;
			clicked = true;
		}
		
		if(topMenu.mouseState(mouse.x, mouse.y, left, right)){
			left = false;
			right = false;
			clicked = true;
		}
		
		return clicked;
	}
	
	public void paint(Graphics g){
		topMenu.paintYou(g);
		debugMenu.paintYou(g);
		for (int i = 0; i < menus.length; i++) {
			menus[i].paintYou(g);
		}
	}
	
	public static boolean addMenu(AbstractMenu  m){
		//shutdown old menus
		for (int i = menus.length-1; i >= 0; i--) {
			if(!menus[i].isActiv()){
				MenuControle mc = menus[i];
				for (int j = i; j < menus.length-1; j++) {
					menus[j] = menus[j+1];
				}
				menus[menus.length-1] = mc;
			}
		}
		
		for (int i = 0; i < menus.length; i++) {
			if(!menus[i].isActiv()){
				menus[i].setActivMenu(m);
				return true;
			}
		}
		return false;
	}
	
	public void setdebugMenu(AbstractMenu m){
		debugMenu.setActivMenu(m);
	}
}
