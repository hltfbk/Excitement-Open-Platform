package eu.excitementproject.eop.common.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
* An LRU cache, based on <code>LinkedHashMap</code>.
* <P>
* <B>Caution: This class was not tested in our lab.</B>
*
* <p>
* This cache has a fixed maximum number of elements (<code>cacheSize</code>).
* If the cache is full and another entry is added, the LRU (least recently used) entry is dropped.
*
* <p>
* This class is thread-safe. All methods of this class are synchronized.
*
* <p>
* Author: Christian d'Heureuse, Inventec Informatik AG, Zurich, Switzerland<br>
* Multi-licensed: EPL / LGPL / GPL / AL / BSD.
* Slightly modified by Shachar to fit BIU API.
*/
public class LruCache<K, V> implements Cache<K,V>{

	private static final float hashTableLoadFactor = 0.75f;

	private LinkedHashMap<K, V> map;
	private int cacheSize;

	/**
	* Creates a new LRU cache.
	* @param cacheSize the maximum number of entries that will be kept in this cache.
	*/
	public LruCache(int cacheSize) {
		this.cacheSize = cacheSize;
		int hashTableCapacity = (int) Math
				.ceil(cacheSize / hashTableLoadFactor) + 1;
		map = new LinkedHashMap<K, V>(hashTableCapacity, hashTableLoadFactor,
				true) {
			// (an anonymous inner class)
			private static final long serialVersionUID = 1;

			@Override
			protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
				return size() > LruCache.this.cacheSize;
			}
		};
	}

	/**
	* Retrieves an entry from the cache.<br>
	* The retrieved entry becomes the MRU (most recently used) entry.
	* @param key the key whose associated value is to be returned.
	* @return    the value associated to this key, or null if no value with this key exists in the cache.
	*/
	public synchronized V get(K key) {
		return map.get(key);
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.Cache#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) {
		
		return map.containsKey(key);
	}

	/**
	* Adds an entry to this cache.
	* The new entry becomes the MRU (most recently used) entry.
	* If an entry with the specified key already exists in the cache, it is replaced by the new entry.
	* If the cache is full, the LRU (least recently used) entry is removed from the cache.
	* @param key    the key with which the specified value is to be associated.
	* @param value  a value to be associated with the specified key.
	*/
	public synchronized void put(K key, V value) {
		map.put(key, value);
	}

	/**
	* Clears the cache.
	*/
	public synchronized void clear() {
		map.clear();
	}

	/**
	* Returns the number of used entries in the cache.
	* @return the number of entries currently in the cache.
	*/
	public synchronized int usedEntries() {
		return map.size();
	}

	/**
	* Returns a <code>Collection</code> that contains a copy of all cache entries.
	* @return a <code>Collection</code> with a copy of the cache content.
	*/
	public synchronized Collection<Map.Entry<K, V>> getAll() {
		return new ArrayList<Map.Entry<K, V>>(map.entrySet());
	}
}