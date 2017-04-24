package crypto;

import java.security.NoSuchAlgorithmException;

public class SCMHA {
	
	/**
	 * The Secure Cellular Mutation Hash Algorithm was considered and created by Sven T. Schneider (Nuernberg, Germany)
	 * The first Consideration was published on the 24. April 2017 on github.com/Tynogc.
	 * The code is under the GNU-General-Public-License v3.0
	 * Your free to use the code in your application, as long as the original Creator is marked.
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
	
	
	//The digesting array, XORed with the bit-Arrays to produce the hash
	private byte[] digest;
	
	public static final int SCMHA_1024 = 1024;
	public static final int SCMHA_768 = 768;
	public static final int SCMHA_512 = 512;
	
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
	}
	
	public void reset(){
		sum = 0;
		a = new boolean[q][q];
		b = new boolean[q][q];
		c = new boolean[q][q];
		d = new boolean[q][q];
		e = new boolean[q][q];
		
		digest = new byte[size/8];
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
	
	//Within one Cycle the original of GameOfLife is played:
	private static final int CONWAY_Start = 4;
	private static final int CONWAY_End = 3;
	
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
	
	////////////////////////////////////////////////////////////////////////////////////////////
	//For Debug only:
	////////////////////////////////////////////////////////////////////////////////////////////
	
	private boolean doCircleAutomatic = true;
	
	private void doWait(){
		if(doCircleAutomatic)return;
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * For debug only, let the Thread wait after every iteration...
	 * @deprecated for debug only
	 * @param atomatic
	 */
	public void doCircle(boolean atomatic){
		doCircleAutomatic = atomatic;
	}

}
