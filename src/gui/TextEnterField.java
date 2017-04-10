package gui;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JTextArea;

import main.MouseListener;

public class TextEnterField extends JTextArea{

	private static final long serialVersionUID = 2515312573501882552L;
	
	private final MouseListener mouse;
	private final JTextArea me;
	
	private BufferedImage ima;
	
	private static final int xSize = 300;
	private static final int ySize = 100;
	
	public TextEnterField(MouseListener m){
		mouse = m;
		me = this;
		
		setLineWrap(true);
		setWrapStyleWord(true);
		setBounds(0, 0, xSize, ySize);
		setFocusable(true);
		
		ima = new BufferedImage(xSize, ySize, BufferedImage.TYPE_INT_ARGB);
		
		addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
			@SuppressWarnings("deprecation")
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_ENTER){
					if(e.isControlDown()){
						e.setModifiers(0);
					}else{
						System.out.println(getText()+" OK"); //TODO senden
						e.consume();
						setText("");
					}
				}
			}
		});
		addMouseListener(new java.awt.event.MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON1)
					mouse.left = false;
				if(e.getButton() == MouseEvent.BUTTON3)
					mouse.right = false;
			}
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON1)
					mouse.left = true;
				if(e.getButton() == MouseEvent.BUTTON3)
					mouse.right = true;
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON1)
					mouse.leftClicked = true;
				if(e.getButton() == MouseEvent.BUTTON3)
					mouse.rightClicked = true;
			}
		});
		addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				mouse.x = e.getX()+me.getX();
				mouse.y = e.getY()+me.getY();
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				mouse.x = e.getX()+me.getX();
				mouse.y = e.getY()+me.getY();
			}
		});
	}
	
	public void paintTheImage(Graphics g){
		if(isVisible())
			super.paintAll(g);
		else
			g.drawImage(ima, 0, 0, null);
	}
	
	@Override
	public void paint(Graphics g) {
		if(isVisible())
			super.paint(ima.getGraphics());
		g.drawImage(ima, 0, 0, null);
	}
	
	
}
