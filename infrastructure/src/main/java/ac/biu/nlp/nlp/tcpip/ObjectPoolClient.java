package ac.biu.nlp.nlp.tcpip;

import java.io.*;
import java.net.*;

/**
 * <p>A client that connects to {@link ObjectPoolServerThread}.
 * <p>For demo, see {@link ObjectPoolClientDemo}
 * @author erelsgl
 * @date 07/01/2011
 */
public abstract class ObjectPoolClient extends ObjectClient {
	
	/**
	 * Create a new initialized object.
	 * Called when no free object is found on the server.
	 * @return
	 */
	protected abstract Object newObject();
	
	public ObjectPoolClient(String theHost, int thePort) throws UnknownHostException, IOException {
		super(theHost, thePort);
	}
	
	public void add(Object theObject) throws Throwable {
		query("add", theObject);
	}
	
	public Object get() throws Throwable {
		long threadId = Thread.currentThread().getId();
		Object unusedObject = null;
		for (;;) {
			unusedObject = query("get", threadId);
			if (unusedObject!=null) break; // found unused object!
			add(newObject()); // add a new object and try again.
		}
		return unusedObject;
	}
	
	public void free() throws Throwable {
		long threadId = Thread.currentThread().getId();
		Boolean hasFreed = (Boolean)query("free", threadId);
		if (!hasFreed)
			throw new IllegalStateException("No object belongs to thread "+threadId);
	}
	
	public void clear() throws Throwable {
		query("clear", Boolean.TRUE);
	}
}
