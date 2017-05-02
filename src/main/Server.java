package main;

import java.net.SocketException;

import crypto.RSAsaveKEY;
import network.FiElement;
import network.TCPserver;
import network.UDPsystem;
import network.com.COMCONSTANTS;
import network.com.ConnectionBasics;

public class Server {

	private boolean serverIsRunning;
	
	private UDPsystem udp;
	private ClientList tcp;
	
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
					if(tcp == null)
						tcp = new ClientList(new TCPserver(k, myKey));
					else tcp.add(new TCPserver(k, myKey));
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
	
	public synchronized void send(String message) throws Exception{
		String to = message.split(COMCONSTANTS.DIV_HEADER)[0];
		to = ConnectionBasics.divideHeader(to)[0];
		ClientList cl = tcp;
		while (cl != null) {
			if(to.compareTo(cl.client.getConnectionName()) == 0){
				cl.client.write(message);
				return;
			}
			cl = cl.next;
		}
	}
}
class ClientList{
	
	public final TCPserver client;
	
	public ClientList next;
	
	public ClientList(TCPserver c){
		client = c;
	}
	
	public void add(TCPserver c){
		if(next == null)
			next = new ClientList(c);
		else
			next.add(c);
	}
}
