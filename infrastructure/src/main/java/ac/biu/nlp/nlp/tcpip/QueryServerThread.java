package ac.biu.nlp.nlp.tcpip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;


/**
 * <p>Base class for threads of a multi-threaded {@link QueryServer}.
 * <p>For usage examples, see {@link ReversingQueryServerThread}.
 * 
 * @author erelsgl
 * @date 12/01/2011
 */
public abstract class QueryServerThread implements Runnable {
	
	protected static final String ABOUT_COMMAND=":about"; 
	protected static final String QUIT_COMMAND=":quit"; 

	/**
	 * Answer a query.
	 * This is the main function that defines what the server does.
	 * @param input the input string that the user entered.
	 * @return an output string to send the user. 
	 */
    public abstract String query(String input);
   
	/**
	 * Create a welcome message to show a telnet user on login.
	 * This is an optional method - the default is to have no welcome message (null).
	 * @return an output string to send the user. 
	 */
    public String welcomeMessage() {
    	return null;
    }
    
	/**
	 * Create a goodbye message to show a telnet user on logout.
	 * This is an optional method - the default is to have no goodbye message (null).
	 * @return an output string to send the user. 
	 */
    public String goodbyeMessage() {
    	return null;
    }
   
    /**
     * Sets the internal socket and parent-server of this thread.
     * @param newSocket - the socket that the new thread will work with.
     * @param newServer - a link to the parent server (for getting "about" info).
     */
    public void initialize (Socket newSocket, QueryServer newServer) {
    	this.socket = newSocket;
    	this.server = newServer;
    }
    
    /**
     * Set to "true" to end the thread after processing the current query.
     */
    protected boolean quitAfterThisQuery;
    
    protected void printLog(String message) { server.printLog(message); }

    public void run() {
        printLog("Thread starts");
        PrintWriter out = null;
        BufferedReader in = null;
    	try {
	        out = new PrintWriter(socket.getOutputStream(), true);
	        in = new BufferedReader(
					new InputStreamReader(
							socket.getInputStream()));
	        String inputLine, outputLine;

	        
	        outputLine = welcomeMessage();
	        if (outputLine!=null)
	        	out.println(outputLine);

	        quitAfterThisQuery = false;
	        while (!quitAfterThisQuery) {
	        	try {
	        		inputLine = in.readLine();
	        	} catch (SocketException e) {
	        		if (e.getMessage().equals("Connection reset")) {
	        			// "It usually happens when the connection was reset by the peer -- that is, when the client unexpectedly closed the socket. This happens when the user clicks "stop", or "reload", or "back", or enters a new URL, or closes his Internet connection, while a resource is still being downloaded (before your server gets to close its socket). This is normal behavior and you shouldn't worry about it. "
	        			// http://www.jguru.com/faq/view.jsp?EID=237557
	        			break;
	        		} else {
	        			throw e;
	        		}
	        	}
	        	if (inputLine==null) 
	        		break;
	        	printLog("Got input '" + inputLine+"'");
	            if (inputLine.equals(ABOUT_COMMAND)) {
	            	outputLine = server.about();	            	
	            } else if (inputLine.equals(QUIT_COMMAND)) {
	            	outputLine = goodbyeMessage();
	            	quitAfterThisQuery = true;
	            } else {
	            	outputLine = query(inputLine);
	            }
	            if (outputLine!=null) {
		            printLog("Sending output '"+outputLine+"'");
		            out.println(outputLine);
		            out.flush();
	            } else {
		            printLog("No output");
	            }
	        }
    	} catch (IOException e) {
    	    e.printStackTrace();
    	} finally {
			try{if (in!=null) in.close();}catch(Exception e){e.printStackTrace();}
			try{if (out!=null) out.close();}catch(Exception e){e.printStackTrace();}
			try{socket.close();}catch(Exception e){e.printStackTrace();}
	        printLog("Thread quits");
		}
   	}

    
    
    private Socket socket = null;
    private QueryServer server  = null;
}
