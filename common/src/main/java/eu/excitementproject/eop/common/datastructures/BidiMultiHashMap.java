package eu.excitementproject.eop.common.datastructures;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;

/**
 * A {@link MultiHashMap} that provides access to all the keys
 * mapped to a given value (inverse lookup). 
 * 
 * @author Ofer Bronstein
 * @since July 2013
 *
 * @param <K>
 * @param <V>
 */
public class BidiMultiHashMap<K,V> extends MultiHashMap<K,V> {

	private static final long serialVersionUID = -4442842450142264903L;

	public BidiMultiHashMap() {
		super();
	}

	public BidiMultiHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public BidiMultiHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	public BidiMultiHashMap(Map<K, V> mapToCopy) {
		super(mapToCopy);
	}

	public BidiMultiHashMap(MultiMap<K, V> mapToCopy) {
		super(mapToCopy);
	}

	/**
	 * @param value
	 * @return A set of all the keys mapped to the given value.
	 */
	public Set<K> getKeysOf(V value) {
		Set<K> keys = new LinkedHashSet<K>();
		for (Entry<K, Collection<V>> entry : this.entrySet()) {
			if (entry.getValue().contains(value)) {
				keys.add(entry.getKey());
			}
		}
		return keys;
	}
	
}
