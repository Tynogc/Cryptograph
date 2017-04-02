package menu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.PicLoader;

public abstract class AlarmkButton extends Button{

	private boolean alarm;
	private boolean warn;
	
	private BufferedImage stateFocNorm;
	private BufferedImage stateFocWarn;
	private BufferedImage stateFocErr;
	
	private BufferedImage stateNorNorm;
	private BufferedImage stateNorWarn;
	private BufferedImage stateNorErr;
	
	private BufferedImage stateCliNorm;
	private BufferedImage stateCliWarn;
	private BufferedImage stateCliErr;
	
	private BufferedImage[] alarmPics;
	
	private static final Color warnColor = new Color(230,200,5);
	
	private long alarmStart = 0;
	
	public AlarmkButton(int x, int y, String filePath) {
		super(x, y, filePath+"n");
		loadAlp(filePath);
		setAlarm(false);
	}
	
	public void setFilePath(String s){
		super.setFilePath(s);
		loadAlp(s);
	}
	
	private void loadAlp(String s){
		stateFocNorm = PicLoader.pic.getImage(s+"nf.png");
		stateCliNorm = PicLoader.pic.getImage(s+"nc.png");
		stateNorNorm = PicLoader.pic.getImage(s+"nn.png");
		
		stateFocWarn = PicLoader.pic.getImage(s+"wf.png");
		stateCliWarn = PicLoader.pic.getImage(s+"wc.png");
		stateNorWarn = PicLoader.pic.getImage(s+"wn.png");
		
		stateFocErr = PicLoader.pic.getImage(s+"ef.png");
		stateCliErr = PicLoader.pic.getImage(s+"ec.png");
		stateNorErr = PicLoader.pic.getImage(s+"en.png");
		
		alarmPics = new BufferedImage[8];
		for (int i = 0; i < alarmPics.length; i++) {
			alarmPics[i] = PicLoader.pic.getImage(s+"e"+i+".png");
		}
	}
	
	public void setAlarm(boolean a){
		if(a == alarm)return;
		alarm = a;
		
		if(alarm){
			state1 = stateNorErr;
			state2 = stateCliErr;
			stateFoc = stateFocErr;
			alarmStart = System.currentTimeMillis();
			setTextColor(Color.RED);
		}else{
			if(warn){
				state1 = stateNorWarn;
				state2 = stateCliWarn;
				stateFoc = stateFocWarn;
				setTextColor(warnColor);
			}else{
				state1 = stateNorNorm;
				state2 = stateCliNorm;
				stateFoc = stateFocNorm;
				setTextColor(blue);
			}
		}
	}
	
	public void setWarn(boolean w){
		if(w == warn)return;
		warn = w;
		
		if(alarm){
			return;
		}else{
			if(warn){
				state1 = stateNorWarn;
				state2 = stateCliWarn;
				stateFoc = stateFocWarn;
				setTextColor(warnColor);
			}else{
				state1 = stateNorNorm;
				state2 = stateCliNorm;
				stateFoc = stateFocNorm;
				setTextColor(blue);
			}
		}
	}
	
	public void paintYou(Graphics g){
		super.paintYou(g);
		if(alarm){
			int c = (int)(System.currentTimeMillis()-alarmStart);
			if(c<20000){
				c = (c/400+1)%8;
				g.drawImage(alarmPics[c], xPos, yPos, null);
//				g.setColor(Color.red);
//				g.drawString("T"+c, xPos+2, yPos+12);
			}
		}
	}

}
