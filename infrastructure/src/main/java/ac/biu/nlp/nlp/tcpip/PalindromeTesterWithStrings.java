package ac.biu.nlp.nlp.tcpip;

import java.io.*;
import java.net.*;

/**
 * <p>A demo application. Reads your input and tests whether it's a palindrome.
 * <p>It demonstrates a {@link QueryClient} that connects to the {@link ReversingQueryServerThread}.
 * <p>
 * @author erelsgl
 * @date 07/01/2011
 */
public class PalindromeTesterWithStrings {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
    public static void main(String[] args) throws IOException, InterruptedException {
    	// Create a client for querying the Reversing Server:
    	QueryClient reverser = null;
    	String host = "localhost";
    	int port = 4444;
        try {
        	reverser = new QueryClient("localhost",port, 3);
	    } catch (UnknownHostException e) {
	        System.err.println("Don't know about host: "+host+".");
	        System.exit(1);
            return;
	    } catch (IOException e) {
	        System.err.println("Couldn't get I/O for "
	                           + "the connection to "+host+":"+port+". Please verify that the Reversing Server is running!");
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
        	System.out.println("Sending '"+input+"' to the server...");
        	String reverse = reverser.query(input);
        	System.out.println("... The server replied '"+reverse+"'");
        	if (input.equals(reverse)) {
        		System.out.println("... Which means that it IS a palindrome!");	
        	} else {
        		System.out.println("... Which means that it's NOT a palindrome!");	        		
        	}
        }
    }
}
