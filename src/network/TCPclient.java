package network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.security.GeneralSecurityException;
import java.security.spec.InvalidKeySpecException;

import user.KeyHandler;
import user.SideDisplay;
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
	
	private int timeToEstConn = 6000;
	
	private UDPsystem udp;
	private SideDisplay sideDisplay;
	
	private int trys;
	private static final int numberOfTrys = 10;
	private long lastTry;
	private static final int timeForTry = 3000;
	
	private NetEncryptionFrame encryptionFrame;
	
	private CommunicationProcess process;
	
	public final String myName;
	
	private final TCPclient me;
	
	public TCPclient(String i, int p, String myName){
		ip = i;
		port = p;
		
		encryptionFrame = new NetEncryptionFrame("Server", true);
		encryptionFrame.setMySuperKey(KeyHandler.key.getPrivateKey("TCP-Client to "+i));
		System.out.println("Fingerprint: "+
		cryptoUtility.RSAkeyFingerprint.getFingerprint(encryptionFrame.getMySuperKey()));
		
		try {
			udp = new UDPsystem(port+256);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		sideDisplay = new SideDisplay(){
			@Override
			public void wasClicked() {
				if(linker == null){
					retryConnect();
				}else if(!linker.isConnected()){
					retryConnect();
				}
			}
			@Override
			public void wasRightClicked() {
				new gui.sub.RCM_Server(me);
			}
		};
		sideDisplay.mainString = ip;
		sideDisplay.status = SideDisplay.SERVER_CONNECTING;
		sideDisplay.update();
		
		this.myName = myName+"@"+ip;
		
		me = this;
	}
	
	public void refresh(){
		if(udp != null){
			if(udp.hasNext()){
				try {
					String[] s = udp.recive().str.split("_");
					port = Integer.parseInt(s[1]);
					connectFinal();
					if(linker.isConnected()){
						udp.close();
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
				trys++;
				if(trys>numberOfTrys){
					udp = null;
					sideDisplay.status = SideDisplay.SERVER_NO_CONNECTION;
					sideDisplay.update();
					return;
				}
				//TODO
				udp.send("This is a test...", new InetSocketAddress(ip, 1234));//TODO non-deafault port...
				debug.Debug.println("Connecting... Try "+trys);
				lastTry = System.currentTimeMillis();
			}
		}
	}
	
	private void connectFinal(){
		//Generate Session Key
		try {
			encryptionFrame.setMyKey(RSAsaveKEY.generateKey(1024, true, true, 0, null));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(ip, port), timeToEstConn);
			linker = new TCPlinker(socket, false, myName.split("@")[0]);
			
			//Listen for Server Keys
			listenForKey();
			listenForKey();
			process = new ClientToServer(this, encryptionFrame, myName, sideDisplay);
			
			//Start Key-Validation for server's Session-Key
			process.add(new KeyExchange(this, encryptionFrame, true, null, myName));
			
			sideDisplay.status = SideDisplay.SERVER_ONLINE;//TODO
			sideDisplay.update();
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
		sideDisplay.status = SideDisplay.SERVER_NO_CONNECTION;
		sideDisplay.update();
	}
	
	@Override
	public void write(String s){
		if(linker == null)
			return;
		System.out.println(s);
		try {
			s = RSAcrypto.encrypt(s, encryptionFrame.getOtherKey(), true);
		} catch (Exception e) {
			debug.Debug.println("* ERROR Encrypting: "+e.toString(), debug.Debug.ERROR);
		}
		linker.write(s);
		System.out.println("$$$");
	}
	
	private void recive(FiElement e){
		String s = e.str;
		try {
			s = RSAcrypto.decrypt(s, encryptionFrame.getOtherKey(), true);
			if(!s.startsWith(COMCONSTANTS.PING))
				System.out.println("RECIVER --> "+s);
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
	
	public SideDisplay getSideDisplay(){
		return sideDisplay;
	}
	
	public NetEncryptionFrame getNef(){
		return encryptionFrame;
	}
	
	public void retryConnect(){
		sideDisplay.status = SideDisplay.SERVER_CONNECTING;
		sideDisplay.update();
		if(udp == null){
			try {
				udp = new UDPsystem(port);
			} catch (SocketException e) {
				debug.Debug.println("*Error reconecting: "+e.toString(), debug.Debug.ERROR);
			}
		}
		trys = 0;
	}
	
	public boolean isConnected(){
		if(linker == null)
			return false;
		return linker.isConnected();
	}
	
	public void addToYourComProcess(CommunicationProcess c){
		process.add(c);
	}
	
	public TCPlinker getLinker(){
		return linker;
	}
}
