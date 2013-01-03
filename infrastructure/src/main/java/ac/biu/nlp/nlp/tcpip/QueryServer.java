package ac.biu.nlp.nlp.tcpip;

import java.io.IOException;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * <p>A multi-threaded TCP/IP server that listens on a specified port and answers queries.
 * <p>The main work is done by a descendant of {@link QueryServerThread}.
 * <p>For usage examples, see {@link ReversingQueryServerThread}.
 * 
 * @author erelsgl
 * @date 12/01/2011
 */
public class QueryServer {
	
	public QueryServer(String name, String version, String author) {
		this.name = name;
		this.author = author;
		this.version = version;
	}

	public String about() {
		return name+" "+version+", by "+author+", listening on port "+port+" since "+startTime; 
	}
	
	public final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void start(int thePort, Class<? extends QueryServerThread> threadClass) {
		this.port = thePort;
		ServerSocket serverSocket = null;
		boolean listening = true;
		try {
			serverSocket = new ServerSocket(thePort);
			try
			{

				startTime = dateFormat.format(Calendar.getInstance().getTime());
				printLog(name+" starts listening on port "+thePort+".");

				while (listening) {
					try {
						QueryServerThread newThread = threadClass.newInstance();
						// QueryServerThread newThread = new THREAD(); - not possible - see http://www.angelikalanger.com/GenericsFAQ/FAQSections/TypeParameters.html#FAQ200 
						newThread.initialize(serverSocket.accept(), this);
						new Thread(newThread).start();
					} catch (IOException e) {
						System.err.println("Could not accept connection on port: "+thePort+". Please check if another instance of the server is listening on this port!");
						e.printStackTrace();
						System.exit(2);
						return;
					} catch (InstantiationException e) {
						System.err.println("Could not instantiate a new thread.");
						e.printStackTrace();
						System.exit(3);
						return;
					} catch (IllegalAccessException e) {
						System.err.println("Could not access a new thread.");
						e.printStackTrace();
						System.exit(4);
					}
				}
			}
			finally
			{
				try {
					serverSocket.close();
				} catch (IOException e) {
					System.err.println("Could not close server socket.");
					e.printStackTrace();
					System.exit(5);
				}
			}
		} catch (IOException e) {
			System.err.println("Could not listen on port: "+thePort+". Please check if another instance of the server is listening on this port!");
			e.printStackTrace();
			System.exit(1);
			return;
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
