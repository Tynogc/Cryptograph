package main;

import java.net.SocketException;

import network.FiElement;
import network.TCPserver;
import network.UDPsystem;

public class Server {

	private boolean serverIsRunning;
	
	private UDPsystem udp;
	private TCPserver tcp;
	
	public Server(){
		serverIsRunning = true;
		try {
			udp = new UDPsystem(1234);
		} catch (SocketException e) {
			debug.Debug.printExeption(e);
		}
	}
	
	public void run(){
		while(serverIsRunning){
			try {
				if(udp.hasNext()){
					int k = (int)(Math.random()*100)+1000;
					tcp = new TCPserver(k);
					FiElement n = udp.recive();
					udp.send("Hello_"+k+"_Port", n.adress);
				}else{
					Thread.sleep(100);
				}
			} catch (Exception e) {
				debug.Debug.println("* FATAL: Server-Error "+e.getMessage(), debug.Debug.FATAL);
				debug.Debug.printExeption(e);
			}
		}
	}
}
