package crypto;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Semaphore;

import cryptoUtility.Random;

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
	 * @version 0.2.1
	 */
	
	public final int size;
	
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
	
	
	//The digesting array, XORed with the bit-Arrays to produce the hash
	private byte[] digest;
	private final int digestBlockSize;
	
	//digest() was called, the hash is final
	private boolean isFinal;
	
	public static final int SCMHA_1024 = 1024;
	public static final int SCMHA_768 = 768;
	public static final int SCMHA_512 = 512;
	
	/**
	 * Generates a Hash by the SCMH-Algorithm ( (C) Sven T. Schneider)
	 * Call the update(byte[])-Method to digest bytes into the Hash
	 * The getState()-Method return the current state of the Hash
	 * During operation you can call reset() to reset the Algorithm to the start!
	 * To finish operation, call digest(). This runs the cycle one final time and adds the destructors.
	 * After that is done, the final Hash is given to you (or can be retrived by calling getstate()).
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
		digestBlockSize = size/24;
		
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
		
		addToMainField(Random.generateSeed(256, 10), e);
		
		digest = new byte[size/8];
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
			byte[] bToUse = new byte[size/8];
			for (int i = 0; i < bToUse.length; i++) {
				bToUse[i] = b[pos];
				pos++;
				if(pos>=b.length)break;
			}
			
			//Pad Message
			bToUse = paddIncomingMessage(bToUse);
			
			//Perform 10 Rounds
			for (int i = 0; i < 10; i++) {
				doRound();
			}
		}
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
		//Play Game of Life 8.times
		playGOLwithE(8);
		
		//2nd Step: XOR E with B
		count_posInRound = 3;
		doWait();
		xOrArrays(e, b);
		count_posInRound = 4;
		//Play Game of Life 8.times
		playGOLwithE(8);
		
		//3rd XOR current Digesting-Array-Part with B
		count_posInRound = 5;
		doWait();
		//TODO
		
		//4th Step: XOR E with C
		count_posInRound = 6;
		doWait();
		xOrArrays(e, c);
		count_posInRound = 7;
		//Play Game of Life 8.times
		playGOLwithE(8);
		
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
		//TODO
		
		//8th Step: XOR E with U
		count_posInRound = 11;
		doWait();
		xOrArrays(e, u);
		
		//9th Step: Fill Digesting-Array with U ---TODO add to flowchat
		count_posInRound = 12;
		doWait();
		//TODO
		
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
			//TODO
			
			//Increment counter
			count_gol++;
		}
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
	 * Inverts the Array b
	 * @param b
	 */
	private void invertArray(boolean[][] b){
		for (int i = 0; i < q; i++) {
			for (int j = 0; j < q; j++) {
				b[i][j] = !b[i][j];
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
					if(y>=q)y = 0;
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
	 * Paints a visualisation of the Algorithms current state
	 */
	public BufferedImage testPaint(int k, Color c, BufferedImage i){
		BufferedImage ima = new BufferedImage(q*k*6+70, q*k+400, BufferedImage.TYPE_INT_ARGB);
		Graphics g = ima.getGraphics();
		
		drawArray(g, 0, k, c, a);
		drawArray(g, k*q, k, c, b);
		drawArray(g, k*q*2, k, c, this.c);
		drawArray(g, k*q*3, k, c, d);
		drawArray(g, k*q*4, k, c, e);
		drawArray(g, k*q*5+10, k, c, u);
		
		g.drawImage(i, 0, q*k+10, null);
		g.setColor(Color.red);
		g.drawRect(0, q*k+10+20*count_posInRound, 300, 20);
		
		
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
