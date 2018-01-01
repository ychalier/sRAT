package server;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("serial")
public class ClientPool extends HashMap<Integer, ConnectedClient> {
	
	public ConnectedClient findByMac(String MAC){
		for (Integer key: keySet()) {
			if (get(key).getMACAddress().equals(MAC))
				return get(key);
		}
		return null;
	}
	
	public int addClient(String MAC){
		int id = generateId();
		ConnectedClient client = new ConnectedClient();
		client.setMACAddress(MAC);
		put(id, client);
		System.out.print("\n New client with ID " + id + " detected.\n>");
		return id;
	}
	
	private int generateId(){
		int id = ThreadLocalRandom.current().nextInt(1000, 10000);
		while (containsKey(id)) {
			id = ThreadLocalRandom.current().nextInt(1000, 10000);
		}
		return id;
	}

}
