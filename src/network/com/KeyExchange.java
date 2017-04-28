package network.com;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

import crypto.MessageAuthentication;
import crypto.RSAcrypto;
import cryptoUtility.NetEncryptionFrame;
import cryptoUtility.Random;
import network.CommunicationProcess;
import network.Writable;

public class KeyExchange extends CommunicationProcess {
	
	private final boolean started;
	
	private MessageAuthentication meAut;
	
	private static final String FAILED = "FAILED";
	private static final String SUCCESS = "SUCCESS";
	
	public KeyExchange(Writable l, NetEncryptionFrame n, boolean started, String init) {
		super(l, n);
		this.started = started;
		
		if(started){
			debug.Debug.println("* Started Key-Validation...");
			//TODO check Super-Key in data-Base!
			String s = Random.generateRandomString(30);
			try {
				meAut = new MessageAuthentication(MessageAuthentication.SRSHA_1024_WITH_RSA, false);
				meAut.init(n.getOtherSuperKey());
			} catch (NoSuchAlgorithmException e) {
				debug.Debug.println("*ERROR creating Message Authentification: "+e.toString(),
						debug.Debug.ERROR);
				return;
			}
			meAut.update(s.getBytes());
			meAut.update(n.getOtherKey().getModulus().toByteArray());
			//Encrypt challenge
			s = RSAcrypto.encrypt(s, n.getOtherSuperKey(), true);
			linker.write(COMCONSTANTS.KEY_EXCHANGE_START+COMCONSTANTS.DIV_HEADER+s);
		}else{
			debug.Debug.println("* Key-Validation was requested.");
			try {
				meAut = new MessageAuthentication(MessageAuthentication.SRSHA_1024_WITH_RSA, false);
				meAut.init(n.getMySuperKey());
			} catch (NoSuchAlgorithmException e) {
				debug.Debug.println("*ERROR creating Message Authentification: "+e.toString(),
						debug.Debug.ERROR);
				linker.write(COMCONSTANTS.KEY_EXCHANGE+COMCONSTANTS.DIV_HEADER+FAILED);
				return;
			}
			//Decrypt challenge
			try {
				init = RSAcrypto.decrypt(init, n.getMySuperKey(), false);
			} catch (UnsupportedEncodingException | GeneralSecurityException e) {
				debug.Debug.println("*ERROR Message Authentification, decrypting challenge: "+e.toString(),
						debug.Debug.ERROR);
				linker.write(COMCONSTANTS.KEY_EXCHANGE+COMCONSTANTS.DIV_HEADER+FAILED);
				return;
			}
			meAut.update(init.getBytes());
			meAut.update(n.getMyKey().getModulus().toByteArray());
			String very = meAut.doFinal();
			linker.write(COMCONSTANTS.KEY_VERIFY+COMCONSTANTS.DIV_HEADER+very);
		}
	}

	@Override
	protected boolean processIntern(String s) {
		if(started)
			return processStarter(s);
		return processReciver(s);
	}
	
	private boolean processStarter(String s){
		String[] st = s.split(COMCONSTANTS.DIV_HEADER);
		if(st.length<2)return false;
		
		if(st[0].compareTo(COMCONSTANTS.KEY_VERIFY)==0){
			if(meAut.verify(st[1])){
				debug.Debug.println("Key is valid!", debug.Debug.MESSAGE);
				key.verifySuperKey();
				linker.write(COMCONSTANTS.KEY_EXCHANGE+COMCONSTANTS.DIV_HEADER+SUCCESS);
			}else{
				debug.Debug.println("Key validation FAILED! "+meAut.getState(), debug.Debug.ERROR);
				//TODO
				linker.write(COMCONSTANTS.KEY_EXCHANGE+COMCONSTANTS.DIV_HEADER+FAILED);
			}
			terminated = true;
			return true;
		}
		
		if(st[0].compareTo(COMCONSTANTS.KEY_EXCHANGE)==0){
			if(st[1].compareTo(FAILED) == 0){
				terminated = true;
				return true;
			}
		}
		
		return false;
	}
	
	private boolean processReciver(String s){
		String[] st = s.split(COMCONSTANTS.DIV_HEADER);
		if(st.length<2)return false;
		
		if(st[0].compareTo(COMCONSTANTS.KEY_EXCHANGE)==0){
			if(st[1].compareTo(FAILED) == 0){
				terminated = true;
				debug.Debug.println("FAILED!", debug.Debug.ERROR);
				return true;
			}
			if(st[1].compareTo(SUCCESS) == 0){
				terminated = true;
				key.myKeyWasVerifyed();
				debug.Debug.println("Success!", debug.Debug.MESSAGE);
				return true;
			}
		}
		
		return false;
	}

}
