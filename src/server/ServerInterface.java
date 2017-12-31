package server;

import java.io.IOException;
import java.util.Scanner;

public class ServerInterface {

	public static void main(String[] args)
			throws IOException {
		
		// Starting server thread
		Server server = new Server(new CommandServer());
		server.start();
		
		// Reading commands
		Scanner sc = new Scanner(System.in);
		System.out.print(">");
		while (sc.hasNextLine()) {
			String cmd = sc.nextLine();
			if (cmd.equals("exit"))
				break;
			else {
				// TODO execute function
				System.out.print(">");
			}
		}
		sc.close();
		
		// TODO close server

	}

}
