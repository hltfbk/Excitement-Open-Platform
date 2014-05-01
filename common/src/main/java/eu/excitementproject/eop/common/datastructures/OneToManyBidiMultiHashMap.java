package eu.excitementproject.eop.common.datastructures;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.collections15.MultiMap;

/**
 * A {@link BidiMultiHashMap} that enforces that a value may not be mapped to
 * more than one key (one-to-many relation).<BR>
 * This is done by wrapping any method that puts new values in the map, and
 * throwing {@link IllegalStateException} if this contract is violated (i.e.
 * an existing value is supposed to be mapped to a key that it is not yet mapped
 * to).<BR>
 * <BR>
 * The class allows exactly one key per value (in addition to the base-class's
 * method that allows to get <B>all</B> keys per value.<BR>
 * <BR>
 * <B>NOTE</B> that although base classes are serializable, this class is not
 * (an exception is thrown if deserialization is attempted). This is due to a
 * technical reason - the method {@code readObject()}, which is called during
 * deserialization, is implemented as {@code private} in base class, and therefore
 * cannot be overriden here to make sure that the class's contract is not violated.
 * 
 * @author Ofer Bronstein
 * @since July 2013
 *
 * @param <K>
 * @param <V>
 */
public class OneToManyBidiMultiHashMap<K,V> extends BidiMultiHashMap<K,V> {

	private static final long serialVersionUID = -5780525565136239017L;

	public OneToManyBidiMultiHashMap() {
		super();
	}

	public OneToManyBidiMultiHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public OneToManyBidiMultiHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	public OneToManyBidiMultiHashMap(Map<K, V> mapToCopy) {
		super(mapToCopy);
	}

	public OneToManyBidiMultiHashMap(MultiMap<K,V> mapToCopy) {
		super(mapToCopy);
		assertOne2Many();
	}

	@Override
    public V put(K key, V value) {
		assertValueNotWithNewKey(key, value);
		return super.put(key, value);
	}
	
	@Override
    public boolean putAll(K key, Collection<? extends V> values) {
    	boolean result = super.putAll(key, values);
    	for (V value : values) {
    		assertValueNotWithNewKey(key, value);
    	}
    	return result;
    }
	
	/**
	 * @param value
	 * @return the single key mapped to the value, or {@code null} if value is not in the map.
	 */
	public K getSingleKeyOf(V value) {
		Set<K> keys = getKeysOf(value);
		if (keys.size()>1) {
			throw new IllegalStateException("Internal error - found mroe than one key for value: " + value);
		}
		if (keys.isEmpty()) {
			return null;
		}
		else {
			return keys.iterator().next();
		}
	}

	private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
		throw new IllegalStateException("Serialization not supported for " + OneToManyBidiMultiHashMap.class.getSimpleName());		
	}
	
	private void assertOne2Many() {
		Map<V,K> flippedMap = new HashMap<V,K>();
		for (Entry<K,Collection<V>> entry : this.entrySet()) {
			for (V value : entry.getValue()) {
				K existingKey = flippedMap.get(value);
				if (existingKey != null &&  !existingKey.equals(entry.getKey())) {
					throwExistingValueException(entry.getKey(), value);
				}
				flippedMap.put(value, entry.getKey());
			}
		}
	}
	
	private void assertValueNotWithNewKey(K key, V value) {
		Collection<V> allValues = this.values();
		if (allValues.contains(value) && !this.getKeysOf(value).contains(key)) {
			throwExistingValueException(key, value);
		}
	}
	
	private void throwExistingValueException(K key, V value) {
		throw new IllegalStateException(String.format("Value %s already in map, in a key different than %s", value, key));
	}
}
