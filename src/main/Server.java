package main;

import java.net.SocketException;

import crypto.RSAsaveKEY;
import cryptoUtility.NetEncryptionFrame;
import network.FiElement;
import network.TCPserver;
import network.UDPsystem;

public class Server {

	private boolean serverIsRunning;
	
	private UDPsystem udp;
	private TCPserver tcp;
	
	private RSAsaveKEY myKey;
	
	public Server(){
		serverIsRunning = true;
		try {
			udp = new UDPsystem(1234);
		} catch (SocketException e) {
			debug.Debug.printExeption(e);
		}
		
		//TODO load key
		try {
			myKey = RSAsaveKEY.generateKey(1024, true, true, 0, null);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public void run(){
		while(serverIsRunning){
			try {
				if(udp.hasNext()){
					int k = (int)(Math.random()*100)+8000;
					tcp = new TCPserver(k, myKey);
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
