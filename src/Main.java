import java.io.IOException;
import java.net.UnknownHostException;

import server.CommandServer;
import server.Server;
import server.UserInput;

public class Main {

	public static void main(String[] args)
			throws UnknownHostException, IOException {
		
		// TODO Handle exceptions
						
		CommandServer c2c = new CommandServer();
		
		// Starting server thread
		Server server = new Server(c2c);
		server.start();
		
		// Starting input thread
		UserInput input = new UserInput(c2c);
		input.start();
		
	}
	

}
