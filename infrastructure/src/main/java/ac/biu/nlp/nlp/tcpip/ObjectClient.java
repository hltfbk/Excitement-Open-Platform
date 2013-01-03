package ac.biu.nlp.nlp.tcpip;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * <p>A client that connects to the {@link ObjectServer}.
 * <p>For usage examples, see {@link PalindromeTesterWithObjects}.
 * @author erelsgl
 * @date 16/01/2011
 */
public class ObjectClient {
	/**
	 * Initialize a client that connects to a query server at a specific port.
	 * @param host The host name (e.g. 'localhost').
	 * @param port The port number.
	 */
	public ObjectClient(String host, int port) throws UnknownHostException, IOException  {
		this.host = host; 
		this.port = port;
		initSocket();
	}
	
	synchronized protected void initSocket() throws IOException {
		try {
			socket = new Socket(host, port);
		} catch (java.net.SocketException ex) {
			throw new IOException("Cannot connect to "+host+" at port "+port+". Make sure a server is listening there! (You can test with: telnet "+host+" "+port+")");
		}
		outputStream = new ObjectOutputStream(socket.getOutputStream());
		inputStream = new ObjectInputStream(socket.getInputStream());
	}

	protected Object querySingleTry (String command, Object input) throws Throwable {
			synchronized(outputStream) {
				outputStream.writeObject(command);
				outputStream.writeObject(input);
			}
			Object result;
			synchronized(inputStream) {
				result = inputStream.readObject();
			}
			if (result instanceof Throwable)
				throw new RuntimeException ("Command "+command+" caused an exception on the server",(Throwable)result);
			else
				return result;
	}

	public Object query (String command, Object input) throws Throwable {
		if (input==null)
			throw new NullPointerException("input to query must not be null!");
		try {
			return querySingleTry(command, input);
		} catch (SocketException ex) {  // this may be caused by a restart of the server. Try again once:
			initSocket();
			return querySingleTry(command, input);
		}
	}

	@Override protected void finalize() throws Throwable  {
		outputStream.close();
		inputStream.close();
		socket.close();
	}

	private String host;
	private int port;
	private Socket socket = null;
	private ObjectOutputStream outputStream = null;
	private ObjectInputStream inputStream = null;
}