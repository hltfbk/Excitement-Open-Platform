package ac.biu.nlp.nlp.tcpip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * <p>A TCP/IP client. Connects to the {@link QueryServer}.
 * <p>For usage examples, see {@link PalindromeTesterWithStrings}.
 * @author erelsgl
 * @date 16/01/2011
 */
public class QueryClient {
	/**
	 * Initialize a client that connects to a query server at a specific port.
	 * @param host The host name (e.g. 'localhost').
	 * @param port The port number.
	 * @param numOfLinesInWelcomeMessage number of lines sent by the server as "welcome". These lines should be discarded before we can read the server responses to our queries.
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public QueryClient(String host, int port, int numOfLinesInWelcomeMessage) throws UnknownHostException, IOException {
    	socket = new Socket(host, port);
    	outputStream = new PrintWriter(socket.getOutputStream(), true);
    	inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    	
    	// Discard the welcome message:
    	for (int i=0; i<numOfLinesInWelcomeMessage; i++)
    		inputStream.readLine();
	}

	public String query (String input) throws IOException {
		outputStream.println(input);
        return inputStream.readLine();
	}
	
	@Override protected void finalize() throws Throwable  {
		outputStream.close();
		inputStream.close();
		socket.close();
	}

	private Socket socket = null;
    private PrintWriter outputStream = null;
    private BufferedReader inputStream = null;
}