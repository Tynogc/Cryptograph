package network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import crypto.RSAsaveKEY;
import cryptoUtility.NetEncryptionFrame;

public class TCPlinker extends Thread{

	private FiFo downStream;
	
	private Scanner in;
	private PrintWriter out;
	
	private Socket socket;
	
	private boolean server;
	private boolean hasConected;
	private boolean isAlive;
	
	private Semaphore sema;
	
	public String name;
	public final String myName;
	public String keyString;
	
	public TCPlinker(Socket sc, boolean server, String myName) throws IOException, InterruptedException{
		super("TCP linker to "+sc.getInetAddress().getHostAddress());
		
		socket = sc;
		
		in = new Scanner(socket.getInputStream());
		out = new PrintWriter( socket.getOutputStream(), true);
		
		this.server = server;
		
		downStream = new FiFo();
		
		sema = new Semaphore(1);
		
		sema.acquire();
		
		isAlive = true;
		
		this.myName = myName;
		
		start();
	}
	
	public void run(){
		String response;
		//Exchange Name
		if(server){
			response = in.nextLine();
			out.println("Hello");
		}else{
			out.println("hello");
			response = in.nextLine();
		}
		name = response;
		hasConected = true;
		sema.release();
		
		debug.Debug.println(response);
		
		String s;
		while (isConnected()) {
			if(in.hasNext()){
				s = in.nextLine();
				try {
					sema.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
				downStream.in(s, System.currentTimeMillis());
				sema.release();
			}else{
				try {
					sleep(10);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		debug.Debug.println("TCP connection closed!", debug.Debug.ERROR);
		hasConected = false;
		//TODO close connection
	}
	
	public void write(String s){
		System.out.println(s);
		try {
			sema.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
			//TODO handle exeption
		}
		out.println(s);
		sema.release();
		if(out.checkError()){
			//TODO close connection
		}
		
	}
	
	
	public boolean hasConnected(int timeOut){
		if(timeOut <= 0 ){
			if(sema.tryAcquire()){
				sema.release();
				return hasConected;
			}
			return false;
		}
			
		try {
			if(sema.tryAcquire(timeOut, TimeUnit.MILLISECONDS)){
				sema.release();
				return hasConected;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void destroy(){
		isAlive = false;
	}
	
	public String readNext(){
		if(sema.tryAcquire()){
			if(! downStream.contains()){
				sema.release();
				return null;
			}
			String b = downStream.out();
			sema.release();
			return b;
		}
		return null;
	}
	
	public FiElement readNextElement(){
		if(sema.tryAcquire()){
			if(! downStream.contains()){
				sema.release();
				return null;
			}
			FiElement b = downStream.outElement();
			sema.release();
			return b;
		}
		return null;
	}
	
	public synchronized boolean isConnected(){
		return isAlive && hasConected;
	}
}
