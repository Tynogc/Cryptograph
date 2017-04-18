package crypto;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class SRSHA{
	/**
	 * The SuperRandomSecureHashingAlgorithm is Idear and Copyright Sven T. Schneider
	 * @author Sven T. Schneider
	 * @version 0.1
	 */
	
	public final int size;
	
	//Size of Array: sqrt(size) or sqrt(size)+1
	private final int q;
	private boolean[][] array;
	
	public static final int SRSHA_64 = 64;
	public static final int SRSHA_128 = 128;
	public static final int SRSHA_256 = 256;
	public static final int SRSHA_512 = 512;
	public static final int SRSHA_1024 = 1024;
	
	//Never use if False! For Debug reason only!!!
	private static final boolean DO_COMPLEX = true;
	
	//Super destructors
	private int sum;
	private byte[] mem;
	private int memPos;
	//Cycles per update
	private static final int CYCLES = 13;
	private int cycleCounter;
	
	//Stops changing to the Hash
	private boolean isFinal;
	
	/**
	 * Generates a Hash by the SRSH-Algorithm ( (C) Sven T. Schneider)
	 * Call the update(byte[])-Method to digest bytes into the Hash
	 * The digest()-Methods return the current state of the Hash
	 * During operation you can call reset() to reset the Algorithm to the start!
	 * To finish operation, call doFinal(). If necessary, it runs the digesting one final time.
	 * After that is done, the final Hash is given to you.
	 * @param size Bit-length of the Hash, use the Provided Variables:
	 * 		SRSHA.SRSHA_64 - SRSHA.SRSHA_1024 
	 * to ensure compatibility. (It should work with any other size as well)
	 */
	public SRSHA(int size){
		this.size = size;
		int qs = (int) Math.sqrt(size);
		if(qs*qs < size)qs++;
		q = qs;
		System.out.println(q+" "+(q*q));
		isFinal = false;
		
		reset();
	}
	
	/**
	 * Resets the Algorithm to start
	 */
	public void reset(){
		if(isFinal)
			return;
		array = new boolean[q][q];
		//Super destructors
		sum = 0;
		mem = new byte[CYCLES];
		memPos = 0;
		cycleCounter = 0;
	}
	
	/**
	 * Takes the byte Array and digests it into the hash (TODO)
	 * @param b the bytes to digest
	 */
	public void update(byte[] b){
		if(isFinal)
			return;
		int x = 0;
		int y = 0;
		for (int i = 0; i < b.length; i++) {
			//fill mem
			mem[memPos] = (byte)(b[i] ^ mem[memPos]);
			memPos++;
			if(memPos>= mem.length) memPos = 0;
			//Fill sum
			int bi = b[i];
			if(bi<0) bi += 256;
			sum+=bi;
			//Fill array
			for (int j = 0; j < 8; j++) {
				array[x][y] = (array[x][y] ^ takeBit(b[i], j));
				x++;
				if(x>=q){
					y++;
					x = 0;
					if(y>=q)y = 0;
				}
			}
		}
	}
	
	/**
	 * Same as update(byte[]), however also returns a digested Hash.
	 * @param b the Bytes to digest
	 * @return Hash of current state
	 */
	public byte[] digest(byte[] b){
		update(b);
		return digest();
	}
	
	/**
	 * Returns the current digested hash
	 * @return current hash
	 */
	public byte[] digest(){
		byte[] ret1 = new byte[size/8];
		int p = 0;
		int pi = 1;
		int count = 0;
		for (int i = 0; i < q; i++) {
			for (int j = 0; j < q; j++) {
				if(isThere(i, j))
					count += pi;
				pi *= 2;
				if(pi >= 255){
					pi = 1;
					ret1[p] = (byte)(count ^ ret1[p]);
					p++;
					count = 0;
					if(p>=ret1.length)p = 0;
				}
			}
		}
		
		return ret1;
	}
	
	private static final int ZERO_TO_ONE = 2;
	private static final int ONE_TO_ONE1 = 3;
	private static final int ONE_TO_ONE2 = 6;
	
	/**
	 * Dose one Iteration over the current state
	 * @deprecated Only for debug reasons! Should not be used during normal operation!
	 */
	public void doLoop(){
		doLoopIntern();
	}
	
	private void doLoopIntern(){
		if(isFinal)
			return;
		cycleCounter++;
		
		boolean[][] b = new boolean[q][q]; 
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[i].length; j++) {
				int f = count(i, j);
				if(isThere(i, j)){
					if(f == ONE_TO_ONE1 || f == ONE_TO_ONE2)
						b[i][j] = true;
					else
						b[i][j] = false;
				}else{
					if(f == ZERO_TO_ONE)
						b[i][j] = true;
					else
						b[i][j] = false;
				}
			}
		}
		array = b;
		
		//Fill mem
		mem[memPos] = (byte)(mem[memPos] ^ (byte)traverse(sum%q));
		
		//Super Random Loop
		if(DO_COMPLEX)
			srDoLoop();
	}
	
	/**
	 * Paints the Current state
	 * @param k Size per Pixel
	 * @return {@link BufferedImage} the visualization of the current state
	 */
	public BufferedImage testPaint(int k){
		BufferedImage ima = new BufferedImage(q*k+2, q*k+50, BufferedImage.TYPE_INT_RGB);
		Graphics g = ima.getGraphics();
		g.setColor(Color.red);
		for (int i = 0; i < q; i++) {
			for (int j = 0; j < q; j++) {
				if(array[i][j])
					g.fillRect(i*k+1, j*k+1, k, k);
			}
		}
		g.setColor(Color.blue);
		g.drawRect(0, 0, q*k+1, q*k+1);
		g.fillRect(memPos*20, q*k+20, 20, 15);
		g.setColor(Color.red);
		for (int i = 0; i < mem.length; i++) {
			g.drawString(""+mem[i], i*20, q*k+32);
		}
		g.drawString(sum+"   "+cycleCounter, 20, q*k+46);
		return ima;
	}
	
	/**
	 * @return true: doFinal() had been called, Hash can't be changed anymore
	 */
	public boolean isFinal(){
		return isFinal;
	}
	
	/**
	 * Finalizes the digesting process and returns the digested Hash
	 * Form here on the Hash can't be changed!
	 * @return
	 */
	public byte[] doFinal(){
		isFinal = true;
		return digest();
	}
	
	/**
	 * Calls the digesting algorithm one last time with the byte[] b and returns the final Hash
	 * Form here on the Hash can't be changed!
	 * @param b
	 * @return
	 */
	public byte[] doFinal(byte[] b){
		byte[] r = digest(b);
		isFinal = true;
		return r;
	}
	
	/**
	 * Counts the surrounding fields
	 * @param x
	 * @param y
	 * @return
	 */
	private int count(int x, int y){
		int f = 0;
		if(isThere(x+1, y+1))f++;
		if(isThere(x+1, y))f++;
		if(isThere(x+1, y-1))f++;
		if(isThere(x, y+1))f++;
		if(isThere(x, y-1))f++;
		if(isThere(x-1, y+1))f++;
		if(isThere(x-1, y))f++;
		if(isThere(x-1, y-1))f++;
		return f;
	}
	
	private boolean isThere(int x, int y){
		if(x<0)x = q-1;
		if(y<0)y = q-1;
		if(x>=q)x = 0;
		if(y>=q)y = 0;
		return array[x][y];
	}
	
	private boolean takeBit(byte b, int pos){
		return (b & (1<<pos)) != 0;
	}
	
	//System to generate certain additional randomness
	private int traverse(int i){
		int x = i;
		i = 0;
		int t = 1;
		for (int y = 0; y < q; y++) {
			if(array[x][y])
				i+=t;
			t++;
			x++;
			if(x>=q)x = 0;
		}
		return i;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Loop for Super-Random: call once per doLoop()
	 */
	private void srDoLoop(){
		memPos++;
		if(memPos>=mem.length)memPos = 0;
		int k = (mem[memPos] & 0xf);
		memPos++;
		if(memPos>=mem.length)memPos = 0;
		int l = mem[memPos];
		if(l<0) l *= -1;
		
		if(k == 0)return;
		switch (k) {
		case 1:
			invertLine(l%q);
			break;
		case 2:
			invertColum(l%q);
			break;
		case 3:
			moveLine(l%q);
			break;
		case 4:
			moveColum(l%q);
			break;

		default:
			break;
		}
		
	}
	
	private void invertLine(int x){
		for (int y = 0; y < q; y++) {
			array[x][y] = !array[x][y];
		}
		System.out.println("invert L"+x);
	}
	
	private void invertColum(int y){
		for (int x = 0; x < q; x++) {
			array[x][y] = !array[x][y];
		}
		System.out.println("invert C"+y);
	}
	
	private void moveLine(int x){
		boolean b = array[x][0];
		for (int y = 0; y < q-1; y++) {
			array[x][y] = array[x][y+1];
		}
		array[x][q-1] = b;
		System.out.println("move L"+x);
	}
	
	private void moveColum(int y){
		boolean b = array[0][y];
		for (int x = 0; x < q-1; x++) {
			array[x][y] = array[x+1][y];
		}
		array[q-1][y] = b;
		System.out.println("move C"+y);
	}
}
