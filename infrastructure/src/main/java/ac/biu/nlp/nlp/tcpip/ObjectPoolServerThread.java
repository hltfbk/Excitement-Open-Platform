package ac.biu.nlp.nlp.tcpip;

import java.util.*;

/**
 * <p>A server that keeps a pool of objects. Each object can be used by a single thread.
 * <p>Understands the following commands:<ul>
 * <li>add [object]       - insert a new object to the pool.
 * <li>get [Thread ID]    - get an unused object from the pool, and mark it as used by a specific thread.
 * <li>free [Thread ID]   - mark all objects given to that thread as used.
 * <li>clear              - remove all objects from the pool.
 * </ul>
 * 
 * <p>See {@link ObjectPoolClient}</p>
 * 
 * @author erelsgl
 * @date 21/08/2011
 */
public class ObjectPoolServerThread extends ObjectServerThread {
	protected static List<ObjectRecord> pool = new ArrayList<ObjectRecord>();
	
	@Override public Object query(String command, Object input) {
		if ("add".equals(command)) {
			synchronized (pool) {
				pool.add(new ObjectRecord(input));
			}
			return null;
		} else if ("get".equals(command)) {
			Long threadId = (Long) input;
			synchronized (pool) {
				for (ObjectRecord objectRecord: pool) {
					System.out.println("  Object "+objectRecord.object+" is used by "+objectRecord.usingThreadId);
					if (objectRecord.usingThreadId==null) {
						objectRecord.usingThreadId = threadId;
						System.out.println("  Object "+objectRecord.object+" is NOW used by "+threadId);
						return objectRecord.object;
					}
				}
				return null; // no unused object found
			}
		} else if ("free".equals(command)) {
			Long threadId = (Long) input;
			synchronized (pool) {
				for (ObjectRecord objectRecord: pool) {
					if (objectRecord.usingThreadId==threadId) {
						objectRecord.usingThreadId = null;
						return Boolean.TRUE;
					}
				}
				return Boolean.FALSE;
			}
		} else if ("clear".equals(command)) {
			synchronized (pool) {
				pool.clear();
				return null;
			}
		} else {
			throw new UnsupportedOperationException("Unknown command "+command);
		}
	}
	
    public static void main(String[] args) {
    	if (args.length<1)
    		throw new IllegalArgumentException("SYNTAX: ObjectPoolServerThread <port>");
    	int port = Integer.valueOf(args[0]);
    	new ObjectServer("Object Pool Server", "1.1", "Erel Segal")
    		.start(port, ObjectPoolServerThread.class);
    }
   
}


class ObjectRecord {
	Object object;
	Long usingThreadId;
	
	public ObjectRecord(Object theObject) {
		object = theObject;
		usingThreadId = null; // an object is initially unused
	}
}
