package server;

import java.io.IOException;

import tools.Connection;

/**
 * Used by the server to handle
 * received requests.
 * 
 * @author Yohan Chalier
 *
 */
public interface RequestHandler {
	
	/**
	 * Handles a server requests and writes an answer in the connection
	 * output stream.
	 * 
	 * @param conn The connection given by the server
	 * @throws IOException
	 */
	void handle(Connection conn) throws IOException;
	
	/**
	 * Regularly checked by the server.
	 * 
	 * @return Wether the server should close itself.
	 */
	boolean isClosed();

}
