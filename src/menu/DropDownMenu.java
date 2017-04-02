package menu;

import java.awt.Color;
import java.awt.Graphics;

public abstract class DropDownMenu extends Button{

	private ButtonInterface subButton;
	private DropDownButton[] ddp;
	private int activButton;
	
	private static final int numberOfButtons = 10;
	
	private DropDownAction dda;
	
	private boolean isDroped = false;
	
	private int virtualXSize;
	
	public DropDownMenu(int x, int y, int size) {
		super(x+size, y, "res/ima/cli/scr/1d");
		
		dda = new DropDownAction() {
			@Override
			public void clicked(int i) {
				handleClick(i);
			}
		};
		
		virtualXSize = size;
		activButton = 0;
		
		ddp = new DropDownButton[numberOfButtons];
		subButton = new EndButtonList();
	}
	
	public void addSubButton(DropDownButton b, int currentNumber){
		if(currentNumber >= numberOfButtons){
			debug.Debug.println("* To many Buttons in DropDown!", debug.Debug.ERROR);
			return;
		}
		
		ddp[currentNumber] = b;
		b.dda = dda;
		b.i = currentNumber;
		
		currentNumber++;
		
		subButton = subButton.add(b);
		
		if(currentNumber != activButton)
			b.setVisible(false);
		b.setxPos(xPos-virtualXSize);
		b.setyPos(yPos);
		
		retread();
	}
	
	@Override
	public void leftClicked(int x, int y) {
		subButton.leftClicked(x, y);
		super.leftClicked(x, y);
	}
	
	@Override
	public void leftReleased(int x, int y) {
		subButton.leftReleased(x, y);
		super.leftReleased(x, y);
	}
	
	@Override
	public void paintYou(Graphics g) {
		subButton.paintYou(g);
		super.paintYou(g);
	}
	
	@Override
	public void checkMouse(int x, int y) {
		subButton.checkMouse(x, y);
		super.checkMouse(x, y);
	}

	@Override
	protected void isClicked() {
		handleClick(activButton);
	}

	@Override
	protected void isFocused() {
		
	}

	@Override
	protected void uppdate() {
		if(!isDroped)
			return;
		if(!wasLastClicked()){
			if(ddp[activButton] != null){
				if(!ddp[activButton].wasLastClicked()){
					retread();
				}
			}
		}
	}
	
	protected abstract void changed(int i);
	
	private void handleClick(int i){
		if(isDroped){
			activButton = i;
			retread();
			changed(i);
		}else{
			if(i != activButton)
				debug.Debug.println("*Strange behaviour of DropDownMenu: clicked by non activ Button", debug.Debug.WARN);
			
			int g = yPos+25;
			for (int j = 0; j < ddp.length; j++) {
				if(ddp[j]==null)
					continue;
				if(j == activButton){
					ddp[j].setyPos(yPos);
				}else{
					ddp[j].setyPos(g);
					g+=20;
				}
				ddp[j].setVisible(true);
			}
			isDroped = true;
		}
	}
	
	private void retread(){
		for (int i = 0; i < ddp.length; i++) {
			if(ddp[i]==null)
				continue;
			if(i == activButton){
				ddp[i].setyPos(yPos);
			}
			ddp[i].setVisible(i == activButton);
		}
		isDroped = false;
	}
	
	public void setCurrentlyActiv(int i){
		activButton = i;
		retread();
	}
	
	public int getState(){
		return activButton;
	}
	
	@Override
	public void setxPos(int xPos) {
		for (int i = 0; i < ddp.length; i++) {
			if(ddp[i] != null)
				ddp[i].setxPos(xPos);
		}
		super.setxPos(xPos+virtualXSize);
	}
	
	@Override
	public void setyPos(int yPos) {
		for (int i = 0; i < ddp.length; i++) {
			if(ddp[i] != null)
				ddp[i].setyPos(yPos);
		}
		super.setyPos(yPos);
	}
	
	@Override
	public void setDisabled(boolean state) {
		for (int i = 0; i < ddp.length; i++) {
			if(ddp[i]!=null)
				ddp[i].setDisabled(state);
		}
		super.setDisabled(state);
	}

}

interface DropDownAction{
	public void clicked(int i);
}
