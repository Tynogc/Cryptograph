package gui.chat;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import network.Writable;
import main.PicLoader;
import menu.Button;
import menu.DataFiled;
import menu.MoveMenu;

public class ChatSite extends MoveMenu{
	
	private Color lastColor; 
	
	private Button[] buttons;
	private DataFiled[] data;
	
	private String[] strings;

	public ChatSite(int x, int y, String t) {
		super(x, y, new BufferedImage(800,600,BufferedImage.TYPE_INT_ARGB), t);
		ima.getGraphics().drawImage(PicLoader.pic.getImage("res/ima/mbe/m800x600.png"), 0, 0, null);
		lastColor = Color.black;
		
		data = new DataFiled[100];
		buttons = new Button[100];
		strings = new String[100];
		for (int i = 0; i < strings.length; i++) {
			strings[i] = null;
		}
	}

	@Override
	protected void paintSecond(Graphics g) {
		
	}

	@Override
	protected boolean close() {
		return true;
	}

	@Override
	protected void uppdateIntern() {
		
	}
	
	public void command(String s){
		debug.Debug.println(s);
		try {
			if(s.length()>1)
				cmd(s);
		} catch (Exception e) {
			debug.Debug.println("Can't process... "+s, debug.Debug.WARN);
			debug.Debug.println(e.toString(), debug.Debug.SUBWARN);
			e.printStackTrace();
		}
	}
	
	private void cmd(String s){
		Graphics g = ima.getGraphics();
		g.setColor(lastColor);
		
		String[] st = s.split("\\(");
		st[1] = st[1].split("\\)")[0];
		String[] ar = st[1].split(",");
		for (int i = 0; i < ar.length; i++) {
			if(ar[i].startsWith(" "))
				ar[i] = ar[i].substring(1);
			if(ar[i].endsWith(" "))
				ar[i] = ar[i].substring(0, ar[i].length()-1);
		}
		st = st[0].split(" ");
		
		if(st[0].compareToIgnoreCase("line") == 0){
			g.drawLine(Integer.parseInt(ar[0]), Integer.parseInt(ar[1]),
					Integer.parseInt(ar[2]), Integer.parseInt(ar[3]));
		}
		if(st[0].compareToIgnoreCase("color") == 0){
			lastColor = new Color(Integer.parseInt(ar[0]), Integer.parseInt(ar[1]),
					Integer.parseInt(ar[2]));
		}
		if(st[0].compareToIgnoreCase("rect") == 0){
			g.drawRect(Integer.parseInt(ar[0]), Integer.parseInt(ar[1]),
					Integer.parseInt(ar[2]), Integer.parseInt(ar[3]));
		}
		if(st[0].compareToIgnoreCase("fillrect") == 0){
			g.fillRect(Integer.parseInt(ar[0]), Integer.parseInt(ar[1]),
					Integer.parseInt(ar[2]), Integer.parseInt(ar[3]));
		}
		if(st[0].compareToIgnoreCase("image") == 0){
			ImageIcon i = new ImageIcon(ar[0]);
			g.drawImage(i.getImage(), Integer.parseInt(ar[1]),
					Integer.parseInt(ar[2]), null);
		}
		
		if(st[0].compareToIgnoreCase("datafield") == 0){
			if(st[1].contains(".")){
				int p = Integer.parseInt(st[1].split("\\.")[0]);
				dataFieldAction(data[p], st[1].split("\\.")[1], ar);
				return;
			}
			int p = Integer.parseInt(st[1]);
			if(data[p] != null)
				remove(data[p]);
			
			data[p] = generateDatafield(ar);
			add(data[p]);
		}
		
		if(st[0].compareToIgnoreCase("button") == 0){
			if(st[1].contains(".")){
				int p = Integer.parseInt(st[1].split("\\.")[0]);
				buttonAction(buttons[p], st[1].split("\\.")[1], ar);
				return;
			}
			
			int p = Integer.parseInt(st[1]);
			if(buttons[p] != null)
				remove(buttons[p]);
			
			buttons[p] = generateButton(ar);
			add(buttons[p]);
		}
	}
	
	private DataFiled generateDatafield(String[] ar){
		final int pos;
		if(ar[4].startsWith("$"))
			pos = Integer.parseInt(ar[4]);
		else
			pos = -1;
		
		if(pos>= strings.length)
			throw new ArrayIndexOutOfBoundsException("String index out of Range!");
		DataFiled d = new DataFiled(Integer.parseInt(ar[0]), Integer.parseInt(ar[1]),
				Integer.parseInt(ar[2]), Integer.parseInt(ar[3]), lastColor) {
			@Override
			protected void uppdate() {}
			@Override
			protected void isClicked() {}
			@Override
			public void longTermUpdate() {
				if(pos>= 0)
					setText(strings[pos]);
				super.longTermUpdate();
			}
		};
		if(pos< 0)
			d.setText(ar[4]);
		return d;
	}
	
	private Button generateButton(String[] ar){
		final String send;
		if(ar.length>=5)
			send = ar[4];
		else
			send = null;
		Button d = new Button(Integer.parseInt(ar[0]), Integer.parseInt(ar[1]), ar[2]) {
			@Override
			protected void uppdate() {}
			@Override
			protected void isClicked() {
				if(send != null){
					debug.Debug.println("Send: "+send, debug.Debug.SUBCOM);
					//TODO send
				}
			}
			@Override
			protected void isFocused() {}
		};
		d.setText(ar[3]);
		d.setTextColor(lastColor);
		return d;
	}
	
	private void dataFieldAction(DataFiled d, String action, String[] ar){
		if(action.compareToIgnoreCase("color") == 0){
			d.setTextColor(new Color(Integer.parseInt(ar[0]), Integer.parseInt(ar[1]), Integer.parseInt(ar[2])));
		}
		if(action.compareToIgnoreCase("blink") == 0){
			d.setBlinking(ar[0].startsWith("t") || ar[0].startsWith("T"));
		}
	}
	
	private void buttonAction(Button d, String action, String[] ar){
		
	}

}
