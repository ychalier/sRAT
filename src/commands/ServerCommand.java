package commands;

import server.CommandServer;
import tools.ParsedCommand;

public abstract class ServerCommand implements CommandInterface {

	public static final String DONE = "DONE";
	public static final String N_DONE = "N_DONE";
	
	protected CommandServer c2;
	
	public ServerCommand(CommandServer c2) {
		this.c2 = c2;
	}
	
	@Override
	public String exec(ParsedCommand pCmd) {
		return null;
	}

}
