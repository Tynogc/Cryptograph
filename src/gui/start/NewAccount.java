package gui.start;

import main.GuiControle;
import main.SeyprisMain;

public class NewAccount{
	
	//Step 0: User Name
	//Step 1: Images
	//Step 2: Keys
	//Step 3: Servers
	private int currentStep = 0;
	
	private int x;
	private int y;
	
	public String name;

	public NewAccount() {
		x = (SeyprisMain.sizeX()-700)/2;
		y = (SeyprisMain.sizeY()-500)/2+30;
		openMenu();
	}
	
	public void nextMenu(){
		currentStep++;
		openMenu();
	}
	
	public void prevMenu(){
		currentStep--;
		openMenu();
	}
	
	private void openMenu(){
		if(currentStep<0)
			currentStep = 0;
		
		if(currentStep == 0){
			GuiControle.addMenu(new AccountSetup_Name(x, y, this));
		}
	}

}
