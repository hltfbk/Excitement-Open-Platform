package ac.biu.nlp.nlp.tcpip;

import java.io.IOException;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * <p>A multi-threaded TCP/IP server that listens on a specified port and answers queries. The queries and replies are general objects.
 * <p>The main work is done by a descendant of {@link ObjectServerThread}.
 * <p>For usage examples, see {@link ReversingQueryServerThread}.
 * <p>For a similar server where the queries are simple strings, see {@link QueryServer}.
 * 
 * @author erelsgl
 * @date 21/08/2011
 */
public class ObjectServer {
	
	public ObjectServer(String name, String version, String author) {
		this.name = name;
		this.author = author;
		this.version = version;
	}

	public String about() {
		return name+" "+version+", by "+author+", listening on port "+port+" since "+startTime; 
	}
	
	public final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void start(int thePort, Class<? extends ObjectServerThread> threadClass) {
       this.port = thePort;
       ServerSocket serverSocket = null;
       boolean listening = true;
       try {
            serverSocket = new ServerSocket(thePort);
            startTime = dateFormat.format(Calendar.getInstance().getTime());
            printLog(name+" starts listening on port "+thePort+".");
       } catch (IOException e) {
            System.err.println("Could not listen on port: "+thePort+". Please check if another instance of the server is listening on this port!");
    	    e.printStackTrace();
            System.exit(1);
            return;
       }

       while (listening) {
    	   try {
    		   ObjectServerThread newThread = threadClass.newInstance();
    		   			// QueryServerThread newThread = new THREAD(); - not possible - see http://www.angelikalanger.com/GenericsFAQ/FAQSections/TypeParameters.html#FAQ200 
    		   newThread.initialize(serverSocket.accept(), this);
        	   new Thread(newThread).start();
    	   } catch (IOException e) {
               System.err.println("Could not accept connection on port: "+thePort+". Please check if another instance of the server is listening on this port!");
               e.printStackTrace();
               System.exit(2);
    	   } catch (InstantiationException e) {
               System.err.println("Could not instantiate a new thread.");
               e.printStackTrace();
               System.exit(3);
    	   } catch (IllegalAccessException e) {
               System.err.println("Could not access a new thread.");
               e.printStackTrace();
               System.exit(4);
    	   }
       }

       try {
    	   serverSocket.close();
       } catch (IOException e) {
           System.err.println("Could not close server socket.");
           e.printStackTrace();
           System.exit(5);
       }

       printLog(name+" quits listening on port "+thePort+".");
    }
    
    
    void printLog(String message) {
        System.out.println(dateFormat.format(Calendar.getInstance().getTime()) + ": " + message);
    }


	private String startTime;
	private String name;
	private String author;
	private String version;
	private int port;
}
