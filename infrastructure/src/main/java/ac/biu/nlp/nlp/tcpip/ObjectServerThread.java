package ac.biu.nlp.nlp.tcpip;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;


/**
 * <p>Base class for threads of a multi-threaded {@link ObjectServer}.
 * <p>For usage examples, see {@link ReversingQueryServerThread}.
 * <p>For a similar server thread where the queries are simple strings, see {@link QueryServerThread}.
 * 
 * @author erelsgl
 * @date 21/08/2011
 */
public abstract class ObjectServerThread implements Runnable {
	
	protected static final String ABOUT_COMMAND=":about"; 
	protected static final String QUIT_COMMAND=":quit";
	
	protected boolean logEachQuery=true;

	/**
	 * Answer a query.
	 * This is the main function that defines what the server does.
	 */
    public abstract Object query(String command, Object input) throws Throwable;
   
    /**
     * Sets the internal socket and parent-server of this thread.
     * @param newSocket - the socket that the new thread will work with.
     * @param newServer - a link to the parent server (for getting "about" info).
     */
    public void initialize (Socket newSocket, ObjectServer newServer) {
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
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
    	try {
	        out = new ObjectOutputStream(socket.getOutputStream());
	        in = new ObjectInputStream(socket.getInputStream());
	        Object inputObject, outputObject;
	        String inputCommand;

	        quitAfterThisQuery = false;
	        while (!quitAfterThisQuery) {
	        	try {
	        		inputCommand = (String)(in.readObject());
	        		if (inputCommand==null) break;
	        		inputObject = in.readObject();
	        		if (inputObject==null) break;
	        	} catch (EOFException e) {
	        		quitAfterThisQuery = true;
	        		break;
	        	} catch (SocketException e) {
	        		if (e.getMessage().equals("Connection reset")) {
	        			// "It usually happens when the connection was reset by the peer -- that is, when the client unexpectedly closed the socket. This happens when the user clicks "stop", or "reload", or "back", or enters a new URL, or closes his Internet connection, while a resource is still being downloaded (before your server gets to close its socket). This is normal behavior and you shouldn't worry about it. "
	        			// http://www.jguru.com/faq/view.jsp?EID=237557
	        			break;
	        		} else {
	        			throw e;
	        		}
	        	} catch (ClassNotFoundException e) {
					e.printStackTrace();
					continue;
				}
	        	if (logEachQuery) printLog("Got input: " + inputCommand+"("+inputObject+")");
	        	
	        	try {
	        		outputObject = query(inputCommand, inputObject);
	        	} catch (Throwable ex) {
	        		outputObject = ex;
	        	}
	        	if (logEachQuery) printLog("Sending output: "+outputObject);
	            out.writeObject(outputObject);
	            out.flush();
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
    private ObjectServer server  = null;
}
