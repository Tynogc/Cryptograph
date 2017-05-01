package main;

import gui.TopMenu;

import java.awt.Graphics;

import menu.AbstractMenu;
import menu.MenuControle;

public class GuiControle {

	private main.MouseListener mouse;
	
	private MenuControle debugMenu;
	private MenuControle topMenu;
	private static MenuControle[] menus;
	private MenuControle frameMenu;
	private MenuControle textEnter;
	private static MenuControle superMenu;
	
	public final TopMenu menuTopMenu;
	
	public GuiControle(main.MouseListener m, main.KeyListener k){
		mouse = m;
		topMenu = new MenuControle();
		debugMenu = new MenuControle();
		menus = new MenuControle[10];
		for (int i = 0; i < menus.length; i++) {
			menus[i] = new MenuControle();
		}
		frameMenu = new MenuControle();
		textEnter = new MenuControle();
		superMenu = new MenuControle();
		
		menuTopMenu = new gui.TopMenu();
		topMenu.setActivMenu(menuTopMenu);
		frameMenu.setActivMenu(new gui.FrameMenu());
	}
	
	public boolean loop(){
		boolean left = mouse.left||mouse.leftClicked;
		boolean right = mouse.right || mouse.rightClicked;
		boolean clicked = false;
		
		boolean leftForFocus = left;
		
		if(superMenu.isActiv()){
			if(superMenu.mouseState(mouse.x, mouse.y, left, right, !clicked)){
				clicked = true;
			}
			left = false;
			right = false;
		}
		
		if(frameMenu.mouseState(mouse.x, mouse.y, left, right, !clicked)){
			left = false;
			right = false;
			clicked = true;
		}
		if(leftForFocus)
			frameMenu.leftClickForFocus(mouse.x, mouse.y);
		
		for (int i = menus.length-1; i >= 0; i--) {
			if(menus[i].mouseState(mouse.x, mouse.y, left, right, !clicked)){
				left = false;
				right = false;
				clicked = true;
			}
			if(leftForFocus)
				menus[i].leftClickForFocus(mouse.x, mouse.y);
		}
		
		if(textEnter.mouseState(mouse.x, mouse.y, left, right, !clicked)){
			left = false;
			right = false;
			clicked = true;
		}
		if(leftForFocus)
			textEnter.leftClickForFocus(mouse.x, mouse.y);
		
		if(debugMenu.mouseState(mouse.x, mouse.y, left, right, !clicked)){
			left = false;
			right = false;
			clicked = true;
		}
		if(leftForFocus)
			debugMenu.leftClickForFocus(mouse.x, mouse.y);
		
		if(topMenu.mouseState(mouse.x, mouse.y, left, right, !clicked)){
			left = false;
			right = false;
			clicked = true;
		}
		if(leftForFocus)
			topMenu.leftClickForFocus(mouse.x, mouse.y);
		
		return clicked;
	}
	
	public void paint(Graphics g){
		textEnter.paintYou(g);
		topMenu.paintYou(g);
		debugMenu.paintYou(g);
		for (int i = 0; i < menus.length; i++) {
			menus[i].paintYou(g);
		}
		frameMenu.paintYou(g);
		superMenu.paintYou(g);
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
	
	public static void setSuperMenu(AbstractMenu m){
		superMenu.setActivMenu(m);
	}
}
