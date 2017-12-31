package server;

import java.util.Scanner;

public class UserInput extends Thread {
	
	private CommandHandler cmdHandler;
	
	public UserInput(CommandHandler cmdHandler){
		this.cmdHandler = cmdHandler;
	}

	public void run() {
		
		Scanner sc = new Scanner(System.in);
		System.out.print(">");
		while (sc.hasNextLine()) {
			String cmd = sc.nextLine();
			String str = cmdHandler.executeCommand(cmd);
			if (str == null) {
				break;
			} else {
				System.out.print(str + "\n>");
			}
		}
		sc.close();

	}

}
