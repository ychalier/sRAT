package tools;

/**
 * A small class to parse a command,
 * retrieving the command itself and
 * its arguments.
 * 
 * @author Yohan Chalier
 *
 */
public class ParsedCommand {
	public final String cmd;
	public final String[] args;
	
	public ParsedCommand(String str){
		String[] split = str.split(" ");
		cmd = split[0];
		args = new String[split.length - 1];
		for (int i = 1; i < split.length; i++)
			args[i-1] = split[i];
	}
}
