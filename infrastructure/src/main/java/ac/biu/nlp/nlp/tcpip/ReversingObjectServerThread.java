package ac.biu.nlp.nlp.tcpip;

/**
 * <p>A demo application for {@link ObjectServer} and {@link ObjectServerThread}.
 * A server that listens on port 4445, and responds to the following commands: "reverse", "split".
 * To test it, use an {@link ObjectClient}, such as in {@link PalindromeTesterWithObjects}. 
 * To do this, run in a separate console window:
 * <pre>
 * > java PalindromeTester
 * </pre>
 * 
 * @author erelsgl
 * @date 21/08/2011
 */
public class ReversingObjectServerThread extends ObjectServerThread {
	@Override public Object query(String command, Object input) {
		if ("reverse".equals(command)) {
			String inputString = input.toString();
			String output = (new StringBuffer(inputString)).reverse().toString();
			return output;
		} else if ("split".equals(command)) {
			String inputString = input.toString();
			String[] inputWords = inputString.split(" ");
			return inputWords;
		} else {
			throw new UnsupportedOperationException("Unknown command "+command);
		}
	}

    public static void main(String[] args) {
    	int port = 4445;
    	new ObjectServer("ReversingObjectServer", "1.0", "Erel Segal")
    		.start(port, ReversingObjectServerThread.class);
    }
}
