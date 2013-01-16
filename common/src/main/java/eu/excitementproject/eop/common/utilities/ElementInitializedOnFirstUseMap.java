package eu.excitementproject.eop.common.utilities;

import java.util.*;

/**
 * A cache for elements ordered by key. Each element will be initialized the first time it is used.
 * 
 * @author erelsgl
 * @date 2011-08-25
 * @deprecated use {@link org.apache.commons.collections15.map.LazyMap}
 */
@Deprecated
public abstract class ElementInitializedOnFirstUseMap<Key,Value> {
	private Map<Key,Value> elements = new HashMap<Key,Value>();
	
	/**
	 * return the initialized value for the given key.
	 */
	protected abstract Value initialized(Key key) throws Throwable;
	
	/**
	 * @return the initial (and final) value of myElement.
	 */
	public Value get(Key key) {
		if (!elements.containsKey(key)) {
			try {
				Value element = initialized(key);
				if (element==null) 
					throw new NullPointerException("element still null after initialization!");
				elements.put(key, element);
			} catch (Throwable ex) {
				throw new RuntimeException("Cannot initialize element", ex);
			}
		}
		return elements.get(key);
	}
	
	public void remove(Key key) {
		elements.remove(key);
	}
}
