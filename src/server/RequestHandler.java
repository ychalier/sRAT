package server;

/**
 * Used by the server to handle
 * received requests.
 * 
 * @author Yohan Chalier
 *
 */
public interface RequestHandler {
	
	String getResponse(String request);

}
