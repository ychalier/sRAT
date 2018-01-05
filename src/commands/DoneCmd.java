package commands;

import server.CommandServer;
import tools.ParsedCommand;

/**
 * Close connection
 * 
 * @author Yohan Chalier
 *
 */
public class DoneCmd extends ServerCommand {

	public DoneCmd(CommandServer c2) {
		super(c2);
	}
	
	@Override
	public String exec(ParsedCommand pCmd) {
		if (c2.getCurrentClient() >= 0)
			return N_DONE;
		return DONE;
	}

}
