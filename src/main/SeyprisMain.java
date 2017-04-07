package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import debug.DebugFrame;
import gui.PicturSystem;

public class SeyprisMain extends JPanel{

	private static final long serialVersionUID = 1890361404691564161L;
	private static int xPos = 1500;
	private static int yPos = 700;
	
	private KeyListener key;
	private MouseListener mouse;
	
	private static JFrame frame;
	private BufferStrategy strategy;
	
	private DebugFrame debFrame;
	
	private GuiControle gui;
	
	public SeyprisMain(){
		
		debFrame = new DebugFrame();
		StartUp st = new StartUp(debFrame);
		st.doStartUp();
		
		new PicLoader();
		
		frame = new JFrame();
		frame.setBounds(10, 10, sizeX(), sizeY());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setUndecorated(true);
		frame.add(this);
		setVisible(true);
		frame.setVisible(true);
		
		setFocusable(true);
		frame.setFocusable(false);
		key = new KeyListener();
		frame.addKeyListener(key);
		mouse = new MouseListener();
		frame.addMouseListener(mouse);
		frame.addMouseMotionListener(mouse);
		frame.addMouseWheelListener(mouse);
		
		gui = new GuiControle(mouse, key);
		
		//Set Menus
		if(st.startPicCrypto){
			PicturSystem picSy = new PicturSystem(20, 20);//TODO coordinats
			picSy.setPassword(st.picPW);
			GuiControle.addMenu(picSy);
		}
		
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		frame.createBufferStrategy(3);
		strategy = frame.getBufferStrategy();
		
		final MainThread mainThr = new MainThread(this);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mainThr.start();
			}
		});
	}
	
	public void loop(int fps, int sleep, int secFPS, int thirFPS){
		Graphics2D g = null;
		try {
			g = (Graphics2D)strategy.getDrawGraphics();
		} catch (Exception e) {
			debug.Debug.println(e.toString(), debug.Debug.ERROR);
			return;
		}
		
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		g.setColor(Color.black);
		g.fillRect(0, 0, xPos, yPos);
		g.setColor(Color.gray);
		g.drawRect(0, 0, xPos-1, yPos-1);
		
		gui.loop();
		mouse.leftClicked = false;
		mouse.rightClicked = false;
		gui.paint(g);
		
		if(mouse.mouseDraggStartX>0){
			if(mouse.mouseDraggStartY > 0 && mouse.mouseDraggStartY < 30 &&
					mouse.mouseDraggStartX<xPos-30)
			frame.setLocation(-mouse.mouseDraggStartX+mouse.mouseDraggX, -mouse.mouseDraggStartY+mouse.mouseDraggY);
		}
		
		g.dispose();
		strategy.show();
	}
	
	public static int sizeX(){
		return xPos;
	}
	
	public static int sizeY(){
		return yPos;
	}
	
	public static JFrame getFrame(){
		return frame;
	}
}
