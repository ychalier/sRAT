package tools;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class Log extends ArrayList<String> {
	
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
	
}
