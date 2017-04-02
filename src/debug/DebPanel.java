package debug;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class DebPanel extends JPanel{
	
	public String input;
	private DebugFrame frame;
	
	public DebPanel(DebugFrame f){
		setBounds(0, 0, Debug.sizeX, Debug.sizeY+30);
		input = "";
		frame = f;
	}
	
	public void paint(Graphics g){
		int dfl = DebugFrame.dfl;
		g.drawImage(Debug.i1, 0, Debug.sizeY-Debug.line*10+dfl, null);
		g.drawImage(Debug.i2, 0, -Debug.line*10+dfl, null);
		g.drawImage(Debug.i2, 0, Debug.sizeY*2-Debug.line*10+dfl, null);
		g.setColor(Color.black);
		g.fillRect(0, Debug.sizeY+10+dfl, Debug.sizeX, 20);
		g.setColor(Color.gray);
		g.drawRect(0, Debug.sizeY+10+dfl, Debug.sizeX, 20);
		g.setColor(Color.white);
		g.drawString(input, 0, Debug.sizeY+20+dfl);
	}
	
	public void setVisible(boolean b){
		frame.setVisible(b);
	}
	
	public JFrame getFrame(){
		return frame;
	}

}
