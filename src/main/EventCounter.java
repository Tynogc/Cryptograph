package main;

public class EventCounter {

	private static final int TIME_UNTIL_SLOWDOWN = 1000;
	
	private static long lastEvent;
	
	public static void event(){
		lastEvent = System.currentTimeMillis();
	}
	
	public static boolean wasEvent(){
		return System.currentTimeMillis()-lastEvent < TIME_UNTIL_SLOWDOWN;
	}
}
