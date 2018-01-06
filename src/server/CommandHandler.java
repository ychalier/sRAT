package server;

/**
 * Used by the user input to
 * handle commands.
 * 
 * @author Yohan Chalier
 *
 */
public interface CommandHandler {
	
	/**
	 * Execute a command from the user input.
	 * 
	 * @param cmd The command to be executed
	 * @return The response string, to be printed to the user
	 */
	String executeCommand(String cmd);
	
	/**
	 * Builds the prefix for the user interface.
	 * 
	 * @return The string to be put before the '>' for user input.
	 */
	String getPrefix();

}
