package menu;

import java.awt.Color;
import java.awt.Graphics;

import main.KeyListener;

public abstract class TextEnterButton extends DataFiled{
	
	private KeyListener key;
	private boolean activ = false;
	private boolean pwMode = false;

	public TextEnterButton(int x, int y, int wi, int hi, Color c, KeyListener k) {
		super(x, y, wi, hi, c);
		key = k;
	}

	@Override
	protected void isClicked() {
		key.deletInput();
		if(pwMode){
			setText("");
			key.setText("");
		}else{
			key.setText(text);
		}
	}

	@Override
	protected void uppdate() {
		if(wasLastClicked()){
			setText(key.getKeyChain());
			activ = true;
			if(key.isEnter()){
				if(!pwMode)
				debug.Debug.println("* Entered text: "+text, debug.Debug.COM);
				lastClicked = false;
				key.deletInput();
				textEntered(text);
				activ = false;
			}
		}else if(activ){
			if(!pwMode)
			debug.Debug.println("* Entered text: "+text, debug.Debug.COM);
			lastClicked = false;
			key.deletInput();
			textEntered(text);
			activ = false;
		}
	}
	
	@Override
	public void paintYou(Graphics g) {
		if(text == null)
			text = "";
		
		String textToPrint = new String(text);
		
		if(pwMode){
			text = "";
			for (int i = 0; i < textToPrint.length(); i++) {
				text+="*";
			}
		}
		
		if(!wasLastClicked()){
			super.paintYou(g);
			return;
		}
		if((System.currentTimeMillis()/500)%2==0)text +=" ";
		else text +="|";
		super.paintYou(g);
		text = textToPrint;
	}

	protected abstract void textEntered(String text);
	
	public void setPwMode(boolean pwMode) {
		this.pwMode = pwMode;
		setText("");
	}

}
