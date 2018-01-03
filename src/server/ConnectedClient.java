package server;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents a connected client.
 * 
 * @author Yohan Chalier
 *
 */
public class ConnectedClient {
	
	private int id;
	private String MACAddress;
	private String InetAddress;
	private String os;
	
	
	private Queue<String> cmdQueue; // File structure FIFO
	
	public ConnectedClient(int id) {
		this.id = id;
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
	
	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
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
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id\t" + id + "\n");
		sb.append("MAC\t" + MACAddress + "\n");
		sb.append("IP\t" + InetAddress + "\n");
		sb.append("os\t" + os);
		return sb.toString();
	}

}
