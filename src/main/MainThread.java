package main;

public class MainThread extends Thread{
	
	private final SeyprisMain main;
	
	public MainThread(SeyprisMain m){
		main = m;
	}
}
