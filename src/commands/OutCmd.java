package commands;

import server.CommandServer;
import tools.ParsedCommand;

public class OutCmd extends ServerCommand {

	public OutCmd(CommandServer c2) {
		super(c2);
	}
	
	@Override
	public String exec(ParsedCommand pCmd) {
		
		System.out.print('\n');
		for (int i = 0; i < pCmd.payload.length; i++)
			System.out.print((char) pCmd.payload[i]);
		System.out.print(c2.getPrefix() + ">");
		if (c2.getCurrentClient() >= 0)
			return N_DONE;
		return DONE;
	}

}
