package network;

import cryptoUtility.NetEncryptionFrame;

public abstract class CommunicationProcess {

	protected final Writable linker;
	protected final NetEncryptionFrame key;
	
	private CommunicationProcess next;
	
	protected boolean terminated;
	
	public final String clientName;
	
	public CommunicationProcess(Writable l, NetEncryptionFrame n, String name){
		linker = l;
		terminated = false;
		key = n;
		clientName = name;
	}
	
	/**
	 * Adds upper Component to process Strings
	 * @param n upper Component
	 */
	public void add(CommunicationProcess n) {
		if(next == null){
			next = n;
		}else{
			next.add(n);
		}
	}

	
	public CommunicationProcess sortAll(){
		if(next != null)
			next = next.sortAll();
		
		return sort();
	}
	
	public CommunicationProcess sort(){
		if(terminated)
			return next;
		return this;
	}
	
	/**
	 * @param s the string to process
	 * @return String was processed by upper units and dosn't need to be processed any more
	 */
	public boolean processString(String s){
		if(next!=null)
			if(next.hasTerminated())
				next = next.sort();
		
		if(next != null){
			if(next.processString(s))
				return true;
		}
		return processIntern(s);
	}
	
	/**
	 * @param s the string to process
	 * @return String was processed by this unit and dosn't need to be processed any more
	 */
	protected abstract boolean processIntern(String s);
	
	public boolean hasTerminated(){
		return terminated;
	}
	
}
