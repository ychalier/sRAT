package tools;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Log incoming and outgoing requests
 * 
 * @author Yohan Chalier
 *
 */
@SuppressWarnings("serial")
public class Log extends ArrayList<String> {
	
	/**
	 * Timestamp format
	 */
	private static final SimpleDateFormat SDF =
			new SimpleDateFormat("yyyy/MM/dd.HH:mm:ss");
	
	/**
	 * Prefix for requests or replies
	 */
	private static final String[] MODES = new String[] {"REQST", "REPLY"};
	
	/**
	 * Log file
	 */
	private static final String LOG_FILE = "log";
	
	/**
	 * Builds a string containing the lines of the log.
	 * 
	 * @return The string made of all the lines.
	 */
	public String getText() {
		return getText(0);
	}
	
	/**
	 * Builds a string containing the lines of the log.
	 * 
	 * @param nRows Number of lines to print
	 * @return The string made of those lines
	 */
	public String getText(int nRows) {
		
		int topRow = Math.max(size() - nRows, 0);
		if (nRows == 0) topRow = 0;
		
		StringBuilder tmp = new StringBuilder();
		for (int i = size() - 1; i >= topRow; i--) {
			tmp.append(get(i) + '\n');
		}
		
		tmp.setCharAt(tmp.length() - 1, (char) 0);
		
		return tmp.toString();
	}
	
	/**
	 * Add a line to the log, with a timestamp.
	 * Also appends it to a file.
	 * 
	 * @param mode Wether it is a reply (0) or a request (1)
	 * @param line The line to add
	 */
	public void add(int mode, String line) {
		if (mode >= 0 && mode < MODES.length) {
			add(MODES[mode] + "\t" + line);
		} else {
			add(line);
		}
	}
	
	/**
	 * Add a line to the log, with a timestamp.
	 * Also appends it to a file.
	 * 
	 * @param line The line to add
	 */
	@Override
	public boolean add(String line) {
		String timestamp = SDF.format(
				new Timestamp(System.currentTimeMillis()));
		append(timestamp + "\t" + line);
		return super.add(timestamp + "\t" + line);
	}
	
	/**
	 * Appends a line to the log file.
	 * 
	 * @param line The line to append
	 */
	private void append(String line) {
		try {
			// Opening file with append set to true
			PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true));
			out.println(line);
			out.close();
		} catch (IOException e) {}
	}
	
}
