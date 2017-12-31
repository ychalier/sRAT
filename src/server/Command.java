package server;

/**
 * Represents a command to be executed by the command and control server.
 * 
 * Arguments are passed through an array of strings, and an output
 * string is returned.
 * 
 * @author Yohan Chalier
 *
 */
public interface Command {

	public String exec(String[] args);
	
}
