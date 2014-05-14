package eu.excitementproject.eop.common.datastructures;

import java.util.Hashtable;
import java.util.Map;

/**
 * A Hashtable that takes case insensitive Strings for keys
 * @author Amnon Lotan
 * @since Dec 7, 2010
 *
 * @param <V>
 */
public class KeyCaseInsensitiveHashTable<V> extends Hashtable<String, V>
{
	/* (non-Javadoc)
	 * @see java.util.Hashtable#containsKey(java.lang.Object)
	 */
	public synchronized boolean containsKey(Object key)
	{		
		return (key != null) ? super.containsKey(key.toString().toLowerCase()) : false;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Hashtable#get(java.lang.Object)
	 */
	public synchronized V get(Object key)
	{
		return (key != null) ? super.get(key.toString().toLowerCase()) : null;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
	 */
	public synchronized V put(String key, V value) 
	{
		return (key != null) ? super.put(key.toString().toLowerCase(), value) : null;
	};

	/* (non-Javadoc)
	 * @see java.util.Hashtable#putAll(java.util.Map)
	 */
	public synchronized void putAll(Map<? extends String, ? extends V> t)
	{
		if (t != null)
			for (String key : t.keySet())
				put(key, t.get(key));
	}
	
	/* (non-Javadoc)
	 * @see java.util.Hashtable#remove(java.lang.Object)
	 */
	public synchronized V remove(Object key)
	{
		return (key != null) ? super.remove(key.toString().toLowerCase()) : null;
	}	
	
	private static final long serialVersionUID = 3175591884407354366L;
}
