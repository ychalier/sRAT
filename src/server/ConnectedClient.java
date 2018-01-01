package server;

import java.util.LinkedList;
import java.util.Queue;

public class ConnectedClient {
	
	private String MACAddress;
	private String InetAddress;
	private int id;
	
	private Queue<String> cmdQueue;
	
	public ConnectedClient() {
		cmdQueue = new LinkedList<String>();
	}
	
	public String getMACAddress() {
		return MACAddress;
	}

	public void setMACAddress(String mACAddress) {
		MACAddress = mACAddress;
	}

	public String getInetAddress() {
		return InetAddress;
	}

	public void setInetAddress(String inetAddress) {
		InetAddress = inetAddress;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void stackCmd(String cmd) {
		cmdQueue.add(cmd);
	}
	
	public String popCmd() {
		return cmdQueue.poll();
	}
	
	public boolean hasCmd() {
		return !cmdQueue.isEmpty();
	}

}
