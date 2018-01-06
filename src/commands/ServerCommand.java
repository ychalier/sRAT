package commands;

import server.CommandServer;
import tools.ParsedCommand;

/**
 * Basic server command scheme, with a reference to
 * the command server to perform dynamic operations.
 * 
 * @author Yohan Chalier
 *
 */
public abstract class ServerCommand implements CommandInterface {

	/**
	 * The string returned when task is done and client is not selected.
	 * The connection is then closed.
	 */
	public static final String DONE = "DONE";
	/**
	 * The string returned when task is done and client is selected.
	 * The connection is then kept alive.
	 */
	public static final String N_DONE = "N_DONE";
	
	/**
	 * A reference to the command server.
	 */
	protected CommandServer c2;
	
	public ServerCommand(CommandServer c2) {
		this.c2 = c2;
	}
	
	@Override
	public String exec(ParsedCommand pCmd) {
		return null;
	}

}
