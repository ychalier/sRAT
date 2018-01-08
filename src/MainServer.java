import java.io.IOException;
import java.net.InetAddress;

import server.CommandServer;
import server.Server;
import server.UserInput;

public class MainServer {

	public static void main(String[] args) {
								
		CommandServer c = new CommandServer();
		
		// Starting server thread
		Server server;
		try {
			server = new Server(c, 80, InetAddress.getByName("127.0.0.1"));
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Starting input thread
		UserInput input = new UserInput(c);
		input.start();
		
	}
	

}
