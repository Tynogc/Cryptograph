package menu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.security.auth.Destroyable;

import main.KeyListener;
import main.SetPassword;

public abstract class TextEnterButton extends DataFiled implements Destroyable{
	
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
				textEnteredDirectly(text);
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
	public void paintYou(Graphics2D g) {
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
			text = textToPrint;
			return;
		}
		if((System.currentTimeMillis()/500)%2==0)text +=" ";
		else text +="|";
		super.paintYou(g);
		text = textToPrint;
	}
	
	/**
	 * Is called when a new Text is Entered, either by loosing focus, or ENTER on the Keyboard is pressed
	 * @param text the text of the Field
	 */
	protected abstract void textEntered(String text);
	
	/**
	 * Is called only if ENTER on the Keyboard is pressed. This is more useful for overlay-menus, so they can close
	 * after the text is entered.
	 * @param text
	 */
	protected void textEnteredDirectly(String text){}
	
	public void setPwMode(boolean pwMode) {
		this.pwMode = pwMode;
		setText("");
	}
	
	public void destroy(){
		setText("");
		text = "";
		if(activ)
			key.deletInput();
	}

}
