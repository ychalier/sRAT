
import server.Server;
import server.CommandServer;

public class Main {

	public static void main(String[] args) throws Exception {
						
		Server server = new Server(new CommandServer());
		server.start();
		
	}
	

}
