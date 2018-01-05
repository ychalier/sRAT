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
	
	void handle(Connection conn) throws IOException;

}
