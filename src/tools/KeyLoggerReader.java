package tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Interprets a keylogger log file and provides
 * tools to print it.
 * 
 * @author Yohan Chalier
 *
 */
public class KeyLoggerReader {
	
	// Key codes
	
	private static final int BACKSPACE = 8;
	private static final int ENTER = 13;
	private static final int CAPSLOCK = 20;
	
	private static final HashMap<Integer, Character> AZERTY;
	private static final HashMap<Integer, Character> AZERTY_CAPS;
	private static final HashMap<Integer, Character> AZERTY_ALT;
	
	static {
		AZERTY = new HashMap<Integer, Character>();
		AZERTY_CAPS = new HashMap<Integer, Character>();
		AZERTY_ALT = new HashMap<Integer, Character>();
		
		AZERTY.put(48, 'à');
		AZERTY.put(49, '&');
		AZERTY.put(50, 'é');
		AZERTY.put(51, '"');
		AZERTY.put(52, '\'');
		AZERTY.put(53, ')');
		AZERTY.put(54, '-');
		AZERTY.put(55, 'è');
		AZERTY.put(56, '_');
		AZERTY.put(57, 'ç');
		AZERTY.put(186, '$');
		AZERTY.put(187, '=');
		AZERTY.put(188, ',');
		AZERTY.put(190, ';');
		AZERTY.put(191, ':');
		AZERTY.put(192, 'ù');
		AZERTY.put(219, ')');
		AZERTY.put(220, '*');
		AZERTY.put(221, '^');
		AZERTY.put(223, '!');
		
		AZERTY_CAPS.put(48, '0');
		AZERTY_CAPS.put(49, '1');
		AZERTY_CAPS.put(50, '2');
		AZERTY_CAPS.put(51, '3');
		AZERTY_CAPS.put(52, '4');
		AZERTY_CAPS.put(53, '5');
		AZERTY_CAPS.put(54, '6');
		AZERTY_CAPS.put(55, '7');
		AZERTY_CAPS.put(56, '8');
		AZERTY_CAPS.put(57, '9');
		AZERTY_CAPS.put(186, '£');
		AZERTY_CAPS.put(187, '+');
		AZERTY_CAPS.put(188, '?');
		AZERTY_CAPS.put(190, '.');
		AZERTY_CAPS.put(191, '/');
		AZERTY_CAPS.put(192, '%');
		AZERTY_CAPS.put(219, '°');
		AZERTY_CAPS.put(220, 'µ');
		AZERTY_CAPS.put(221, '¨');
		AZERTY_CAPS.put(223, '§');
		
		AZERTY_ALT.put(48, '@');
		AZERTY_ALT.put(49, '&');
		AZERTY_ALT.put(50, '~');
		AZERTY_ALT.put(51, '#');
		AZERTY_ALT.put(52, '{');
		AZERTY_ALT.put(53, '[');
		AZERTY_ALT.put(54, '|');
		AZERTY_ALT.put(55, '`');
		AZERTY_ALT.put(56, '\\');
		AZERTY_ALT.put(57, '^');
		AZERTY_ALT.put(186, '¤');
		AZERTY_ALT.put(187, '}');
		AZERTY_ALT.put(188, ',');
		AZERTY_ALT.put(190, ';');
		AZERTY_ALT.put(191, ':');
		AZERTY_ALT.put(192, 'ù');
		AZERTY_ALT.put(219, ']');
		AZERTY_ALT.put(220, '*');
		AZERTY_ALT.put(221, '^');
		AZERTY_ALT.put(223, '!');
	}
	
	/**
	 * Holds info about a single keypress.
	 * 
	 * @author Yohan Chalier
	 *
	 */
	private class KeyPress {
		@SuppressWarnings("unused")
		String timestamp;
		int keyCode;
		boolean isShiftPressed;
		boolean isAltPressed;
		boolean isCtrlPressed;
		
		KeyPress(String logLine) {
			String[] split = logLine.split("\t");
			this.timestamp = split[0];
			this.keyCode = Integer.parseInt(split[1]);
			this.isShiftPressed = Boolean.parseBoolean(split[2]);
			this.isAltPressed = Boolean.parseBoolean(split[3]);
			this.isCtrlPressed = Boolean.parseBoolean(split[4]);
		}
		
		char toChar(boolean capsLock){
			boolean uppercase = capsLock ^ isShiftPressed;
			
			if (keyCode == ENTER)
				return '\n';
			
			// Key codes from NUMPAD
			if (keyCode > 95 && keyCode < 106)
				return "0123456789".charAt(keyCode - 96);
			
			// Handling normal keys, and modifiers
			if (AZERTY.containsKey(keyCode)) {
				if (isAltPressed && isCtrlPressed)
					return AZERTY_ALT.get(keyCode);
				if (uppercase)
					return AZERTY_CAPS.get(keyCode);
				return AZERTY.get(keyCode);
			}

			if (!uppercase)
				return Character.toLowerCase((char) keyCode);
			
			return (char) keyCode;
		}
	}
	
	/**
	 * Holds the pressed keys
	 */
	private ArrayList<KeyPress> log;
	
	public KeyLoggerReader(String logFile) {
		log = new ArrayList<KeyPress>();
		read(logFile);
	}
	
	public void read(String logFile) {
		try {
			BufferedReader reader =
					new BufferedReader(new FileReader(logFile));
			String line;
			while ((line = reader.readLine()) != null)
				log.add(new KeyPress(line));
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		boolean capsLock = false;
		for (KeyPress key: log) {
			if (key.keyCode == CAPSLOCK)
				capsLock = !capsLock;
			else if (key.keyCode == BACKSPACE)
				sb.replace(sb.length() - 1, sb.length(), "");
			else
				sb.append(key.toChar(false));
		}
		return sb.toString();
	}
	
	public void print() {
		System.out.println(toString());
	}

}
