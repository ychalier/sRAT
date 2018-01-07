package commands;

import server.CommandServer;
import tools.ParsedCommand;

public class KlogCmd extends ServerCommand {

	public KlogCmd(CommandServer c2) {
		super(c2);
	}
	
	@Override
	public String exec(ParsedCommand pCmd) {
		c2.setCommunicating(true);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < pCmd.payload.length; i++)
			sb.append((char) pCmd.payload[i]);
		System.out.println(sb.toString());
		if (c2.getCurrentClient() >= 0)
			return N_DONE;
		return DONE;
	}

}
