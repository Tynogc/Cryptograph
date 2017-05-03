package user;

import java.io.File;

import javax.security.auth.DestroyFailedException;

import main.SetPassword;
import crypto.KeySaveLoad;
import crypto.RSAsaveKEY;

public class KeyHandler {

	public static KeyHandler key;
	
	private static final String version = "0.1";
	
	public final String privateKeyFile = "data/user/default/Private.key";
	
	private RSAsaveKEY privateKey;
	
	private final boolean isPrivateKeyEncr;
	
	public KeyHandler(){
		debug.Debug.println("Starting Key-Handling Agent v"+version);
		
		File f = new File(privateKeyFile);
		if(!f.exists()){
			debug.Debug.println("No Key Found...", debug.Debug.WARN);
			isPrivateKeyEncr = false;
			return;
		}
		try {
			privateKey = new KeySaveLoad().load(f);
			debug.Debug.println("Un-Encrypted Private-Key found and Loaded!");
		} catch (Exception e) {
			debug.Debug.println("Encrypted Private-Key found!");
		}
		isPrivateKeyEncr = privateKey == null;
	}
	
	public boolean isPrivateKeyEncrypted(){
		return isPrivateKeyEncr;
	}
	
	public void decryptPrivateKey(SetPassword p){
		try {
			debug.Debug.println("*Loading Key...");
			privateKey = new KeySaveLoad().loadEncrypted(new File(privateKeyFile), p.getPassword());
		} catch (Exception e) {
			debug.Debug.println("ERROR Encrypting Key: "+e.toString(), debug.Debug.ERROR);
			return;
		}
		p.destroy();
		
		if(!(privateKey.runTest() && privateKey.runTest())){
			debug.Debug.println("Selftest FAILED!", debug.Debug.ERROR);
			debug.Debug.println("The Password dosn't seam to be correct", debug.Debug.SUBERR);
			debug.Debug.println("the Key is not Valid!", debug.Debug.SUBERR);
			try {
				privateKey.destroy();
			} catch (DestroyFailedException e) {
				debug.Debug.println("ERROR Destroying Key: "+e.toString(), debug.Debug.ERROR);
			}
			privateKey = null;
		}
	}
}
