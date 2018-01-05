package commands;

import server.CommandServer;
import tools.ParsedCommand;

/**
 * Display available server commands
 * 
 * @author Yohan Chalier
 *
 */
public class HelpCmd extends ServerCommand {

	public HelpCmd(CommandServer c2) {
		super(c2);
	}

	@Override
	public String exec(ParsedCommand pCmd) {
		StringBuilder builder = new StringBuilder();
		
		for (String key: c2.getCmdsServer().keySet())
			builder.append(key + '\n');
		
		// Removing last '\n'
		builder.setCharAt(builder.length() - 1, (char) 0);
		
		return builder.toString();
	}

}
