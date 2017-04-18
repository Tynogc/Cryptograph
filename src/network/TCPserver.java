package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import cryptoUtility.NetEncryptionFrame;
import network.com.ServerToClient;

public class TCPserver extends Thread{
	
	private ServerSocket server;

	private TCPlinker linker;
	
	private ServerToClient stc;
	
	public TCPserver(int port){
		debug.Debug.println("Starting TCP server on Port "+port);
		try {
			server = new ServerSocket(port, 1);
			server.setSoTimeout(3000);
		} catch (IOException e) {
			debug.Debug.println("*ERROR creating TCP Server: "+e.getMessage(), debug.Debug.ERROR);
			return;
		}
		start();
	}
	
	@Override
	public void run() {
		try {
			Socket s = server.accept();
			linker = new TCPlinker(s, true);
			stc = new ServerToClient(linker, new NetEncryptionFrame());
		} catch (IOException e) {
			debug.Debug.println("*ERROR creating TCP Server: "+e.getMessage());
		} catch (InterruptedException e) {
			debug.Debug.println("*ERROR creating TCP Server: "+e.getMessage());
		}
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send(String header, String text, boolean encrypted){
		
	}
}
