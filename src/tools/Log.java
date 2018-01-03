package tools;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Log extends ArrayList<String> {
	
	private static final SimpleDateFormat SDF =
			new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
	private static final String[] MODES = new String[] {"REQST", "REPLY"};
	
	public String getText() {
		return getText(0);
	}
	
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
	
	public void add(int mode, String line) {
		if (mode >= 0 && mode < MODES.length) {
			add(MODES[mode] + "\t" + line);
		} else {
			add(line);
		}
	}
	
	@Override
	public boolean add(String line){
		String timestamp = SDF.format(
				new Timestamp(System.currentTimeMillis()));
		return super.add(timestamp + "\t" + line);
	}
	
}
