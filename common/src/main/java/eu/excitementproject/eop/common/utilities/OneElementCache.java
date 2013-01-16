package eu.excitementproject.eop.common.utilities;

/**
 * Implementation of {@link Cache} with one element cached at most.
 * @author Asher Stern
 * @since Aug 22, 2010
 *
 * @param <K>
 * @param <V>
 */
public class OneElementCache<K, V> implements Cache<K, V>
{
	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.Cache#containsKey(java.lang.Object)
	 */
	public synchronized boolean containsKey(K key)
	{
		boolean ret = false;
		if (cached)
		{
			if (null==key)
			{
				if (null==this.key)
					ret = true;
				else
					ret = false;
			}
			else
			{
				if (key.equals(this.key))
					ret = true;
				else
					ret = false;
			}
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.Cache#get(java.lang.Object)
	 */
	public synchronized V get(K key)
	{
		V ret = null;
		if (key!=null)
		{
			if (key.equals(this.key))
				ret = this.value;
		}
		else
		{
			if (null==this.key)
				ret = this.value;
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.Cache#put(java.lang.Object, java.lang.Object)
	 */
	public synchronized void put(K key, V value)
	{
		this.key = key;
		this.value = value;
		this.cached = true;
	}
	

	protected K key = null;
	protected V value = null;
	protected boolean cached = false;
}
