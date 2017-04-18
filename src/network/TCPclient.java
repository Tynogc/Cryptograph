package network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class TCPclient {

	private Socket socket;
	private TCPlinker linker;
	
	public final String ip;
	public int port;
	
	private int timeToEstConn = 1000;
	
	private UDPsystem udp;
	
	private int trys;
	private long lastTry;
	private static final int timeForTry = 3000;
	
	public TCPclient(String i, int p){
		ip = i;
		port = p;
		
		try {
			udp = new UDPsystem(1245);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void refresh(){
		if(udp != null){
			 if(udp.hasNext()){
				 try {
					 String[] s = udp.recive().str.split("_");
					 port = Integer.parseInt(s[1]);
					 connectFinal();
					 udp = null;
					 debug.Debug.println("Connected to "+ip+":"+port, debug.Debug.MESSAGE);
				} catch (Exception e) {
					debug.Debug.println("*Error opening TCP: "+e.toString(),
							debug.Debug.ERROR);
				}
			 }
			 if(System.currentTimeMillis()-lastTry>timeForTry){
				 udp.send("This is a test...", new InetSocketAddress(ip, port));
				 trys++;
				 debug.Debug.println("Connecting... Try "+trys);
				 lastTry = System.currentTimeMillis();
			 }
		}
	}
	
	private void connectFinal(){
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(ip, port), timeToEstConn);
			linker = new TCPlinker(socket, false);
		} catch (IOException e) {
			debug.Debug.println("*Error conecting: "+e.getMessage(), debug.Debug.ERROR);
		} catch (InterruptedException e) {
			debug.Debug.println("*Error conecting: "+e.getMessage(), debug.Debug.ERROR);
		}
	}
}
