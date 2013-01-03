package ac.biu.nlp.nlp.tcpip;

import java.io.*;
import java.net.*;

/**
 * <p>A demo for {@link ObjectPoolClient}.
 * @author erelsgl
 * @date 07/01/2011
 */
public class ObjectPoolClientDemo extends ObjectPoolClient {
	public ObjectPoolClientDemo(String theHost, int thePort) throws UnknownHostException, IOException {
		super(theHost, thePort);
	}

	@Override protected Object newObject() {
		String randomValue = String.valueOf(Math.random());
		System.out.println("New object created: "+randomValue);
		return new StringBuffer(randomValue);
	}
	
	/**
	 * Create and return a new ObjectPoolClient.
	 * @param host
	 * @param port
	 */
	public static ObjectPoolClientDemo newInstance(String host, int port) {
    	// Create the client for getting objects from the pool:
    	ObjectPoolClientDemo client = null;
        try {
        	client = new ObjectPoolClientDemo(host, port);
	    } catch (UnknownHostException e) {
	        System.err.println("Don't know about host: "+host+".");
	        System.exit(1);
	    } catch (IOException e) {
	        System.err.println("Couldn't get I/O for the connection to "+host+":"+port+". Please verify that the Object Pool Server is running!");
	        System.exit(2);
	    }
	    return client;
	}
	
	
	/**
	 * Demo program. To use, first run an instance of ObjectPoolServerThread.
	 */
    public static void main(String[] args) throws Throwable {
    	if (args.length<1)
    		throw new IllegalArgumentException("SYNTAX: ObjectPoolClientDemo <port>");
    	int port = Integer.valueOf(args[0]);
	    
	    // Clear the pool on the server:
		//ObjectPoolClientDemo client = ObjectPoolClientDemo.newInstance("localhost", port);
		//client.clear();
	    
	    // Start many threads that will use objects in the pool:
	    for (int i=0; i<12; ++i) {
	    	new Thread(new ObjectPoolClientDemoThread(port)).start();
	    }
    }
}









/**
 * A thread for the demo main program 
 */
class ObjectPoolClientDemoThread implements Runnable {
	ObjectPoolClientDemo client;
	
	ObjectPoolClientDemoThread(int thePort) {
		client = ObjectPoolClientDemo.newInstance("localhost", thePort);
	}
	
	@Override public void run() {
		StringBuffer sb;
		try {
			sb = (StringBuffer)client.get();
		} catch (Throwable e) {
			e.printStackTrace();
			return;
		}
		System.out.println("Thread "+Thread.currentThread().getId()+" got "+sb);
		if (sb==null) return;
		try {
			client.free();
		} catch (Throwable e) {
			e.printStackTrace();
			return;
		}
		System.out.println("Thread "+Thread.currentThread().getId()+" freed "+sb);
	}
}
