package network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.security.GeneralSecurityException;
import java.security.spec.InvalidKeySpecException;

import crypto.RSAcrypto;
import crypto.RSAsaveKEY;
import cryptoUtility.NetEncryptionFrame;
import network.com.COMCONSTANTS;
import network.com.ClientToServer;
import network.com.KeyExchange;

public class TCPclient implements Writable{

	private Socket socket;
	private TCPlinker linker;
	
	public final String ip;
	public int port;
	
	private int timeToEstConn = 1000;
	
	private UDPsystem udp;
	
	private int trys;
	private long lastTry;
	private static final int timeForTry = 3000;
	
	private NetEncryptionFrame encryptionFrame;
	
	private CommunicationProcess process;
	
	public TCPclient(String i, int p){
		ip = i;
		port = p;
		
		//TODO load key
		encryptionFrame = new NetEncryptionFrame("Server", true);
		try {
			encryptionFrame.setMyKey(RSAsaveKEY.generateKey(1024, true, true, 0, null));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
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
					 if(linker.isConnected()){
						 udp = null;
						 debug.Debug.println("Connected to "+ip+":"+port, debug.Debug.MESSAGE);
						 new Thread(){
							 public void run(){
								 loop();
							 }
						 }.start();
					 }
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
			
			//Listen for Server Keys
			listenForKey();
			listenForKey();
			process = new ClientToServer(this, encryptionFrame);
			
			//Start Key-Validation for server's Session-Key
			process.add(new KeyExchange(this, encryptionFrame, true, null));
			
		} catch (IOException e) {
			debug.Debug.println("*Error conecting: "+e.getMessage(), debug.Debug.ERROR);
		} catch (InterruptedException e) {
			debug.Debug.println("*Error conecting: "+e.getMessage(), debug.Debug.ERROR);
		} catch (ArrayIndexOutOfBoundsException e) {
			debug.Debug.println("*ERROR exchanging Keys!", debug.Debug.ERROR);
		} catch (InvalidKeySpecException e) {
			debug.Debug.println("*ERROR exchanging Keys!", debug.Debug.ERROR);
		}
	}
	
	public void loop(){
		FiElement s;
		while(linker.isConnected()){
			s = linker.readNextElement();
			if(s != null){
				recive(s);
			}else{
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void write(String s){
		try {
			s = RSAcrypto.encrypt(s, encryptionFrame.getOtherKey(), true);
		} catch (Exception e) {
			debug.Debug.println("* ERROR Encrypting: "+e.toString(), debug.Debug.ERROR);
		}
		linker.write(s);
	}
	
	private void recive(FiElement e){
		String s = e.str;
		try {
			s = RSAcrypto.decrypt(s, encryptionFrame.getOtherKey(), true);
			process.processString(s);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (GeneralSecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void listenForKey() throws ArrayIndexOutOfBoundsException, InvalidKeySpecException, InterruptedException{
		String key;
		int trys = 0;
		do {
			trys++;
			Thread.sleep(1);
			key = linker.readNext();
			if(trys > 3000){
				debug.Debug.println("*ERROR exchanging Keys! Time expiered.", debug.Debug.ERROR);
				linker.destroy();
				throw new InterruptedException("Key Exchange took to long...");
			}
		} while (key == null);
		String[] st = key.split(COMCONSTANTS.DIV_HEADER);
		RSAsaveKEY k = new RSAsaveKEY(st[1]);
		if(st[0].compareTo(COMCONSTANTS.KEY) == 0)
			encryptionFrame.setOtherKey(k);
		else if(st[0].compareTo(COMCONSTANTS.KEY_SUPER) == 0)
			encryptionFrame.setOtherSuperKey(k);
		debug.Debug.println("Got Key: "+key);
	}
}
