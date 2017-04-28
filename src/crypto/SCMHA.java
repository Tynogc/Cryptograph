package crypto;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Semaphore;

public class SCMHA {
	
	/**
	 * The Secure Cellular Mutation Hash Algorithm was considered and created by Sven T. Schneider (Nuernberg, Germany)
	 * The first Consideration was published on the 24. April 2017 on github.com/Tynogc.
	 * The code is under the GNU-General-Public-License v3.0
	 * Your free to use the code in your application, as long as the original Creator(s) is/are marked.
	 * 
	 * It is a modification of my SRSH-Algorithm
	 * 
	 * The basic principle of destroying the Pre-Image is based on John Horton Conway's "Game of Life" 
	 * @author Sven T. Schneider
	 * @version 0.3
	 */
	
	public final int size;
	//Size of one Unit of Message to be Processed
	public final int wordSize;
	
	//Size of Array: sqrt(size) or sqrt(size)+1
	private final int q;
	
	//Basic single-Int destructor, based on the Input;
	private int sum;
	
	//The Playing-Ground Bit-Arrays, store the original 32
	private boolean[][] a;
	private boolean[][] b;
	private boolean[][] c;
	private boolean[][] d;
	private boolean[][] e;
	
	private boolean[][] u;
	
	//Countimg variables
	//Counting Rounds
	private int count_roundsDone;
	//Counter in Round (for debug only)
	private int count_posInRound;
	//Counts the times GameOfLife was called
	private int count_gol;
	
	//How many times GOL is played per Round
	private final int play_gol_per_round;
	
	
	//The digesting array, XORed with the bit-Arrays to produce the hash
	private byte[] digest;
	private final int digestSize;
	
	//digest() was called, the hash is final
	private boolean isFinal;
	
	public static final int SCMHA_1024 = 1024;
	public static final int SCMHA_768 = 768;
	public static final int SCMHA_512 = 512;
	
	/**
	 * Round Constants for B, C, D and E
	 * A Dosn't need one, because it is the original bits of Message.
	 * They are composed of the first Primes mod 256.
	 * If a Playing-Ground can't fit all bits, the less significant ones are ignored. 
	 */
	//Prime 1-16
	public static final byte[] INIT_B = 
			new byte[]{2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53};
	//Prime 17-32
	public static final byte[] INIT_C = 
			new byte[]{59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, -125};
	//Prime 33-48
	public static final byte[] INIT_D = 
			new byte[]{-119, -117, -107, -105, -99, -93, -89, -83, -77, -75, -65, -63, -59, -57, -45, -33};
	//Prime 49-64
	public static final byte[] INIT_E = 
			new byte[]{-29, -27, -23, -17, -15, -5, 1, 7, 13, 15, 21, 25, 27, 37, 51, 55};
	
	/**
	 * Generates a Hash by the SCMH-Algorithm ( (C) Sven T. Schneider)
	 * Call the update(byte[])-Method to digest bytes into the Hash
	 * The getState()-Method return the current state of the Hash
	 * During operation you can call reset() to reset the Algorithm to the start!
	 * To finish operation, call digest(). This runs the cycle one final time and adds the destructors.
	 * After that is done, the final Hash is given to you (or can be retrieved by calling getstate()).
	 * @param s Bit-length of the Hash, use the Provided Variables:
	 * 		SCMHA_512, SCMHA_768, SCMHA_1024
	 * @throws NoSuchAlgorithmException if the choosen bit-Size is not compatible
	 */
	public SCMHA(int s) throws NoSuchAlgorithmException{
		size = s;
		if(size == SCMHA_1024){
			q = 15;
		}else if(size == SCMHA_512){
			q = 11;
		}else if(size == SCMHA_768){
			q = 13;
		}else{
			throw new NoSuchAlgorithmException("This algorithm dosn't exist!");
		}
		
		wordSize = size/16;
		
		digestSize = size/8;
		
		play_gol_per_round = size/16;
		
		reset();
	}
	
