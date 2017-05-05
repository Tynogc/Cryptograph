package debug;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import main.SetPassword;

public class DebugFrame extends JFrame{
	
	private DebPanel panel;
	
	private String input = "";
	
	private int a = 0;
	private int b = 10;
	
	private byte canState;
	private boolean checkState;
	private boolean pwState;
	private boolean numState;
	private String pwString = "";
	private SetPassword toSet;
	
	public static int dfl = -280;
	
	private String[] omtc;
	private int omtcPos = -1;
	
	private static DebugFrame dbf;
	
	public DebugFrame(){
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		dfl = dim.height-(Debug.sizeY+100);
		if(dfl>0)dfl = 0;
		
		setBounds(100,0,Debug.sizeX+110,Debug.sizeY+80+dfl);
		panel = new DebPanel(this);
		add(panel);
		Debug.panel = panel;
		setVisible(true);
		panel.setFocusable(false);
		setFocusable(true);
		canState = 0;
		checkState = false;
		pwState = false;
		
		dbf = this;
		
		omtc = new String[10];
		for (int i = 0; i < omtc.length; i++) {
			omtc[i] = "";
		}
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				if(arg0.getButton() == MouseEvent.BUTTON1){
					Debug.knowMemory();
					Debug.displayMemory(Debug.COM);
				}
				else{
					Debug.remove(3);
					Debug.print(b+"%", Debug.SUBWARN);
					b++;
				}
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
			}
		});
		addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {
				char c = arg0.getKeyChar();
				if(checkState){
					if(c == '\n') canState = 3;
					if(c == 'y') canState = 1;
					if(c == 'n') canState = 2;
					
					return;
				}else if(pwState){
					if(c == '\n'){
						canState = 1;
						if(toSet!=null){
							toSet.setPassword(pwString);
						}
						pwString = "";
						input = "";
						pwState = false;
					}else if((int)arg0.getKeyChar() == 8){
						if(pwString.length()>=1)pwString=pwString.substring(0, pwString.length()-1);
						if(input.length()>=1)input = input.substring(0, input.length()-1);
					}else{
						pwString += c;
						input += "*";
					}
					panel.input = input;
					panel.paint(panel.getGraphics());
					
					return;
				}else if(numState){
					if(c == '\n')
						canState = 90;
					if(c>='1' && c<='9'){
						canState = (byte)(c-'1'+1);
					}
					if(c == '0'){
						canState = 91;
					}
					return;
				}
				omtcPos = -1;
				if(c == '\n'){
					addOmtcString(input);
					Debug.println(input, Debug.PRICOM);
					boolean b = false;
					if(input.length()>= 2)
						if(input.substring(0, 2).compareTo("//")==0)
							b = true;
					if(b){
						//ComunicationControl.com.enterDebugLink(input.substring(2));
					}else{
						DebugComand.operateComand(input, dbf);
					}
					
					input = "";
				}else if((int)arg0.getKeyChar() == 8){
					if(input.length()>=1)
					input = input.substring(0, input.length()-1);
				}else{
					input += c;
				}
				
				panel.input = input;
				panel.paint(panel.getGraphics());
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode() == KeyEvent.VK_UP){
					omtcPos++;
					if(omtcPos>=omtc.length)omtcPos = omtc.length-1;
					input = omtc[omtcPos];
					panel.input = input;
					panel.paint(panel.getGraphics());
				}
				if(arg0.getKeyCode() == KeyEvent.VK_DOWN){
					omtcPos--;
					if(omtcPos<0)omtcPos = 0;
					input = omtc[omtcPos];
					panel.input = input;
					panel.paint(panel.getGraphics());
				}
			}
		});
		setVisible(true);
		
		paint(getGraphics());
	}
	
	public void setCheckState(boolean state){
		checkState = state;
		pwState = false;
		numState = false;
		canState = 0;
	}
	
	public void setPwState(boolean state, SetPassword p){
		toSet = p;
		checkState = false;
		pwState = state;
		numState = false;
		pwString = "";
		canState = 0;
	}
	
	public void setNumState(boolean state){
		checkState = false;
		pwState = false;
		numState = state;
		canState = 0;
	}
	
	public byte canState(){
		return canState;
	}
	
	private void addOmtcString(String s){
		for (int i = omtc.length-2; i >= 0; i--) {
			omtc[i+1]=omtc[i];
		}
		omtc[0] = s.substring(0);
	}
	
	public static void waitForEnter(String couse){
		if(dbf == null)
			return;
		Debug.println(couse+" [ENTER]");
		DebugComand.questionEnter(dbf);
		Debug.println("* ENTER", Debug.PRICOM);
	}
	
	public static boolean Question(String couse, boolean onEnter){
		if(dbf == null)
			return onEnter;
		
		if(onEnter) Debug.println(couse+" [Y/n]");
		else Debug.println(couse+" [y/N]");
		return DebugComand.question(onEnter, dbf);
	}

}
