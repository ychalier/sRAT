package commands;

import tools.ParsedCommand;

/**
 * Represents a command to be executed by the command and control server.
 * 
 * Arguments are passed through an array of strings, and an output
 * string is returned.
 * 
 * @author Yohan Chalier
 *
 */
public interface CommandInterface {

	/**
	 * Execute the represented command.
	 * 
	 * @param pCmd Contains all arguments for the command to be executed.
	 * @return The response String.
	 */
	public String exec(ParsedCommand pCmd);
	
}
