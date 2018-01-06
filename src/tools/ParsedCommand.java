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
	/**
	 * The command itself (first word)
	 */
	public final String cmd;
	
	/**
	 * The command (cmd) arguments
	 */
	public final String[] args;
	
	/**
	 * The attached payload
	 */
	public final byte[] payload;
	
	public ParsedCommand(String request){
		String[] split = request.split(" ");
		cmd = split[0];
		args = new String[split.length - 1];
		for (int i = 1; i < split.length; i++)
			args[i-1] = split[i];
		payload = new byte[] {};
	}
	
	public ParsedCommand(String request, byte[] payload) {
		String[] split = request.split(" ");
		this.cmd = split[0];
		this.args = new String[split.length - 1];
		for (int i = 1; i < split.length; i++)
			args[i-1] = split[i];
		this.payload = payload;
	}
	
	/**
	 * Returns a string with all arguments inline,
	 * starting from a given index to the end.
	 * 
	 * @param start The index to start
	 * @return The concatenation of arguments
	 */
	public String argLine(int start) {
		StringBuilder sb = new StringBuilder();
		for (int i = start; i < args.length; i++) {
			sb.append(args[i] + (i == args.length - 1 ? "" : " "));
		}
		return sb.toString();
	}
	
	public String argLine() {
		return argLine(0);
	}
	
}
