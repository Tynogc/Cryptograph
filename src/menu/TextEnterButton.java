package menu;

import java.awt.Color;
import java.awt.Graphics;

import main.KeyListener;

public abstract class TextEnterButton extends DataFiled{
	
	private KeyListener key;
	private boolean activ = false;

	public TextEnterButton(int x, int y, int wi, int hi, Color c, KeyListener k) {
		super(x, y, wi, hi, c);
		key = k;
	}

	@Override
	protected void isClicked() {
		key.deletInput();
		key.setText(text);
	}

	@Override
	protected void uppdate() {
		if(wasLastClicked()){
			setText(key.getKeyChain());
			activ = true;
			if(key.isEnter()){
				debug.Debug.println("* Entered text: "+text, debug.Debug.COM);
				lastClicked = false;
				key.deletInput();
				textEntered(text);
				activ = false;
			}
		}else if(activ){
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
		if(!wasLastClicked()){
			super.paintYou(g);
			return;
		}
		if((System.currentTimeMillis()/500)%2==0)text +=" ";
		else text +="|";
		super.paintYou(g);
		if(text.length()>1)
			text = text.substring(0, text.length()-1);
		else
			text = "";
	}

	protected abstract void textEntered(String text);

}
