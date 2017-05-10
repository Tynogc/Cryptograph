package gui.start;

import java.awt.image.BufferedImage;

import main.PicLoader;
import gui.OverswapMenu;

public class UserChoose extends OverswapMenu{

	public UserChoose() {
		imas = new BufferedImage[]{
			PicLoader.pic.getImage("res/ima/ote/pw1.png")	
		};
	}
	
	@Override
	protected void uppdateIntern() {
		
	}

}
