package web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class Server extends ServerSocket {
	
	private static final int DEFAULT_BACKLOG = 0;
	private static final int DEFAULT_PORT = 80;
	
	public Server()
			throws UnknownHostException, IOException {
		this(selectPort());
	}
	
	public Server(int port)
			throws UnknownHostException, IOException{
		this(port, selectBindAddress());
	}
	
	public Server(int port, InetAddress bindAddr)
			throws IOException {
		super(port, DEFAULT_BACKLOG, bindAddr);
		System.out.println("Server hosted on "
						   + bindAddr.getHostAddress()
						   + ":" + port);
	}

	public void run() throws IOException {
		
		while (true) {
	    	
	    	Socket socket = accept();
	    	BufferedReader reader = new BufferedReader(
	    			new InputStreamReader(socket.getInputStream()));
			String request = reader.readLine();			
			System.out.println(request);
			
			String httpResponse = "HTTP/1.1 200 OK\r\n\r\n";
			
			socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
			socket.close();
	
	    }
	}
	
	private static int selectPort() {
		return DEFAULT_PORT;
	}
	
	private static InetAddress selectBindAddress()
			throws UnknownHostException {
		
	    try {
	        InetAddress candidateAddress = null;
	        
	        for (Enumeration<NetworkInterface> ifaces =
	        		NetworkInterface.getNetworkInterfaces();
	        		ifaces.hasMoreElements();) {
	        	
	            NetworkInterface iface =
	            		(NetworkInterface) ifaces.nextElement();
	            
	            if (!iface.toString().contains("VirtualBox")) {
	            	
	            	for (Enumeration<InetAddress> inetAddrs =
	            			iface.getInetAddresses();
	            			inetAddrs.hasMoreElements();) {
	            		
		                InetAddress inetAddr =
		                		(InetAddress) inetAddrs.nextElement();
		                
		                if (!inetAddr.isLoopbackAddress()) {
		                    if (inetAddr.isSiteLocalAddress())
		                    	return inetAddr;
		                    else if (candidateAddress == null)
		                    	candidateAddress = inetAddr;
		                }
		                
		            }
	            	
	            }
	            
	        }
	        
	        if (candidateAddress != null) return candidateAddress;
	        return InetAddress.getLocalHost();
	    }
	    catch (Exception e) {
	        UnknownHostException unknownHostException =
	        		new UnknownHostException(
	        				"Failed to determine LAN address: " + e);
	        unknownHostException.initCause(e);
	        throw unknownHostException;
	    }
	}

}
