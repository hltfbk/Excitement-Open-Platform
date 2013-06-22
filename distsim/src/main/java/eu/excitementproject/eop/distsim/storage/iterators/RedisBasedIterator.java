package eu.excitementproject.eop.distsim.storage.iterators;

import java.io.Serializable;
import java.util.NoSuchElementException;

import redis.clients.jedis.Jedis;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.util.Serialization;
import eu.excitementproject.eop.distsim.util.SerializationException;

/**
 * An implementation of ImmutableIterator, which based on Redis storage device
 * 
 * <P>non thread-safe
 * 
 * @author Meni Adler
 * @since 22/07/2012
 *
 * @param <T> the type of the iterated items
 * 
 */
public class RedisBasedIterator<T extends Serializable> extends ImmutableIterator<T>  {

	public RedisBasedIterator(Jedis jedis) {
		this(jedis,1);
	}
	
	public RedisBasedIterator(Jedis jedis, int entriesPerItem) {
		this.jedis = jedis;
		this.count = 0;
		this.id = 1;
		this.entriesPerItem = entriesPerItem;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return count < (jedis.dbSize() / entriesPerItem);
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public T next()  {
		
		//tmp for debug
		//if (id > 100)
			//throw new NoSuchElementException();
		
		id++;
		String val = null; // = jedis.get(Integer.toString(id));
		while ((val = jedis.get(Integer.toString(id))) == null && hasNext())
			id++;
		if (val != null) {
			count++;
			try {
				return Serialization.deserialize(val);
			} catch (SerializationException e) {
				throw new RuntimeException(e);
			}
		} else
			throw new NoSuchElementException();
				
	}

	protected final Jedis jedis;
	protected int id;
	protected int count;
	protected final int entriesPerItem;
}
