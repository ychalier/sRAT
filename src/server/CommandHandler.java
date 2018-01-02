package server;

/**
 * Used by the user input to
 * handle commands.
 * 
 * @author Yohan Chalier
 *
 */
public interface CommandHandler {
	
	String executeCommand(String cmd);
	String getPrefix();

}
