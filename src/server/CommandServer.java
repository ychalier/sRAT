package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Set;

/**
 * Represents a Command and Control (C&C) server
 * 
 * @author Yohan Chalier
 *
 */
public class CommandServer implements RequestHandler {
	
	private Set<InetAddress> clients;

	@Override
	public String getResponse(Socket socket)
			throws IOException {
		
		// Identifying client
		InetAddress client = socket.getInetAddress();
		if (!clients.contains(client))
			clients.add(client);
		
		// Reading request
		BufferedReader reader = new BufferedReader(
    			new InputStreamReader(socket.getInputStream()));
		String request = reader.readLine();
		
		// TODO log
		System.out.println(socket.getInetAddress() + ": " + request);

		return "Hello world!";
	}
	
}