	public void reset(){
		sum = 0;
		a = new boolean[q][q];
		b = new boolean[q][q];
		c = new boolean[q][q];
		d = new boolean[q][q];
		e = new boolean[q][q];
		
		u = new boolean[q][q];
		
		//Fill with initialisation
		addToMainField(INIT_B, b);
		addToMainField(INIT_C, c);
		addToMainField(INIT_D, d);
		//addToMainField(INIT_E, e);
		
		digest = new byte[digestSize];
	}
	
	/**
	 * Takes the byte Array and digests it into the hash
	 * @param b the bytes to digest
	 */
	public void update(byte[] b){
		if(b == null)
			return;
		if(isFinal)
			return;
		
		int pos = 0;
		while(pos<b.length){
			//Split incomming Message into easy to chew chunks
			byte[] bToUse = new byte[wordSize];
			for (int i = 0; i < bToUse.length; i++) {
				bToUse[i] = b[pos];
				pos++;
				if(pos>=b.length)break;
			}
			
			//Pad Message
			bToUse = paddIncomingMessage(bToUse);
			
			addToAllFields(bToUse);
			
			//Perform 10 Rounds
			for (int i = 0; i < 10; i++) {
				doRound();
			}
		}
	}
	
	/**
	 * Runns one last 
	 * Form here on the Hash can't be changed!
	 * @return
	 */
	public byte[] digest(){
		if(isFinal)
			return getState();
		int x = 0;
		int y = 0;
		int f = 0;
		boolean[][] array = a;
		for (int i = 0; i < digest.length; i++) {
			int t1 = 0;
			int t2 = 1;
			for (int j = 0; j < 8; j++) {
				if(array[x][y])
					t1+=t2;
				t2*=2;
				x++;
				if(x>=q){
					y++;
					x = 0;
					if(y>=q){
						y = 0;
						f++;
						if(f==1)array = this.b;
						if(f==2)array = this.c;
						if(f==3)array = this.d;
						if(f==4)array = this.e;
					}
				}
			}
			digest[i] = (byte)(digest[i] ^ (byte)t1);
		}
		
		isFinal = true;
		return getState();
	}
	
	/**
	 * @return true: <b>digest()</b> had been called, Hash can't be changed anymore
	 */
	public boolean isFinal(){
		return isFinal;
	}
	
	/**
	 * Returns the current hash
	 * @return current hash
	 */
	public byte[] getState(){
		return digest.clone();
	}
	////////////////////////////////////////////////////////////////////////////////////////////
	//The Loop
	////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Performs one iteration of the Hash-Function, a complet Hash generation needs at least 10 Rounds.
	 * A Flowchat of the Process is under res/SCMHA_Layout.jpg
	 */
	private void doRound(){
		//reste local counters
		count_gol = 0;
		
		//1st step: XOR E with A
		count_posInRound = 1;
		doWait();
		xOrArrays(e, a);
		count_posInRound = 2;
		//Play Game of Life
		playGOLwithE(play_gol_per_round/4);
		
		//2nd Step: XOR E with B
		count_posInRound = 3;
		doWait();
		xOrArrays(e, b);
		count_posInRound = 4;
		//Play Game of Life
		playGOLwithE(play_gol_per_round/4);
		
		//3rd XOR current Digesting-Array-Part with B
		count_posInRound = 5;
		doWait();
		//Create an Array to add...
		byte[] digRMA = new byte[q*q/8+1];
		for (int i = 0; i < digRMA.length; i++) {
			digRMA[i] = digest[position_digest(-i-2)];
		}
		//And add it to B
		addToMainField(digRMA, b);
		
		//4th Step: XOR E with C
		count_posInRound = 6;
		doWait();
		xOrArrays(e, c);
		count_posInRound = 7;
		//Play Game of Life
		playGOLwithE(play_gol_per_round/4);
		
		//5th Step: generate U
		count_posInRound = 8;
		doWait();
		mixACDinU();
		
		//6th Step: Play GOL with U 3.times
		count_posInRound = 9;
		for (int i = 0; i < 3; i++) {
			doWait();
			u = playOneRound(canDoConway(u), u);
		}
		
		//7th Step: Rotate U and B
		count_posInRound = 10;
		doWait();
		//The amount of Rotation
		u = rotateBy(u, q*q-position_Rotate());
		b = rotateBy(b, position_Rotate());
		
		//8th Step: XOR E with U
		count_posInRound = 11;
		doWait();
		xOrArrays(e, u);
		
		//9th Step: Fill Digesting-Array with U
		count_posInRound = 12;
		//Same as playGOLwithE() but with u
		for (int i = 0; i < play_gol_per_round/4; i++) {
			doWait();
			
			u = playOneRound(canDoConway(u), u);
			
			addLineToDigest(u);
			
			count_gol++;
		}
		
		//10th Step: Move Arrays
		count_posInRound = 13;
		doWait();
		boolean[][] x = e;
		e = d;
		d = c;
		c = b;
		b = a;
		a = x;
		
		//And done!
		count_roundsDone++;
	}
	
