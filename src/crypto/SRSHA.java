package crypto;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class SRSHA{
	/**
	 * The SuperRandomSecureHashingAlgorithm was considered and created by Sven T. Schneider (Nuernberg, Germany)
	 * It was first published on the 18. April 2017 on github.com/Tynogc.
	 * The code is under the GNU-General-Public-License v3.0
	 * Your free to use the code in your application, as long as the original Creator is marked.
	 * 
	 * The basic principle is based on John Horton Conway's "Game of Life" 
	 * @author Sven T. Schneider
	 * @version 0.2.1
	 */
	
	public final int size;
	
	//Size of Array: sqrt(size) or sqrt(size)+1
	private final int q;
	private boolean[][] array;
	private boolean[][] arrayInactiv;
	
	public static final int SRSHA_64 = 64;
	public static final int SRSHA_128 = 128;
	public static final int SRSHA_256 = 256;
	public static final int SRSHA_512 = 512;
	public static final int SRSHA_1024 = 1024;
	
	//Never use if False! For Debug reason only!!!
	private static final boolean DO_COMPLEX = true;
	
	//Super destructors, mem gets XORed once per Cycle in an diagonal Pattern
	private int sum;
	private final int DESTRUCTOR_MEMORY_SIZE;
	private byte[] mem;
	private int memPos;
	//Cycles per update
	private final int CYCLES;
	private int cycleCounter;
	
	//Array for digesting operation, is filled 6 times per cycle
	private final int DIGESTING_ARRAY_SIZE;
	private int[] dig;
	private int digPos;
	
	//Only switch off for debug reasons!
	private boolean doCycleAutomatic = true;
	
	//Stops changing to the Hash
	private boolean isFinal;
	
	/**
	 * Generates a Hash by the SRSH-Algorithm ( (C) Sven T. Schneider)
	 * Call the update(byte[])-Method to digest bytes into the Hash
	 * The getState()-Method return the current state of the Hash
	 * During operation you can call reset() to reset the Algorithm to the start!
	 * To finish operation, call digest(). This runs the cycle one final time and adds the destructors.
	 * After that is done, the final Hash is given to you (or can be retrived by calling getstate()).
	 * @param size Bit-length of the Hash, use the Provided Variables:
	 * 		SRSHA.SRSHA_64 - SRSHA.SRSHA_1024 
	 * to ensure compatibility. (It should work with any other size as well)
	 */
	public SRSHA(final int size){
		this.size = size;
		int qs = (int) Math.sqrt(size);
		if(qs*qs < size)qs++;
		q = qs;
		isFinal = false;
		
		CYCLES = size/32+7;
		
		if(size/16<CYCLES)
			DESTRUCTOR_MEMORY_SIZE = size/16;//(8bit XOr with each other) use 1 per cycle
		else
			DESTRUCTOR_MEMORY_SIZE = CYCLES-1;//Should be smaller than cycles
		
		if((size/16)%2==0)
			DIGESTING_ARRAY_SIZE  = size/16-1;//(16bit XOr with hash) fill 6 per Cycle
		else
			DIGESTING_ARRAY_SIZE  = size/16;//must be odd!
		
		reset();
	}
	
	/**
	 * Resets the Algorithm to start
	 */
	public void reset(){
		isFinal = false;
		array = new boolean[q][q];
		arrayInactiv = new boolean[q][q];
		//Super destructors
		sum = 0;
		mem = new byte[DESTRUCTOR_MEMORY_SIZE];
		memPos = 0;
		cycleCounter = 0;
		
		//digesting array
		dig = new int[DIGESTING_ARRAY_SIZE];
		digPos = 0;
	}
	
	/**
	 * Takes the byte Array and digests it into the hash (TODO)
	 * @param b the bytes to digest
	 */
	public void update(byte[] b){
		if(isFinal)
			return;
		
		int pos = 0;
		while(pos<b.length){
			//Split incomming Message into easy to chew chunks
			byte[] bToUse = new byte[size/8];
			for (int i = 0; i < bToUse.length; i++) {
				bToUse[i] = b[pos];
				pos++;
				if(pos>=b.length)break;
			}
			
			//Pad Message
			bToUse = paddIncomingMessage(bToUse);
			
			addToMainField(bToUse);
			if(doCycleAutomatic){
				for (int i = 0; i < CYCLES; i++) {
					doLoopIntern();
				}
			}
		}
	}
	
	/**
	 * XORs the byte-Array to the boolean-field
	 * @param b
	 */
	private void addToMainField(byte[] b){
		int x = 0;
		int y = 0;
		for (int i = 0; i < b.length; i++) {
			//fill mem
			mem[memPos] = (byte)(b[i] ^ mem[memPos]);
			memPos++;
			if(memPos>= mem.length) memPos = 0;
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
	 * Returns the current hash
	 * @return current hash
	 */
	public byte[] getState(){
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
		
		//Mix in mem.
		int c = (sum+mem[0]+dig[1]+mem[2])%dig.length;
		if(c<0)c*=-1;
		for (int i = 0; i < ret1.length; i++) {
			if(i%2==0){
				ret1[i] = (byte)(ret1[i] ^ (dig[c]&0xff));
			}else{
				ret1[i] = (byte)(ret1[i] ^ (dig[c]>>8));
				
				c++;
				if(c >= dig.length){
					c = 0;
				}
			}
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
	
	private static final int ZERO_TO_ONE1_Mutation = 2;
	private static final int ZERO_TO_ONE2_Mutation = 3;
	private static final int ONE_TO_ONE1_Mutation = 3;
	private static final int ONE_TO_ONE2_Mutation = 6;
	
	private int ZERO_TO_ONE1;
	private int ZERO_TO_ONE2;
	private int ONE_TO_ONE1;
	private int ONE_TO_ONE2;
	
	//Within one Cycle the original of GameOfLife is played:
	private static final int CONWAY_Start = 4;
	private static final int CONWAY_End = 3;
	
	/**
	 * The Last iteration was the original "Game of Life"
	 */
	public boolean conwayWasPlayed;
	
	/**
	 * Plays One round of the GameOfLife or teh Mutation variant
	 * @param conway use Original
	 */
	private void playOneRound(boolean conway){
		prepareLoop(conway);
		
		boolean[][] b = arrayInactiv;
		for (int i = 0; i < q; i++) {
			for (int j = 0; j < q; j++) {
				int f = count(i, j);
				if(isThere(i, j)){
					if(f == ONE_TO_ONE1 || f == ONE_TO_ONE2)
						b[i][j] = true;
					else
						b[i][j] = false;
				}else{
					if(f == ZERO_TO_ONE1 || f == ZERO_TO_ONE2)
						b[i][j] = true;
					else
						b[i][j] = false;
				}
			}
		}
		arrayInactiv = array;
		array = b;
	}
	
	/**
	 * Sets the Rule-Numbers to the given rule
	 */
	private void prepareLoop(boolean conway){
		if(conway){
			ZERO_TO_ONE1 = ZERO_TO_ONE_Conway;
			ZERO_TO_ONE2 = 100;
			ONE_TO_ONE1 = ONE_TO_ONE1_Conway;
			ONE_TO_ONE2 = ONE_TO_ONE2_Conway;
			conwayWasPlayed = true;
		}else{
			ZERO_TO_ONE1 = ZERO_TO_ONE1_Mutation;
			ZERO_TO_ONE2 = ZERO_TO_ONE2_Mutation;
			ONE_TO_ONE1 = ONE_TO_ONE1_Mutation;
			ONE_TO_ONE2 = ONE_TO_ONE2_Mutation;
			conwayWasPlayed = false;
		}
	}
	
	/**
	 * Determines whether Conways original or the Mutation is played this cycle
	 * Because Conways Game of Life is Non-Reversible, it destroys any possibility of undoing the change
	 * however, it also leads quit fast to a blank chart, thats why the mutation must be called more often
	 */
	private boolean doConway(){
		if(cycleCounter%CYCLES == CYCLES-CONWAY_End){
			return true;
		}
		if(cycleCounter%CYCLES > CONWAY_Start && cycleCounter%CYCLES <= CYCLES-CONWAY_End){
			int lines = 0;
			int conw = q*2;
			conw /= 7;
			for (int i = 0; i < q; i++) {
				if(countLine(i)>=conw)
					lines++;
			}
			if(lines>=conw)
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
		
		//Play one round
		playOneRound(doConway());
		
		//Fill the digesting Array with a "random" number
		for (int i = 0; i < 3; i++) {
			digPos++;
			if(digPos>=dig.length)digPos = 0;
			dig[digPos] = dig[digPos] ^ bitTraverse((sum/q)%q, (sum+(i*q/3))%q);
			
			digPos++;
			if(digPos>=dig.length)digPos = 0;
			dig[digPos] = dig[digPos] ^ bitLine((sum)%q, (sum/q+(i*q/3))%q);
		}
		
	}
	
	/**
	 * Paints the Current state
	 * @param k Size per Pixel
	 * @return {@link BufferedImage} the visualization of the current state
	 */
	public BufferedImage testPaint(int k, Color c){
		if(q*k<140) k = 150/q;
		BufferedImage ima = new BufferedImage(q*k+70, q*k+50, BufferedImage.TYPE_INT_ARGB);
		Graphics g = ima.getGraphics();
		g.setColor(c);
		for (int i = 0; i < q; i++) {
			for (int j = 0; j < q; j++) {
				if(array[i][j])
					g.fillRect(i*k+1, j*k+1, k, k);
			}
		}
		g.setColor(Color.cyan);
		int x = (sum/q)%q;
		int y = (sum)%q;
		x*=k;
		y*=k;
		//Draw filled bits
		g.drawLine(x+k/2, y+k/2, x+k*3, y+k*3);
		g.setColor(Color.blue);
		//DrawdigestingBits diagonal
		for (int i = 0; i < 3; i++) {
			x = (sum/q)%q;
			y = (sum+(i*q/3))%q;
			x*=k;
			y*=k;
			y+=k/2;
			x+=k/2;
			g.drawLine(x, y, x-k*3, y+k*3);
		}
		//DrawdigestingBits linear
		for (int i = 0; i < 3; i++) {
			y = (sum/q +(i*q/3))%q;
			x = (sum)%q;
			x*=k;
			y*=k;
			y+=k/2;
			x+=k/2;
			g.drawLine(x, y, x+30, y);
		}
		
		g.drawRect(0, 0, q*k+1, q*k+1);
		g.fillRect(memPos*20, q*k+20, 20, 15);
		g.setColor(c);
		for (int i = 0; i < mem.length; i++) {
			g.drawString(""+mem[i], i*20, q*k+32);
		}
		g.drawString(sum+"   "+cycleCounter+"   "+countTrue(), 20, q*k+46);
		if(conwayWasPlayed)
			g.drawString("C", 120, q*k+46);
		else
			g.drawString("N", 130, q*k+46);
		
		for (int i = 0; i < dig.length; i++) {
			g.drawString(""+(byte)(dig[i]&0xff), q*k+10, i*10);
			g.drawString(""+(byte)(dig[i]>>8), q*k+40, i*10);
		}
		return ima;
	}
	
	/**
	 * @return true: <b>digest()</b> had been called, Hash can't be changed anymore
	 */
	public boolean isFinal(){
		return isFinal;
	}
	
	/**
	 * Mixes the current state withe the Interpolations and runs the cycle one last time.
	 * Form here on the Hash can't be changed!
	 * @return
	 */
	public byte[] digest(){
		if(isFinal)
			return getState();
		
		//Mix the Memory into the field
		mixInMemory();
		
		//Now its almost done, Conways Original GameOfLife is now played one last cycle,
		//to destroy reversability completly.
		playOneRound(true);
		cycleCounter = CONWAY_Start+1;
		playOneRound(doConway());
		playOneRound(false);
		playOneRound(false);
		
		//We are almost there, destroy dig[] by adding mem[]
		int m = 0;
		for (int i = 0; i < dig.length; i++) {
			int b1 = mem[m];
			m++;
			if(m>=mem.length)m = 0;
			int b2 = mem[m];
			m++;
			if(m>=mem.length)m = 0;
			dig[i] = (dig[i] ^ ((b2<<8)|b1));
		}
		
		isFinal = true;
		return getState();
	}
	
	/**
	 * As soon as digest is called, this Method XORs the mem-Array with the boolean field.
	 * This is an important part, now the whole "History" is needed to be correct.
	 */
	private void mixInMemory(){
		byte[] ret1 = new byte[size/8];
		int c = 0;
		for (int i = 0; i < ret1.length; i++) {
			if(i%2==0){
				ret1[i] = (byte)((dig[c]&0xff));
			}else{
				ret1[i] = (byte)((dig[c]>>8));
				
				c++;
				if(c >= dig.length){
					c = 0;
				}
			}
		}
		addToMainField(ret1);
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
	private int bitTraverse(int x, int y){
		int i = 0;
		int t = 1;
		for (int j = 0; j < 16; j++) {
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
	
	//takes the int in the line
	private int bitLine(int x, int y){
		int r = 0;
		int t = 1;
		for (int i = 0; i < 16; i++) {
			if(array[x][y])
				r+=t;
			t*=2;
			y++;
			if(y>=q){
				y = 0;
				x++;
				if(x>=q)x=0;
			}
		}
		return r;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Loop for Super-Random: call once per doLoop()
	 */
	private void srDoLoop(){
		int r = 0;
		if(cycleCounter>=1)
		do{
			memPos++;
			if(memPos>=mem.length)memPos = 0;
			
			r++;
		}while(mem[memPos]==0 && r<mem.length);
		
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
	 * Padds an incoming Message to the word length, also fills Sum
	 * @param b
	 * @return
	 */
	private byte[] paddIncomingMessage(byte[] b){
		byte[] bNew = new byte[size/8];
		for (int i = 0; i < b.length; i++) {
			bNew[i] = b[i];
			//Fill sum
			int bi = b[i];
			if(bi<0) bi += 256;
			sum+=bi+1;
		}
		if(b.length>=bNew.length)
			return bNew;
		for (int i = b.length; i < bNew.length; i++) {
			if(i == bNew.length-1){
				bNew[i] = (byte)b.length;
			}else{
				bNew[i] = 0;
			}
		}
		return bNew;
	}
	
	//Counts all true bits
	public int countTrue(){
		int k = 0;
		for (int i = 0; i < q; i++) {
			k+=countLine(i);
		}
		return k;
	}
}
