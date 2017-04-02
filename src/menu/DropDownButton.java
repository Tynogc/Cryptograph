package menu;

import java.awt.Color;

public class DropDownButton extends DataFiled{
	
	public int i = -1;
	public DropDownAction dda;
	
	public DropDownButton(int fx, int fy, String s) {
		super(0,0, fx, fy, Color.black);
		setText(s);
		setTextColor(Color.white);
	}

	@Override
	protected void isClicked() {
		if(dda != null)
			dda.clicked(i);
	}

	@Override
	protected void isFocused() {
		
	}

	@Override
	protected void uppdate() {
		
	}
}
