package server;

import java.util.Scanner;

/**
 * A thread to read user input and execute commands.
 * Server-side.
 * 
 * @author Yohan Chalier
 *
 */
public class UserInput extends Thread {
	
	private CommandHandler cmdHandler;
	
	public UserInput(CommandHandler cmdHandler){
		this.cmdHandler = cmdHandler;
	}

	public void run() {
		
		Scanner sc = new Scanner(System.in);
		System.out.print(">");
		
		while (sc.hasNextLine()) {
			// Reading command
			String cmd = sc.nextLine();
			
			// Executing command and storing output
			String str = cmdHandler.executeCommand(cmd);
			
			// Close thread on null response
			if (str == null) {
				break;
			} else {
				System.out.print(str + "\n>");
			}
		}
		sc.close();

	}

}
