package main;

public class StartUp {
	
	private debug.DebugFrame frame;
	
	private boolean advancedInfo = false;
	private boolean fullScreen = true;
	private boolean standartStartUp = true;
	private boolean playIntro = true;
	private boolean playIntroSimple = true;
	private boolean spectator = false;
	
	public boolean server = false;
	
	public boolean startPicCrypto = false;
	public SetPassword picPW;
	
	public StartUp(debug.DebugFrame f){
		frame = f;
	}
	
	public void doStartUp(){
		gtt();
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
		gte();
		
		new UserManager(server, frame);
		
		user.KeyHandler kh = new user.KeyHandler();
		if(kh.isPrivateKeyEncrypted()){
			debug.Debug.println("* Enter Password:");
			debug.Debug.print("[Private Key "+UserManager.getUserName()+"]", debug.Debug.COM);
			SetPassword sp = new SetPassword();
			pw(sp);
			kh.decryptPrivateKey(sp);
		}
	}
	
	private void gte(){
		debug.Debug.println("* Quick StartUp? [Y|n]");
		if(question(true)) return;
		debug.Debug.println("* Start Server? [y|N]");
		if(question(false)){
			//TODO set user directory to Server!
			server = true;
			return;
		}
		
		picCrypt();
		
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
