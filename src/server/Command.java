package server;

/**
 * Represents a command to be executed
 * by the control server.
 * 
 * @author Yohan Chalier
 *
 */
public interface Command {

	public String exec(String[] args);
	
}
