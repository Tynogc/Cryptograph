package network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.spec.InvalidKeySpecException;

import crypto.RSAcrypto;
import crypto.RSAsaveKEY;
import cryptoUtility.NetEncryptionFrame;
import network.com.COMCONSTANTS;
import network.com.ServerToClient;

public class TCPserver extends Thread implements Writable{
	
	private ServerSocket server;

	private TCPlinker linker;
	
	private ServerToClient stc;
	
	private NetEncryptionFrame encryptionFrame;
	
	public TCPserver(int port, RSAsaveKEY serverKey){
		debug.Debug.println("Starting TCP server on Port "+port);
		try {
			server = new ServerSocket(port, 1);
			server.setSoTimeout(3000);
		} catch (IOException e) {
			debug.Debug.println("*ERROR creating TCP Server: "+e.getMessage(), debug.Debug.ERROR);
			return;
		}
		
		encryptionFrame = new NetEncryptionFrame("Server", false);
		//Sets the SuperKey
		encryptionFrame.setMySuperKey(serverKey);
		//Generate new Session-Key
		try {
			//TODO variable Size, better Catch, Random != null
			RSAsaveKEY sessionKey = RSAsaveKEY.generateKey(1024, true, false, 1, null);
			encryptionFrame.setMyKey(sessionKey);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		start();
	}
	
	@Override
	public void run() {
		try {
			Socket s = server.accept();
			linker = new TCPlinker(s, true, "TestServer");//TODO
			if(linker.hasConnected(5000)){
				//Send the Public Key
				linker.write(COMCONSTANTS.KEY + COMCONSTANTS.DIV_HEADER +
						encryptionFrame.getMyKey().getPublicKeyString());
				linker.write(COMCONSTANTS.KEY_SUPER + COMCONSTANTS.DIV_HEADER +
						encryptionFrame.getMySuperKey().getPublicKeyString());
				stc = new ServerToClient(this, encryptionFrame, "");
				//Enter loop:
				loop();
			}
		} catch (IOException e) {
			debug.Debug.println("*ERROR creating TCP Server: "+e.getMessage());
		} catch (InterruptedException e) {
			debug.Debug.println("*ERROR creating TCP Server: "+e.getMessage());
		} catch (ArrayIndexOutOfBoundsException e) {
			debug.Debug.println("*ERROR exchanging Keys!");
		}
		
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//TODO close linker
	}

	/**
	 * Encrypts the String first by the user's Session PrivateKey than by the Servers PublicKey 
	 * @Override
	 */
	public void write(String s) {
		s = RSAcrypto.encrypt(s, encryptionFrame.getMyKey(), false);
		linker.write(s);
	}
	
	private void loop(){
		try {
			String v;
			while(linker.isConnected()){
				stc.refresh();
				v = linker.readNext();
				if(v != null){
					recive(v);
				}else{
					try {
						sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			debug.Debug.println("* Connection Exeption: "+e.toString(), debug.Debug.WARN);
		}
	}
	
	public void recive(String s) {
		try {
			s = RSAcrypto.decrypt(s, encryptionFrame.getMyKey(), false);
			stc.processString(s);
		} catch (UnsupportedEncodingException | GeneralSecurityException e) {
			e.printStackTrace();
		}
	}
	
	public String getConnectionName(){
		if(linker == null)
			return "";
		return linker.name;
	}
}
