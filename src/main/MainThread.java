package main;

public class MainThread extends Thread{
	
	private final SeyprisMain main;
	
	public static long currentTime = 0;
	
	private int lastFPS;
	private long fpsMarker;
	private int currFps;
	private double frameTimeOVA;
	private double frameTimeOVAsmal;
	
	private boolean isRunning = true;
	
	
	public static final int timeToFrameUppdateFAST = 33;
	public static final int timeToFrameUppdateSLOW = 100;
	private int timeToFrameUppdate = timeToFrameUppdateFAST;
	private boolean slow;
	
	public MainThread(SeyprisMain m){
		super("MAIN Thread");
		main = m;
		frameTimeOVA = 50;
		frameTimeOVAsmal = 50;
	}
	
	public void run(){
		try {
			int sleepTime = 0;
			fpsMarker = System.currentTimeMillis();
			while(isRunning){
				//gui.PerformanceMenu.ThreadCheck(Thread.currentThread());
				//gui.PerformanceMenu.startTime();
				if(EventCounter.wasEvent() != !slow){
					if(EventCounter.wasEvent()){
						slow = false;
						timeToFrameUppdate = timeToFrameUppdateFAST;
					}else{
						slow = true;
						timeToFrameUppdate = timeToFrameUppdateSLOW;
					}
				}
				
				currFps++;
				
				currentTime = System.currentTimeMillis();
				main.loop(lastFPS, sleepTime, (int)frameTimeOVAsmal, (int)frameTimeOVA);
				
				//FPS uberprufen
				if(currentTime-fpsMarker>500){
					fpsMarker += 500;
					if(Math.abs(fpsMarker-currentTime)>1000){
						fpsMarker = currentTime;
						currFps = 0;
						debug.Debug.println("WARNING: FPS criticaly low!", debug.Debug.WARN);
					}
					lastFPS = currFps*2;
					currFps = 0;
				}
				
				int q = (int)(System.currentTimeMillis()-currentTime);
				frameTimeOVA = (frameTimeOVA*499+q)/500;
				frameTimeOVAsmal = (frameTimeOVAsmal*49+q)/50;
				
				//Schlafen
				sleepTime = timeToFrameUppdate-(int)(System.currentTimeMillis()-currentTime);
				if(sleepTime > 0 ){
					try {
						sleep(sleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}else{
					System.out.println("lag"+sleepTime);
				}
			}
			
		} catch (Exception e) {
			debug.Debug.println("*FATAL: Exeption in Thread MAIN:",debug.Debug.FATAL);
			debug.Debug.printExeption(e);
		}
	}
}
