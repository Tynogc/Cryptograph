package cryptoUtility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Simple System to farm entropy from the File-System
 * @author Sven Schneider
 */
public class TraverseFiles {
	
	private SecureRandom random;
	private File start;
	
	public TraverseFiles(SecureRandom sr){
		random = sr;
		start = new File("res/");
	}
	
	public TraverseFiles(File start, SecureRandom sr){
		this.start = start;
		random = sr;
	}
	
	/**
	 * Traverses the File system and generates Entropy by found Files
	 * @param m Amount of Entropy to generate
	 */
	public void feedEntropy(int m){
		byte[] b = new byte[m];
		feedEntropy(b);
		System.out.println();
		for (int i = 0; i < b.length-1; i+=2) {
			int z = ((int)b[i]<<8)+(int)b[i+1];
			Random.enterEntropy(z);
		}
		System.out.println(b.length);
	}
	
	/**
	 * Traverses the File system and generates Entropy by found Files
	 * @param b Stores the entropy in b
	 */
	public void feedEntropy(byte[] b){
		int pos = 0;
		try {
			while(pos<b.length){
				byte[] k = generateRndmHash();
				int j = random.nextInt(32);
				for (int i = random.nextInt(32); i < k.length-j; i++) {
					b[pos] = k[i];
					pos++;
					if(pos>=b.length)return;
				}
			}
		} catch (Exception e) {
			debug.Debug.printExeption(e);
		}
		
	}
	
	private byte[] generateRndmHash() throws GeneralSecurityException, IOException{
		File f = findFile();
		for (int i = 0; i < 5; i++) {
			if(f != null)break;
			f = findFile();
		}
		if(f == null)throw new FileNotFoundException("Problem finding Files in the directory!");
		
		byte[] b = new byte[random.nextInt(400)];
		FileInputStream fio = new FileInputStream(f);
		fio.read(b);
		fio.close();
		
		MessageDigest messageDigest;
		messageDigest = MessageDigest.getInstance("SHA-512");
		messageDigest.update(b);
		
		return messageDigest.digest();
	}
	
	private File findFile(){
		File f = start;
		while(f.isDirectory()){
			f = getFileFromDirectory(f.listFiles());
			if(f == null)return null;
		}
		System.out.println(f.getPath());
		return f;
	}
	
	private File getFileFromDirectory(File[] f){
		if(f == null)return null;
		if(f.length<=0)return null;
		int i = random.nextInt(f.length);
		return f[i];
	}
	
}
