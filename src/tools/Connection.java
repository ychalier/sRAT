package tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connection {
	
	// Newline
	private static final byte[] CRLF = new byte[] {13, 10};
	
	// End Of Communication
	private static final byte[] EOC = new byte[] {0, 1, 2};
	
	private Socket socket;
	private InputStream in;
	private OutputStream out;
	
	public Connection(String url, int port)
			throws UnknownHostException, IOException {
		this(new Socket(url, port));
	}
	
	public Connection(Socket socket)
			throws IOException {
		this.socket = socket;
		out = socket.getOutputStream();
		in = socket.getInputStream();
	}
	
	public void write(String request, byte[] payload) {
		try {
			out.write(Integer.toString(payload.length).getBytes());
			out.write(CRLF);
			out.write(request.getBytes());
			out.write(CRLF);
			out.write(payload);
			out.write(EOC);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void write(String response) {
		try {
			out.write(response.getBytes());
			out.write(EOC);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String readLine() {
		StringBuilder sb = new StringBuilder();
		int c;
		try {
			while ((c = in.read()) != CRLF[CRLF.length - 1]) {
				boolean valid = true;
				for (int i = 0; i < CRLF.length; i++) {
					if (CRLF[i] == c)
						valid = false;
				}
				if (valid)
					sb.append((char) c);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sb.toString();
	}
	
	public ParsedCommand readRequest() {
		try {
			// Payload length
			int payloadLength = Integer.parseInt(readLine());
			String request = readLine();
			
			// Reading payload
			byte[] payload = new byte[payloadLength];
			for (int i = 0; i < payloadLength; i++)
				payload[i] = (byte) in.read();
			
			// Moving cursor
			for (int i = 0; i < EOC.length; i++)
				in.read();
			
			return new ParsedCommand(request, payload);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String readResponse() {
		try {
			byte[] lastBytes = new byte[EOC.length];
			boolean isEOC;
			
			StringBuilder response = new StringBuilder();
			int c;
			while (true) {
				c = in.read();
				
				// Stream is closed
				if (c == -1)
					return response.toString();
				
				// Determining if end of communication
				isEOC = true;
				for (int i = 1; i < lastBytes.length; i++) {
					lastBytes[i-1] = lastBytes[i];
					isEOC = isEOC && lastBytes[i-1] == EOC[i - 1]; 
				}
				lastBytes[lastBytes.length - 1] = (byte) c;
				isEOC = isEOC && (byte) c == EOC[EOC.length - 1];
				
				if (isEOC) {
					// Removing last EOC.length - 1 characters
					response.replace(response.length() - EOC.length + 1,
							response.length(), "");
					break;
				} else {
					response.append((char) c);
				}
			}
			return response.toString();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public InetAddress getInetAddress() {
		return socket.getInetAddress();
	}
	
	public void close() throws IOException {
		in.close();
		out.close();
		socket.close();
	}
	
}
