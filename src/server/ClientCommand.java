package server;

import tools.Command;

public class ClientCommand implements Command {
	
	private CommandServer c2;
	private String prefix;
	
	public ClientCommand(CommandServer c2, String prefix) {
		this.c2 = c2;
		this.prefix = prefix;
	}

	@Override
	public String exec(String[] args) {
		if (c2.getCurrentClient() >= 0) {
			ConnectedClient client;
			if ((client = c2.getClients().identify(c2.getCurrentClient()))
					!= null) {
				StringBuilder cmd = new StringBuilder();
				cmd.append(prefix + " ");
				for (int i = 0; i < args.length; i++){
					cmd.append(args[i]
							+ (i == args.length - 1 ? "" : " "));
				}
				client.stackCmd(cmd.toString());
				return "Added command to stack";
			}
			return "No corresponding client (" + args[0] + ") found.";
		} else {
			return "Select a client first.";
		}
	}

}
