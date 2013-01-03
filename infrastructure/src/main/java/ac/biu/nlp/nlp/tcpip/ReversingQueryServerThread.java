package ac.biu.nlp.nlp.tcpip;

/**
 * <p>A demo application for {@link QueryServer} and {@link QueryServerThread}.
 * A server that listens on port 4444, and reverses each request.
 * To test it, first run this class in Eclipse, or from a console window:
 * <pre>
 * > java ReversingServerThread
 * </pre>
 * Then, in another console window, open telnet:
 * <pre>
 * > telnet localhost 4444
 * </pre>
 * You can open several telnet clients simultaneously.
 * 
 * <p>A second way to use the server is using a {@link QueryClient}, such as in {@link PalindromeTesterWithObjects}. 
 * To do this, run in a separate console window:
 * <pre>
 * > java PalindromeTester
 * </pre>
 * 
 * <p>A third way to use the server is using an external script, such as web/reversingclient.php.
 * <p>To run this script on your computer, you have to install Apache and PHP.
 * 
 * @author erelsgl
 * @date 07/01/2011
 */
public class ReversingQueryServerThread extends QueryServerThread {
	@Override public String query(String input) {
		String output = (new StringBuffer(input)).reverse().toString();
		return output;
	}
	
	@Override public String welcomeMessage() {
		return 
			"Welcome to Reversing Server!\n"+
			"Enter strings and I will reverse them for you.\n"+
			"Special commands are ':about' and ':quit'.";
	}
	
    public static void main(String[] args) {
    	int port = 4444;
    	new QueryServer("ReversingServer", "1.0", "Erel Segal")
    		.start(port, ReversingQueryServerThread.class);
    }
}
