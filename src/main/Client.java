package main;

import network.TCPclient;
import network.UDPsystem;

public class Client {

	private TCPclient tcp;
	private UDPsystem udp;
	
	private int trys;
	private long lastTry;
	private static final int timeForTry = 3000;
}
