package main;

public class StartUp {
	
	private debug.DebugFrame frame;
	
	private boolean advancedInfo = false;
	private boolean fullScreen = true;
	private boolean standartStartUp = true;
	private boolean playIntro = true;
	private boolean playIntroSimple = true;
	private boolean spectator = false;
	
	public boolean startPicCrypto = false;
	public SetPassword picPW;
	
	public StartUp(debug.DebugFrame f){
		frame = f;
	}
	
	public void doStartUp(){
		gte();
		debug.Debug.showExtendedBootInfo = advancedInfo;
		//MainFrame.standartStartUp = standartStartUp;
		//MainFrame.spectatorMode = spectator;
//		if(fullScreen){
//			MainFrame.frameX = MainFrame.sizeX;
//			MainFrame.frameY = MainFrame.sizeY;
//		}
		//MainFrame.fullScreen = fullScreen;
		if(playIntro){
			//MainFrame.playIntro = 0;
		}else if(playIntroSimple){
			//MainFrame.playIntro = 1;
		}else{
			//MainFrame.playIntro = 2;
		}
		//loadRovervalues();
	}
	
	private void gtt(){
		debug.Debug.println("* Enter Password:");
		pw(null);
		debug.Debug.println("* Quick StartUp? [Y|n]");
		if(question(true)) return;
		debug.Debug.println("* Full Screen? [Y|n]");
		fullScreen = question(true);
		debug.Debug.println("* Spectator Mode? [y|N]");
		spectator = question(false);
		debug.Debug.println("* AdvancedOptions? [y|N]");
		if(!question(false))return;
		
		debug.Debug.println("* Extended Information? [y|N]");
		advancedInfo = question(false);
		
		debug.Debug.println("* Use standart Boot order? [Y|n]");
		standartStartUp = question(true);
		
		debug.Debug.println("* Play Intro? [Y|n]");
		playIntro = question(true);
		
		if(!playIntro){
			debug.Debug.println("* Play simplyfied Version instead? [Y|n]");
			playIntroSimple = question(true);
		}
	}
	
	private void gte(){
		debug.Debug.println("* Quick StartUp? [Y|n]");
		if(!question(true)) picCrypt();
		
	}
	
	private void picCrypt(){
		debug.Debug.println("* Start Pictur-Cryptograph? [y|N]");
		if(!question(false)) return;
		startPicCrypto = true;
		picPW = new SetPassword();
		debug.Debug.println("* Enter Password: ");
		debug.Debug.print("[Pictur Cryptograph]", debug.Debug.COM);
		pw(picPW);
	}
	
	@SuppressWarnings("static-access")
	public void pw(SetPassword p){
		frame.setPwState(true, p);
		while(frame.canState()==0){
			try {
				Thread.currentThread().sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		frame.setPwState(false, null);
	}
	
	public boolean question(boolean enter){
		boolean t = quete(enter);
		frame.setCheckState(false);
		if(t){
			debug.Debug.println("* YES");
		}else{
			debug.Debug.println("* NO");
		}
		return t;
	}
	
	@SuppressWarnings("static-access")
	public boolean quete(boolean enter){
		frame.setCheckState(true);
		
		while (true){
			switch (frame.canState()) {
			case 0:
				try {
					Thread.currentThread().sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
				
			case 1:
				return true;
			case 2:
				return false;
			case 3:
				return enter;

			default:
				break;
			}
			
		}
	}

}
