package commands;

import server.CommandServer;
import tools.ParsedCommand;

/**
 * Selects a client.
 * 
 * @author Yohan Chalier
 *
 */
public class SelectCmd extends ServerCommand {

	public SelectCmd(CommandServer c2) {
		super(c2);
	}
	
	@Override
	public String exec(ParsedCommand pCmd) {
		// Check if an id is given
		if (pCmd.args.length != 1)
			return "Usage: select ID";
		
		// Read id
		int id = Integer.parseInt(pCmd.args[0]);
		
		// Check if id is valid
		if (c2.getClients().containsKey(id)) {
			// If everything is good, set current client
			c2.setCurrentClient(id);
			return "Selected client " + id;
		}
		return "Wrong id.";
	}

}
