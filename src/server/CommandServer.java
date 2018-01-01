package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import tools.Command;

/**
 * Represents a Command and Control (C&C) server
 * 
 * @author Yohan Chalier
 *
 */
public class CommandServer implements RequestHandler, CommandHandler {
	
	// The string sent when no matching command is found
	public static final String ERROR_COMMAND_NOT_FOUND = "Command not found.";
	
	// The sets of commands for both the server and the client
	private final HashMap<String, Command> cmdsServer;
	private final HashMap<String, Command> cmdsClient;
	
	// The set of connected clients IPs
	private Set<InetAddress> clients;
	
	// The set of couples (MAC Address, ID)
	private HashMap<String, Integer> macMap;
	
	public CommandServer() {
		clients = new HashSet<InetAddress>();
		macMap = new HashMap<String, Integer>();
		// TODO : load macMap from external file
		
		cmdsServer = new HashMap<String, Command>();
		cmdsClient = new HashMap<String, Command>();
		
		// Display available commands
		cmdsServer.put("help", new Command(){
			@Override
			public String exec(String[] args) {
				StringBuilder builder = new StringBuilder();
				
				for (String key: cmdsServer.keySet())
					builder.append(key + '\n');
				
				// Removing last '\n'
				builder.setCharAt(builder.length() - 1, (char) 0);
				
				return builder.toString();
			}
		});
		
		// Close user input
		cmdsServer.put("exit", new Command(){
			@Override
			public String exec(String[] args) {
				return null;
			}
		});
		
		// Assign an ID to a client
		cmdsClient.put("GETID", new Command(){
			@Override
			public String exec(String[] args) {
				// Already known client
				if (macMap.containsKey(args[0])){
					return Integer.toString(macMap.get(args[0]));
				}
				// New client
				else {
					int id = ThreadLocalRandom.current().nextInt(1000, 10000);
					macMap.put(args[0], id);
					return Integer.toString(id);
				}
			}
		});
		
		// ADD COMMANDS HERE
		
	}

	@Override
	public String getResponse(Socket socket)
			throws IOException {
		
		// Identifying client
		InetAddress client = socket.getInetAddress();
		if (!clients.contains(client))
			clients.add(client);
		
		// Reading request
		BufferedReader reader = new BufferedReader(
    			new InputStreamReader(socket.getInputStream()));
		
		StringBuilder builder = new StringBuilder();
		
		// Reading POST header
		String line;
		int contentLength = 0;
		final String contentHeader = "Content-Length: ";
		while (!(line = reader.readLine()).equals("")) {
			if (line.startsWith(contentHeader)) {
				contentLength = Integer.parseInt(
						line.substring(contentHeader.length()));
			}
 		}
		
		// Reading POST body
		int c = 0;
		for (int i = 0; i < contentLength; i++){
			c = reader.read();
			builder.append((char) c);
		}
		String request = builder.toString();
				
		// TODO log
		System.out.print("\n" + socket.getInetAddress()
						+ ": " + request + "\n>");
		
		// Executing command
		ParsedCommand pCmd = new ParsedCommand(request);
		if (cmdsClient.containsKey(pCmd.cmd))
			return cmdsClient.get(pCmd.cmd).exec(pCmd.args);
		return ERROR_COMMAND_NOT_FOUND;
	}

	@Override
	public String executeCommand(String cmd) {
		ParsedCommand pCmd = new ParsedCommand(cmd);
		if (cmdsServer.containsKey(pCmd.cmd))
			return cmdsServer.get(pCmd.cmd).exec(pCmd.args);
		return ERROR_COMMAND_NOT_FOUND;
	}
	
	/**
	 * A small class to parse a command,
	 * retrieving the command itself and
	 * its arguments.
	 * 
	 * @author Yohan Chalier
	 *
	 */
	private class ParsedCommand {
		String cmd;
		String[] args;
		
		ParsedCommand(String str){
			String[] split = str.split(" ");
			cmd = split[0];
			args = new String[split.length - 1];
			for (int i = 1; i < split.length; i++)
				args[i-1] = split[i];
		}
		
	}
	
}
