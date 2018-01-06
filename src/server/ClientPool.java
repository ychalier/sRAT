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
	
	/**
	 *  The file client list will be saved at.
	 */
	private static final String CLIENTS_FILE = "clients";
	
	/**
	 * If the clients file already exists, loads its information.
	 * 
	 * @throws IOException
	 * @see save
	 */
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
	
	/**
	 * Writes the database to an external file.
	 * 
	 * @param fileName File to use as a database.
	 * @return A string to inform user when done.
	 * @throws IOException
	 */
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
	
	/**
	 * Returns a client identified by its MAC address.
	 * 
	 * @param MAC The MAC address of the client.
	 * @return The corresponding client, null if not found.
	 */
	public ConnectedClient findByMac(String MAC){
		for (Integer key: keySet()) {
			if (get(key).getMACAddress().equals(MAC))
				return get(key);
		}
		return null;
	}
	
	/**
	 * Add a client to the pool.
	 * Generates an id for it.
	 * 
	 * @param MAC Client's MAC address.
	 * @return The id given to the client.
	 */
	public int addClient(String MAC){
		int id = generateId();
		ConnectedClient client = new ConnectedClient(id);
		client.setMACAddress(MAC);
		put(id, client);
		return id;
	}
	
	/**
	 * Generates a random numerical id of length 4,
	 * that does not exist in the current database.
	 * 
	 * @return The generated id.
	 */
	private int generateId(){
		int id = ThreadLocalRandom.current().nextInt(1000, 10000);
		while (containsKey(id)) {
			id = ThreadLocalRandom.current().nextInt(1000, 10000);
		}
		return id;
	}
	
	/**
	 * Identifies a client by its id.
	 * 
	 * @param idStr Client id in String format
	 * @return The corresponding client, null if not found.
	 */
	public ConnectedClient identify(String idStr) {
		return identify(Integer.parseInt(idStr));
	}
	
	/**
	 * Identifies a client by its id.
	 * 
	 * @param id Client id.
	 * @return The corresponding client, null if not found.
	 */
	public ConnectedClient identify(int id) {
		return get(id);
	}
	
}
