package commands;

import java.io.IOException;

import server.CommandServer;
import tools.ParsedCommand;

/**
 * Save clients to a local file.
 * 
 * @author Yohan Chalier
 *
 */
public class SaveClientsCmd extends ServerCommand {
	
	private static String CLIENT_FILE = "clients";

	public SaveClientsCmd(CommandServer c2) {
		super(c2);
	}
	
	@Override
	public String exec(ParsedCommand pCmd) {
		
		try {
			return c2.getClients().save(CLIENT_FILE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
