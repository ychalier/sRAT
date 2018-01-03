package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Stores a set of clients, represented by their ids.
 * 
 * @author Yohan Chalier
 *
 */
@SuppressWarnings("serial")
public class ClientPool extends HashMap<Integer, ConnectedClient> {
	
	// The file client list will be saved at
	private static final String CLIENTS_FILE = "clients";
	
	public ClientPool() throws IOException {
		if (new File(CLIENTS_FILE).exists()) {
			BufferedReader reader = new BufferedReader(
					new FileReader(CLIENTS_FILE));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] split = line.split("\t");
				if (split.length == 4) {
					int id = Integer.parseInt(split[0]);
					ConnectedClient c = new ConnectedClient(id);
					c.setMACAddress(split[1]);
					c.setInetAddress(split[2]);
					c.setOs(split[3]);
					put(id, c);
				}
			}
			reader.close();
		}
	}
	
	public ConnectedClient findByMac(String MAC){
		for (Integer key: keySet()) {
			if (get(key).getMACAddress().equals(MAC))
				return get(key);
		}
		return null;
	}
	
	public int addClient(String MAC){
		int id = generateId();
		ConnectedClient client = new ConnectedClient(id);
		client.setMACAddress(MAC);
		put(id, client);
		return id;
	}
	
	private int generateId(){
		int id = ThreadLocalRandom.current().nextInt(1000, 10000);
		while (containsKey(id)) {
			id = ThreadLocalRandom.current().nextInt(1000, 10000);
		}
		return id;
	}
	
	public ConnectedClient identify(String idStr) {
		return identify(Integer.parseInt(idStr));
	}
	
	public ConnectedClient identify(int id) {
		return get(id);
	}
	
	public String save(String fileName) throws IOException {
		FileWriter fw = new FileWriter(new File(fileName));
		for (int key: keySet()) {
			ConnectedClient c = get(key);
			fw.write(c.getId() + "\t" + c.getMACAddress()
					+ "\t" + c.getInetAddress() + "\t" + c.getOs() + "\n");
		}
		fw.close();
		return "Saved at " + CLIENTS_FILE;
	}

}
