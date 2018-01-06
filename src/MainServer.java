import java.io.IOException;

import server.CommandServer;
import server.Server;
import server.UserInput;

public class MainServer {

	public static void main(String[] args) {
								
		CommandServer c = new CommandServer();
		
		// Starting server thread
		Server server;
		try {
			server = new Server(c);
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Starting input thread
		UserInput input = new UserInput(c);
		input.start();
		
	}
	

}
