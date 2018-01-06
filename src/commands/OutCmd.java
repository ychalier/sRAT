package commands;

import server.CommandServer;
import tools.ParsedCommand;

/**
 * Prints the payload to System.out
 * 
 * @author Yohan Chalier
 *
 */
public class OutCmd extends ServerCommand {

	public OutCmd(CommandServer c2) {
		super(c2);
	}
	
	@Override
	public String exec(ParsedCommand pCmd) {
		// Newline after >
		System.out.print('\n');
		
		// Printing payload
		for (int i = 0; i < pCmd.payload.length; i++)
			System.out.print((char) pCmd.payload[i]);
		
		// Printing the prefix
		System.out.print("\n" + c2.getPrefix() + ">");
		
		if (c2.getCurrentClient() >= 0)
			return N_DONE;
		return DONE;
	}

}
