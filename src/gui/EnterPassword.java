package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Fonts;
import main.KeyListener;
import main.PicLoader;
import main.SetPassword;
import main.SeyprisMain;
import menu.AbstractMenu;
import menu.Button;
import menu.TextEnterButton;

public class EnterPassword extends OverswapMenu{

	private TextEnterButton teb;
	private Button close;
	private Button ok;
	
	private int lastLength;
	private boolean showAnalysis;
	private int analysis;
	
	private boolean repeading;
	
	private static final String[] analysisString = new String[]{
		"Too Short", "Very Weak", "Weak", "Medium", "Strong", "Very Strong", "Extreme", "Ridiculous", "Neeeerd!"
	};
	private static final Color[] analysisColor = new Color[]{
		new Color(122,10,15), new Color(237,22,22), new Color(250,122,22),
		new Color(250,210,30), new Color(91,252,22),  new Color(23,252,180),
		new Color(19,225,252), new Color(218,10,255), new Color(0,120,255)
	};
	private static final char[] list1 = new char[]{'a','b','c','d','e','f','g','h',
		'i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
	private static final char[] list2 = new char[]{'A','B','C','D','E','F','G','H',
		'I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
	private static final char[] list3 = new char[]{'1','2','3','4','5','6','7','8','9','0'};
	
	private SetPassword setPassword;
	
	public EnterPassword(SetPassword spw, KeyListener k, boolean analysis) {
		
		teb = new TextEnterButton(xSize/2-110, 80, 220, 20, Color.black, k) {
			@Override
			protected void textEntered(String text) {
			}
			@Override
			protected void textEnteredDirectly(String text) {
				setPassword.setPW(teb.getText());
				System.out.println(setPassword.getPassword());
				teb.destroy();
				closeIntern();
			}
		};
		add(teb);
		teb.setPwMode(true);
		teb.setTextColor(Color.white);
		
		imas = new BufferedImage[]{
			PicLoader.pic.getImage("res/ima/ote/pw1.png")	
		};
		
		setPassword = spw;
		showAnalysis = analysis;
		
		ok = new Button(xSize/2-80,146,"res/ima/cli/Gsk") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				setPassword.setPW(teb.getText());
				System.out.println(setPassword.getPassword());
				teb.destroy();
				closeIntern();
			}
		};
		add(ok);
		ok.setText("OK");
		
		close = new Button(xSize/2+5,146,"res/ima/cli/Gsk") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				teb.destroy();
				closeIntern();
			}
		};
		add(close);
		close.setText("Cancle");
		
		moveAble = false;
	}

	@Override
	protected void uppdateIntern() {
		super.uppdateIntern();
		if(!showAnalysis)return;
		if(teb.getText().length()!=lastLength){
			String s = teb.getText();
			lastLength = s.length();
			analysis = 0;
			
			if(lastLength<6)return;
			
			int sl = searchLists(s);
			analysis+=sl;
			
			if(lastLength<9&&analysis>=2)analysis = 1;
			if(lastLength>12)analysis++;
			if(lastLength>20)analysis++;
			
			if(lastLength>40 && sl>=4)analysis++;
			if(lastLength>80 && sl>=4)analysis++;
			
			repeading = false;
			for (int i = 1; i <= s.length()/2; i++) {
				if(check4Repeading(s, i)){
					repeading = true;
					break;
				}
			}
			if(repeading){
				if(analysis>2)
					analysis--;
				if(analysis>1)
					analysis--;
				if(analysis>3){
					analysis = 3;
				}
			}
			
			s = "";
			s = null;
		}
	}

	@Override
	protected void paintIntern(Graphics g) {
		super.paintIntern(g);
		if(!showAnalysis)return;
		int rop = xSize/2-100;
		g.setColor(Color.white);
		g.setFont(Fonts.fontBold18);
		g.drawString("Enter Password:", rop, 60);
		g.setFont(Fonts.fontSans14);
		g.drawString("Password Strength: ", rop, 120);
		g.setColor(analysisColor[analysis]);
		if(repeading)
			g.drawString(analysisString[analysis]+" (Repetition)", rop+70, 140);
		else
			g.drawString(analysisString[analysis], rop+70, 140);
		for (int i = 0; i <= analysis; i++) {
			g.drawLine(rop+i*25, 122, rop+25+i*25, 122);
			g.drawLine(rop+i*25+1, 123, rop+26+i*25, 123);
			g.drawLine(rop+i*25, 124, rop+25+i*25, 124);
		}
	}
	
	private int searchLists(String s){
		boolean l1 = false;
		boolean l2 = false;
		boolean l3 = false;
		boolean lz = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			boolean b = false;
			for (int j = 0; j < list1.length; j++) {
				if(c == list1[j]){
					l1 = true;
					b = true;
				}
			}
			for (int j = 0; j < list2.length; j++) {
				if(c == list2[j]){
					l2 = true;
					b = true;
				}
			}
			for (int j = 0; j < list3.length; j++) {
				if(c == list3[j]){
					l3 = true;
					b = true;
				}
			}
			if(!b)
				lz = true;
		}
		s = "";
		s = null;
		int i = 0;
		if(l1)i++;
		if(l3)i++;
		if(l2)i++;
		if(lz)i++;
		
		return i;
	}
	
	private boolean check4Repeading(String s, int r){
		if(s.length() < r*2) return false;
		for (int i = 0; i <= s.length()/2-r; i++) {
			String st = s.substring(i, i+r);
			if(crossearch(s, st, i))return true;
		}
		return false;
	}
	
	private boolean crossearch(String s, String st, int start){
		int u = 0;
		boolean loop = false;
		for (int i = start; i < s.length(); i++) {
			if(s.charAt(i)!=st.charAt(u))return false;
			u++;
			if(u>=st.length()){
				u = 0;
				loop = true;
			}
		}
		//if(loop) System.out.println(s+" "+st);
		return loop;
	}

}
