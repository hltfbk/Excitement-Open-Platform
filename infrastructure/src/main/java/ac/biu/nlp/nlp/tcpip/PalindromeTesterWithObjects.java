package ac.biu.nlp.nlp.tcpip;

import java.io.*;
import java.net.*;

/**
 * <p>A demo application. Reads your input and tests whether it's a palindrome.
 * <p>It demonstrates a {@link ObjectClient} that connects to the {@link ReversingObjectServerThread}.
 * <p>
 * @author erelsgl
 * @date 07/01/2011
 */
public class PalindromeTesterWithObjects {

	/**
	 * @param args
	 * @throws Throwable 
	 */
    public static void main(String[] args) throws Throwable {
    	// Create a client for querying the Reversing Server:
    	ObjectClient reverser = null;
    	String host = "localhost";
    	int port = 4445;
        try {
        	reverser = new ObjectClient("localhost",port);
	    } catch (UnknownHostException e) {
	        System.err.println("Don't know about host: "+host+".");
	        System.exit(1);
            return;
	    } catch (IOException e) {
	        System.err.println("Couldn't get I/O for "
	                           + "the connection to "+host+":"+port+". Please verify that the Reversing Object Server is running!");
	        System.exit(2);
            return;
	    }

    	// Create a console for getting user input:
	    Console console = System.console();
        if (console == null) {
            System.err.println("No console.");
            System.exit(3);
            return;
        }

        while (true) {
        	String input = console.readLine("%nEnter a string (':quit' to quit):  ");
        	if (input.equals(":quit")) {
        		break;
        	}
        	System.out.println("Sending reverse("+input+") to the server...");
        	Object reverse = reverser.query("reverse", input);
        	System.out.println("... The server replied '"+reverse+"'");
        	if (input.equals(reverse)) {
        		System.out.println("... Which means that it IS a palindrome!");	
        	} else {
        		System.out.println("... Which means that it's NOT a palindrome!");
        		System.out.println("... But I can split it into words for you:");
            	String[] words = (String[])(reverser.query("split", input));
            	for (String word: words)
            		System.out.println("\t"+word);	        		
        	}
        }
    }
}