	private void playGOLwithE(int times){
		for (int i = 0; i < times; i++) {
			doWait();
			//Play the Game
			e = playOneRound(canDoConway(e), e);
			
			//Adds psudo random Line to Digesting-Array
			addLineToDigest(e);
			
			//Increment counter
			count_gol++;
		}
	}
	
	/**
	 * Takes a Line of the Array and adds it to the digesting array (mod 0xffff).
	 * This is the "red +" operator in the Flowchart
	 * @param b
	 */
	private void addLineToDigest(boolean[][] b){
		int t = bitLine(position_ArrayToDigestX(), position_ArrayToDigestY(), b);
		int u = ((digest[position_digest(0)] & 0xff) << 8) | ((digest[position_digest(1)] & 0xff));
		
		t = (t+u)%0xffff;
		
		digest[position_digest(0)] = (byte)(t & 0xff);
		digest[position_digest(1)] = (byte)((t >> 8) & 0xff);
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	//Position Methods
	////////////////////////////////////////////////////////////////////////////////////////////
	
	//Get the Current X-Position for the bitLine-Method in addToDigest
	private int position_ArrayToDigestX(){
		return sum%q;
	}
	
	//Get the Current Y-Position for the bitLine-Method in addToDigest
	private int position_ArrayToDigestY(){
		return (count_gol + count_gol*q/3 + sum)%q;
	}
	
	//Returns the current starting position in the Digesting-Array
	private int position_digest(int add){
		int r = count_gol*2+sum*count_roundsDone+add;
		if(r<0)
			r+=digestSize;
		return r%digestSize;
	}
	
	//Returns the determined rotation Value
	private int position_Rotate(){
		int r = sum;
		for (int i = 0; i < 5; i++) {
			int b = digest[position_digest(-i-2)];
			if(b<0)b+=256;
			r+=b;
		}
		return r%(q*q);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	//Game of Life Methods:
	////////////////////////////////////////////////////////////////////////////////////////////
	
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
	
	/**
	 * The Last iteration was the original "Game of Life"
	 */
	public boolean conwayWasPlayed;
	
	/**
	 * Plays One round of the GameOfLife or the Mutation variant
	 * @param conway use Original Rule-Set
	 * @param bOld the Playing ground
	 * @return the mutated State
	 */
	private boolean[][] playOneRound(boolean conway, boolean[][] bOld){
		prepareLoop(conway);
		
		boolean[][] b = new boolean[q][q];
		for (int i = 0; i < q; i++) {
			for (int j = 0; j < q; j++) {
				int f = count(i, j, bOld);
				if(isThere(i, j, bOld)){
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
		return b;
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
	
	private boolean canDoConway(boolean[][] b){
		return countTrue(b)>=(q*q)/4;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	//Support-Methods:
	////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Counts the surrounding fields
	 * @param x
	 * @param y
	 * @return
	 */
	private int count(int x, int y, boolean[][] b){
		int f = 0;
		if(isThere(x+1, y+1, b))f++;
		if(isThere(x+1, y, b))f++;
		if(isThere(x+1, y-1, b))f++;
		if(isThere(x, y+1, b))f++;
		if(isThere(x, y-1, b))f++;
		if(isThere(x-1, y+1, b))f++;
		if(isThere(x-1, y, b))f++;
		if(isThere(x-1, y-1, b))f++;
		return f;
	}
	
	private boolean isThere(int x, int y, boolean[][] b){
		if(x<0)x = q-1;
		if(y<0)y = q-1;
		if(x>=q)x = 0;
		if(y>=q)y = 0;
		return b[x][y];
	}
	
	/**
	 * XORs the Array <b>or</b> into the Array <b>b</b>, or stays unaffected.
	 * @param b
	 * @param or
	 */
	private void xOrArrays(boolean[][] b, boolean[][] or){
		for (int i = 0; i < q; i++) {
			for (int j = 0; j < q; j++) {
				b[i][j] = b[i][j] ^ or[i][j];
			}
		}
	}
	
	/**
	 * Mixes the channels A, C and D in preparation for mixing with E
	 * The pattern is: U = (A and D) xor (C and ~D);
	 * Puts the values in the array u
	 */
	private void mixACDinU(){
		for (int i = 0; i < q; i++) {
			for (int j = 0; j < q; j++) {
				u[i][j] = (a[i][j] && d[i][j]) ^ (c[i][j] && (!d[i][j]));
			}
		}
	}
	
	/**
	 * Padds the byte[] b to the specified length
	 * @param b
	 * @return
	 */
	private byte[] paddIncomingMessage(byte[] b){
		byte[] bNew = new byte[wordSize*2+4];
		for (int i = 0; i < b.length; i++) {
			bNew[i] = b[i];
			//Fill sum
			int bi = b[i];
			if(bi<0) bi += 256;
			sum+=bi+1;
		}
		bNew[wordSize] = (byte)b.length;
		bNew[wordSize+1] = (byte)(b.length/3);
		for (int i = 0; i < b.length; i++) {
			bNew[i+wordSize+2] = b[i];
		}
		bNew[wordSize*2+2] = (byte)b.length;
		bNew[wordSize*2+3] = (byte)(b.length/3);
		return bNew;
	}
	
	/**
	 * XORs the byte-Array to the boolean-field
	 * @param b
	 */
	private void addToMainField(byte[] b, boolean[][] array){
		int x = 0;
		int y = 0;
		for (int i = 0; i < b.length; i++) {
			//Fill array
			for (int j = 0; j < 8; j++) {
				array[x][y] = (array[x][y] ^ takeBit(b[i], j));
				x++;
				if(x>=q){
					y++;
					x = 0;
					if(y>=q)return;
				}
			}
		}
	}
	
	/**
	 * XORs the byte-Array to all boolean-fields
	 * @param b
	 */
	private void addToAllFields(byte[] b){
		boolean[][] array = a;
		int f = 0;
		int x = 0;
		int y = 0;
		for (int i = 0; i < b.length; i++) {
			//Fill array
			for (int j = 0; j < 8; j++) {
				array[x][y] = (array[x][y] ^ takeBit(b[i], j));
				x++;
				if(x>=q){
					y++;
					x = 0;
					if(y>=q){
						y = 0;
						f++;
						if(f==1)array = this.b;
						if(f==2)array = this.c;
						if(f==3)array = this.d;
						if(f==4)array = this.e;
					}
				}
			}
		}
	}
	
	private boolean takeBit(byte b, int pos){
		return (b & (1<<pos)) != 0;
	}
	
	private int countTrue(boolean[][] b){
		int z = 0;
		for (int i = 0; i < q; i++) {
			for (int j = 0; j < q; j++) {
				if(b[i][j])
					z++;
			}
		}
		return z;
	}
	
	//takes the int in the line
	private int bitLine(int x, int y, boolean[][] array){
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
	
	/**
	 * Performs an Rotate <<< Operator on the given array
	 */
	private boolean[][] rotateBy(boolean[][] b, int ammount){
		boolean[][] bNew = new boolean[q][q];
		
		int t;
		int x;
		int y;
		for (int i = 0; i < q; i++) {
			for (int j = 0; j < q; j++) {
				t = i+j*q+ammount;
				x = t%q;
				y = (t/q)%q;
				bNew[x][y] = b[i][j];
			}
		}
		
		return bNew;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	//For Debug only:
	////////////////////////////////////////////////////////////////////////////////////////////
	
	private boolean doCircleAutomatic = true;
	private Semaphore sema;
	
	private void doWait(){
		if(doCircleAutomatic)return;

		if(sema == null)sema = new Semaphore(1);
		while (!sema.tryAcquire()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * For debug only, let the Thread wait after every iteration...
	 * @deprecated for debug only
	 * @param atomatic
	 */
	public void doCircle(boolean atomatic){
		doCircleAutomatic = atomatic;
		if(sema == null && !atomatic){
			sema = new Semaphore(1);
			sema.tryAcquire();
		}
	}
	
	/**
	 * For debug only, let the Thread wait after every iteration...
	 * @deprecated for debug only
	 */
	public void goOn(){
		if(sema == null)sema = new Semaphore(1);
		sema.release();
	}
	
	/**
	 * Paints a visualization of the Algorithms current state
	 */
	public BufferedImage testPaint(int k, Color c, BufferedImage i){
		BufferedImage ima = new BufferedImage(q*k*6+70, q*k+400, BufferedImage.TYPE_INT_ARGB);
		Graphics g = ima.getGraphics();
		
		drawArray(g, 0, k, c, a);
		drawArray(g, k*q, k, c, b);
		drawArray(g, k*q*2, k, c, this.c);
		drawArray(g, k*q*3, k, c, d);
		drawArray(g, k*q*4, k, c, e);
		if(count_posInRound > 8 && count_posInRound < 13)
			drawArray(g, k*q*5+20, k, c, u);
		
		g.drawImage(i, 0, q*k+10, null);
		g.setColor(Color.red);
		g.drawRect(0, q*k+10+20*count_posInRound, 300, 20);
		
		if(count_posInRound == 1)
			g.drawString("E = E ^ A", 200, q*k+25+20*count_posInRound);
		else if(count_posInRound == 3)
			g.drawString("E = E ^ B", 200, q*k+25+20*count_posInRound);
		else if(count_posInRound == 5)
			g.drawString("B = B + Dig", 200, q*k+25+20*count_posInRound);
		else if(count_posInRound == 6)
			g.drawString("E = E ^ C", 200, q*k+25+20*count_posInRound);
		else if(count_posInRound == 8)
			g.drawString("U = (A & D)^(C & ~D)", 180, q*k+25+20*count_posInRound);
		else if(count_posInRound == 11)
			g.drawString("E = E ^ U", 200, q*k+25+20*count_posInRound);
		else if(count_posInRound == 13)
			g.drawString("Move > Arrays", 200, q*k+25+20*count_posInRound);
		else if(count_posInRound == 10)
			g.drawString("B<<<"+position_Rotate(), 200, q*k+25+20*count_posInRound);
		else if(conwayWasPlayed)
			g.drawString("Played Conway", 200, q*k+25+20*count_posInRound);
		else
			g.drawString("Played Mutation", 200, q*k+25+20*count_posInRound);
		
		g.setColor(Color.white);
		int x = position_ArrayToDigestX()*k;
		int y = position_ArrayToDigestY()*k;
		if(count_posInRound<9)
			x+=4*q*k;
		else
			x+=5*q*k+20;
		g.drawLine(x, y+k/2, x+30, y+k/2);
		
		return ima;
	}
	
	private void drawArray(Graphics g, int x, int k, Color c, boolean[][] array){
		g.setColor(c);
		for (int i = 0; i < q; i++) {
			for (int j = 0; j < q; j++) {
				if(array[i][j])
					g.fillRect(x+i*k+1, j*k+1, k, k);
			}
		}
		g.setColor(Color.blue);
		g.drawRect(x, 0, q*k,q*k);
	}
	
}
