package network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.Semaphore;

public class UDPsystem extends Thread{

	private DatagramSocket socket;
	
	protected byte[] buffer;
	protected final int bufferSize = 1024;
	
	protected FiFo in;
	
	private boolean isRunning;
	
	public final int port;
	
	protected Semaphore sema;
	
	public UDPsystem(int port) throws SocketException{
		socket = new DatagramSocket(port);
		isRunning = true;
		in = new FiFo(); 
		this.port = port;
		sema = new Semaphore(1);
		start();
	}
	
	public void run(){
		DatagramPacket packet;
		while (isRunning) {
			buffer = new byte[bufferSize];
			packet = new DatagramPacket(buffer, bufferSize);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				debug.Debug.println("*Error reciving UDP: "+e.toString());
				continue;
			}
			SocketAddress adress = packet.getSocketAddress();
			try {
				sema.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
				continue;
			}
			in.in(new String(buffer, 0, packet.getLength()), System.currentTimeMillis(), adress);
			sema.release();
		}
		socket.close();
	}
	
	public boolean hasNext(){
		boolean b = false;
		if(sema.tryAcquire()){
			b = in.contains();
			sema.release();
		}
		return b;
	}
	
	public FiElement recive(){
		try {
			sema.acquire();
			FiElement f = in.outElement();
			sema.release();
			return f;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return new FiElement("", 0, null);
		}
	}
	
	public void send(String s, SocketAddress a){
		try {
			sema.acquire();
			
			if(!s.endsWith("\n\r"))
				s+="\n\r";
			    byte[] raw = s.getBytes("UTF-8");

			    DatagramPacket packet = new DatagramPacket( raw, raw.length, a);

			    socket.send(packet);
			
			sema.release();
		} catch (InterruptedException | IOException e) {
			debug.Debug.println("*Problem sending Message (UDP): "+e.getMessage(),
					debug.Debug.WARN);
		}
	}
	
	public void close(){
		isRunning = false;
		socket.close();
	}
	
}
