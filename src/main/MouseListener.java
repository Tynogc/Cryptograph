package main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import cryptoUtility.Random;

public class MouseListener implements java.awt.event.MouseListener, MouseMotionListener, MouseWheelListener{

	public boolean leftClicked;
	public boolean rightClicked;
	public boolean left;
	public boolean right;
	
	public int x;
	public int y;
	
	public int mouseDraggX = 0;
	public int mouseDraggY = 0;
	public int mouseDraggStartX = 0;
	public int mouseDraggStartY = 0;
	
	public int rot;
	
	@Override
	public void mouseDragged(MouseEvent e) {
		x = e.getX();
		y = e.getY();
		
		mouseDraggX = e.getXOnScreen();
		mouseDraggY = e.getYOnScreen();
		
		EventCounter.event();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		x = e.getX();
		y = e.getY();
		
		Random.enterEntropy(x*y*(int)System.currentTimeMillis());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1){
			left = true;
			leftClicked = true;
		}
		if(e.getButton() == MouseEvent.BUTTON3){
			right = true;
			rightClicked = true;
		}
		mouseDraggStartX = e.getX();
		mouseDraggStartY = e.getY();
		mouseDraggX = e.getXOnScreen();
		mouseDraggY = e.getYOnScreen();
		
		EventCounter.event();
		
		Random.enterEntropy(x*y*(int)System.currentTimeMillis()*13);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1){
			left = false;
		}
		if(e.getButton() == MouseEvent.BUTTON3){
			right = false;
		}
		mouseDraggStartX = 0;
		mouseDraggStartY = 0;
		
		EventCounter.event();
		
		Random.enterEntropy(x*y*(int)System.currentTimeMillis()*17);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		rot+=e.getWheelRotation();
		EventCounter.event();
	}

}
