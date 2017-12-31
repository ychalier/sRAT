package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Implements basic server features,
 * in a dedicated thread.
 * 
 * @author Yohan Chalier
 *
 */
public class Server extends Thread {
	
	private static final int DEFAULT_BACKLOG = 0;
	private static final int DEFAULT_PORT = 80;
	
	private ServerSocket server;
	private RequestHandler requestHandler;
	
	public Server(RequestHandler requestHandler)
			throws UnknownHostException, IOException {
		this(requestHandler, selectPort());
	}
	
	public Server(RequestHandler requestHandler, int port)
			throws UnknownHostException, IOException{
		this(requestHandler, port, selectBindAddress());
	}
	
	public Server(RequestHandler requestHandler,
				  int port, InetAddress bindAddr)
			throws IOException {
		this.requestHandler = requestHandler;
		server = new ServerSocket(port, DEFAULT_BACKLOG, bindAddr);
		System.out.println("Server hosted on "
						   + bindAddr.getHostAddress()
						   + ":" + port);
	}

	/**
	 * Main loop of the server
	 * Listens for requests and replies.
	 * 
	 * @throws IOException Reading or writing in the connection stream
	 */
	public void run() {

		while (true) {
			try {
				// Reading input
		    	Socket socket = server.accept();
		    	BufferedReader reader = new BufferedReader(
		    			new InputStreamReader(socket.getInputStream()));
				String request = reader.readLine();	
				
				//TODO log request
				System.out.println(request);
				
				// Preparing response
				String httpResponse = "HTTP/1.1 200 OK\r\n\r\n"
									+ requestHandler.getResponse(request);
				
				// Answering request
				socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
				socket.close();
			} catch (IOException e) {
				//TODO handle exception nicely
				System.out.println(e);
			}
	    }
		
	}
	
	/**
	 * Selects the port to host the server on.
	 * 
	 * @return The elected port number
	 */
	private static int selectPort() {
		return DEFAULT_PORT;
	}
	
	/**
	 * Selects the IP address to host the server on.
	 * 
	 * Iterates trough interfaces and addresses and returns the first non
	 * loopback address found.
	 * 
	 * @return The elected IP address
	 * @throws SocketException Error iterating through interfaces
	 * @throws UnknownHostException If no address were found and localhost
	 * 								is not valid.
	 */
	private static InetAddress selectBindAddress()
			throws SocketException, UnknownHostException {
		
        InetAddress candidateAddress = null;
        
        // Iterating through all network card interfaces
        for (Enumeration<NetworkInterface> ifaces =
        		NetworkInterface.getNetworkInterfaces();
        		ifaces.hasMoreElements();) {
        	
            NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
            
            // We omit addresses used by VirtualBox
            if (!iface.toString().contains("VirtualBox")) {
            	
            	// Iterating trough all addresses of the interface
            	for (Enumeration<InetAddress> inetAddrs =
            			iface.getInetAddresses();
            			inetAddrs.hasMoreElements();) {
            		
	                InetAddress inetAddr =
	                		(InetAddress) inetAddrs.nextElement();
	                
	                if (!inetAddr.isLoopbackAddress()) {
	                	
	                    if (inetAddr.isSiteLocalAddress())
	                    	return inetAddr; // Should be the correct one,
	                    					 // returning it directly
	                    else if (candidateAddress == null)
	                    	candidateAddress = inetAddr;
	                }
	                
	            }
            	
            }
            
        }
        
        // Returns the first encountered candidate if found
        if (candidateAddress != null)
        	return candidateAddress;
        
        // Else, returns at least the localhost
        return InetAddress.getLocalHost();
	}

}
