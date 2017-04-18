package network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class TCPlinker extends Thread{

	private FiFo downStream;
	
	private Scanner in;
	private PrintWriter out;
	
	private Socket socket;
	
	private boolean server;
	private boolean hasConected;
	
	private Semaphore sema;
	
	public TCPlinker(Socket sc, boolean server) throws IOException, InterruptedException{
		super("TCP linker to "+sc.getInetAddress().getHostAddress());
		
		socket = sc;
		
		in = new Scanner(socket.getInputStream());
		out = new PrintWriter( socket.getOutputStream(), true);
		
		this.server = server;
		
		downStream = new FiFo();
		
		sema = new Semaphore(1);
		
		sema.acquire();
		
		start();
	}
	
	public void run(){
		String response;
		if(server){
			response = in.nextLine();
			out.println("Hello");
		}else{
			out.println("hello");
			response = in.nextLine();
		}
		hasConected = true;
		sema.release();
		
		debug.Debug.println(response);
		
		String s;
		while (hasConected) {
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
	}
	
	public void write(String s){
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
	
	
	public boolean hasConnected(){
		if(sema.tryAcquire()){
			return hasConected;
		}
		return false;
	}
}
