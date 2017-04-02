package main;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import debug.DebugFrame;

public class SeyprisMain extends JPanel{

	private static final long serialVersionUID = 1890361404691564161L;
	private static int xPos = 400;
	private static int yPos = 500;
	
	private KeyListener key;
	private MouseListener mouse;
	
	private JFrame frame;
	private BufferStrategy strategy;
	
	private DebugFrame debFrame;
	
	public SeyprisMain(){
		
		frame = new JFrame();
		frame.setBounds(10, 10, sizeX(), sizeY());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.add(this);
		setVisible(true);
		frame.setVisible(true);
		
		setFocusable(true);
		frame.setFocusable(false);
		key = new KeyListener();
		frame.addKeyListener(key);
		
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
	
	public void loop(int fps, int secFps, int thiFps, int triFps){
		
		Graphics2D g = null;
		try {
			g = (Graphics2D)strategy.getDrawGraphics();
		} catch (Exception e) {
			debug.Debug.println(e.toString(), debug.Debug.ERROR);
			return;
		}
		
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		
	}
	
	public static int sizeX(){
		return xPos;
	}
	
	public static int sizeY(){
		return yPos;
	}
}
