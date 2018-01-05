package commands;

import server.CommandServer;
import tools.ParsedCommand;

/**
 * Show log
 * 
 * @author Yohan Chalier
 *
 */
public class LogCmd extends ServerCommand {

	public LogCmd(CommandServer c2) {
		super(c2);
	}
	
	@Override
	public String exec(ParsedCommand pCmd) {
		if (pCmd.args.length > 0)
			return c2.getLog().getText(Integer.parseInt(pCmd.args[0]));
		return c2.getLog().getText();
	}

}
