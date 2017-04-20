package crypto;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class SRSHA{
	/**
	 * The SuperRandomSecureHashingAlgorithm was considered and created by Sven T. Schneider (Nuernberg, Germany)
	 * It was first published on the 18. April 2017 on github.com/Tynogc.
	 * The code lies under the GNU-General-Public-License v3.0
	 * Your free to use the code in your application, as long as the original Creator is marked.
	 * 
	 * The basic principle is based on John Horton Conway's "Game of Life" 
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
	private static final int CYCLES = 23;
	private int cycleCounter;
	
	//Array for digesting operation
	private int[] dig;
	private int digPos;
	
	//Only switch off for debug reasons!
	private boolean doCycleAutomatic = true;
	
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
		
		//digesting array
		dig = new int[CYCLES*3];
		digPos = 0;
	}
	
	/**
	 * Takes the byte Array and digests it into the hash (TODO)
	 * @param b the bytes to digest
	 */
	public void update(byte[] b){
		if(isFinal)
			return;
		
		//Pad Message
		b = paddIncomingMessage(b);
		
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
		if(doCycleAutomatic){
			for (int i = 0; i < CYCLES; i++) {
				doLoopIntern();
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
		
		if(cycleCounter==0)return ret1;
		
		int c = 0;
		for (int i = 0; i < ret1.length; i++) {
			ret1[i] = (byte)(ret1[i] ^ dig[c]);
			
			c++;
			if(c >= dig.length)c = 0;
		}
		
		return ret1;
	}
	
	/**
	 * Dose one Iteration over the current state
	 * @deprecated Only for debug reasons! Should not be used during normal operation!
	 */
	public void doLoop(){
		doLoopIntern();
	}
	
	/**
	 * Stops the automatic processing of doLoop()
	 * @deprecated Only for debug reasons! Should not be used during normal operation!
	 */
	public void noAutomaticLoop(){
		doCycleAutomatic = false;
	}
	
	private static final int ZERO_TO_ONE_Conway = 3;
	private static final int ONE_TO_ONE1_Conway = 2;
	private static final int ONE_TO_ONE2_Conway = 3;
	
	private static final int ZERO_TO_ONE_Mutation = 2;
	private static final int ONE_TO_ONE1_Mutation = 3;
	private static final int ONE_TO_ONE2_Mutation = 6;
	
	private int ZERO_TO_ONE;
	private int ONE_TO_ONE1;
	private int ONE_TO_ONE2;
	
	//Within one Cycle the original of GameOfLife is played:
	private static final int CONWAY_Start = 4;
	private static final int CONWAY_End = CYCLES-3;
	
	/**
	 * The Last iteration was the original "Game of Life"
	 */
	public boolean conwayWasPlayed;
	
	/**
	 * Determines whether Conways original or the Mutation is played this cycle
	 * Because Conways Game of Life is Non-Reversible, it destroys any possibility of undoing the change
	 * however, it also leads quit fast to a blank chart, thats why the mutation must be called more often
	 */
	private void prepareLoop(){
		if(doConway()){
			ZERO_TO_ONE = ZERO_TO_ONE_Conway;
			ONE_TO_ONE1 = ONE_TO_ONE1_Conway;
			ONE_TO_ONE2 = ONE_TO_ONE2_Conway;
			conwayWasPlayed = true;
		}else{
			ZERO_TO_ONE = ZERO_TO_ONE_Mutation;
			ONE_TO_ONE1 = ONE_TO_ONE1_Mutation;
			ONE_TO_ONE2 = ONE_TO_ONE2_Mutation;
			conwayWasPlayed = false;
		}
	}
	
	private boolean doConway(){
		if(cycleCounter%CYCLES > CONWAY_Start && cycleCounter%CYCLES <= CONWAY_End){
			int lines = 0;
			for (int i = 0; i < q; i++) {
				if(countLine(i)>=q/4)
					lines++;
			}
			if(lines>=q/4)
				return true;
		}
		return false;
	}
	
	/**
	 * Plays one iteration of GameOfLife or the mutation variant.
	 * Also calls the Super_Random_Methode
	 */
	private void doLoopIntern(){
		if(isFinal)
			return;
		cycleCounter++;
		
		//Super Random Loop
		if(DO_COMPLEX)
			srDoLoop();
		
		prepareLoop();
		
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
		
		//Fill the digesting Array with a "random" number
		for (int i = 0; i < 3; i++) {
			digPos++;
			if(digPos>=dig.length)digPos = 0;
			dig[digPos] = dig[digPos] ^ traverse((sum/q)%q, (sum+(i*q/3))%q);
		}
		
	}
	
	/**
	 * Paints the Current state
	 * @param k Size per Pixel
	 * @return {@link BufferedImage} the visualization of the current state
	 */
	public BufferedImage testPaint(int k, Color c){
		BufferedImage ima = new BufferedImage(q*k+2, q*k+50, BufferedImage.TYPE_INT_ARGB);
		Graphics g = ima.getGraphics();
		g.setColor(c);
		for (int i = 0; i < q; i++) {
			for (int j = 0; j < q; j++) {
				if(array[i][j])
					g.fillRect(i*k+1, j*k+1, k, k);
			}
		}
		g.setColor(Color.cyan);
		for (int i = 0; i < 3; i++) {
			int x = (sum/q)%q;
			int y = (sum+(i*q/3))%q;
			x*=k;
			y*=k;
			if(i!=0)
				g.setColor(Color.blue);
			g.drawLine(x+k/2, y+k/2, x+k*3, y+k*3);
		}
		
		g.drawRect(0, 0, q*k+1, q*k+1);
		g.fillRect(memPos*20, q*k+20, 20, 15);
		g.setColor(c);
		for (int i = 0; i < mem.length; i++) {
			g.drawString(""+mem[i], i*20, q*k+32);
		}
		g.drawString(sum+"   "+cycleCounter, 20, q*k+46);
		if(conwayWasPlayed)
			g.drawString("C", 120, q*k+46);
		else
			g.drawString("N", 130, q*k+46);
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
	
	//Goes through in an inverse diagonal pattern
	private int traverse(int x, int y){
		int i = 0;
		int t = 1;
		for (int j = 0; j < 8; j++) {
			if(isThere(x, y))
				i+=t;
			t*=2;
			x--;
			y++;
			if(y>=q)y = 0;
			if(x<0)x = q-1;
		}
		return i;
	}
	
	//counts the bits in the line
	private int countLine(int l){
		int r = 0;
		for (int i = 0; i < q; i++) {
			if(array[l][i])
				r++;
		}
		return r;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Loop for Super-Random: call once per doLoop()
	 */
	private void srDoLoop(){
		
		if(cycleCounter>=1)
		do{
			memPos++;
			if(memPos>=mem.length)memPos = 0;
		}while(mem[memPos]==0);
		
		fillTraverse((sum/q)%q, sum%q, mem[memPos]);
		sum++;
	}
	
	//XORs the byte at the given Position in a diagonal pattern
	private void fillTraverse(int x, int y, byte b){
		for (int i = 0; i < 8; i++) {
			array[x][y] = (array[x][y] ^ takeBit(b, i));
			x++;
			y++;
			if(x>=q)x = 0;
			if(y>=q)y = 0;
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Padds an incoming Message to the word length
	 * @param b
	 * @return
	 */
	private byte[] paddIncomingMessage(byte[] b){
		byte[] bNew = new byte[size];
		for (int i = 0; i < b.length; i++) {
			bNew[i] = b[i];
		}
		for (int i = b.length; i < bNew.length; i++) {
			if(i == bNew.length-1){
				bNew[i] = (byte)b.length;
			}else{
				bNew[i] = 0;
			}
		}
		return bNew;
	}
}
