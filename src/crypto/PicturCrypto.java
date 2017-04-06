package crypto;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.concurrent.Semaphore;

import cryptoUtility.Random;

public class PicturCrypto extends NumberEncrypter{

	protected Semaphore sema;
	
	public PicturCrypto(String pw) {
		super(pw, 16);
		sema = new Semaphore(1);
	}
	
	public static final void addNoise(BufferedImage ima){
		SecureRandom sc = Random.generateSR();
		
		Graphics gra = ima.getGraphics();
		Color c;
		int ri;
		int bi;
		int gi;
		for (int i = 0; i < ima.getWidth(); i++) {
			for (int j = 0; j < ima.getHeight(); j++) {
				c = new Color(ima.getRGB(i, j));
				ri = c.getRed();
				gi = c.getGreen();
				bi = c.getBlue();
				
				if(sc.nextBoolean() && ri <255)ri++;
				if(sc.nextBoolean() && gi <255)gi++;
				if(sc.nextBoolean() && bi <255)bi++;
				if(sc.nextBoolean() && ri >1)ri--;
				if(sc.nextBoolean() && gi >1)gi--;
				if(sc.nextBoolean() && bi >1)bi--;
				
				gra.setColor(new Color(ri,gi,bi));
				gra.drawRect(i, j, 0, 0);
			}
		}
	}
	
	/**
	 * Decrypts/encrypts the given {@link BufferedImage}
	 * This can take some Time, so it starts a new Thread. Use isDone() to wait.
	 * @param ima Image to encrypt
	 * @param enc true: Encryption-Mode
	 * @throws IllegalStateException if the security-Data has been destroyed
	 */
	public final void processPictur(BufferedImage ima, boolean enc){
		if(isDestroyd())
			throw new IllegalStateException("This Object has been destroyed");
		
		if(!sema.tryAcquire())
			debug.Debug.println("*Problem with Semaphore in Pictur Cryptograph", debug.Debug.WARN);
		
		final PicturCrypto it = this;
		new Thread(){
			public void run() {
				processChannel(ima, true, false, false, enc);
				processChannel(ima, false, true, false, enc);
				processChannel(ima, false, false, true, enc);
				
				it.destroy();
				sema.release();
			};
		}.start();
	}
	
	private final void processChannel(BufferedImage ima, boolean r, boolean g, boolean b, boolean enc){
		Graphics gra = ima.getGraphics();
		Color c;
		int ri;
		int bi;
		int gi;
		for (int i = 0; i < ima.getWidth(); i++) {
			for (int j = 0; j < ima.getHeight(); j++) {
				c = new Color(ima.getRGB(i, j));
				ri = c.getRed();
				gi = c.getGreen();
				bi = c.getBlue();
				
				if(r) ri = process(ri, enc);
				if(g) gi = process(gi, enc);
				if(b) bi = process(bi, enc);
				
				gra.setColor(new Color(ri,gi,bi));
				gra.drawRect(i, j, 0, 0);
			}
		}
	}
	
	private final int process(int i, boolean enc){
		int d = i%16;
		int b = i/16;
		if(enc){
			b = singleEncrypt(b);
			longTermCount++;
			d = singleEncrypt(d);
			longTermCount++;
		}else{
			b = singleDecrypt(b);
			longTermCount++;
			d = singleDecrypt(d);
			longTermCount++;
		}
		return d+b*16;
	}
	
	/**
	 * @return true, if the Picture is completly encrypted/decrypted.
	 */
	public boolean isDone(){
		return sema.availablePermits()>0;
	}

}
