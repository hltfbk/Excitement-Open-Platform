package eu.excitementproject.eop.common.datastructures.immutable;

import java.util.Collection;
import java.util.Iterator;

/**
 * An immutable collection.
 * It wraps a <code>java.util.Collection</code>, but <B> does
 * not implement <code>java.util.Collection</code> </B>.
 * <BR>
 * The real collection can be further modified by any one who holds it, but not
 * by this interface.
 * Casting to the real collection is impossible at compilation time.
 * 
 * @author Asher Stern
 *
 */
public abstract class AbstractImmutableCollectionWrapper<T> implements ImmutableCollection<T>
{
	private static final long serialVersionUID = 8584453468823520667L;

	public AbstractImmutableCollectionWrapper(Collection<? extends T> collection)
	{
		this.wrappedCollection = collection;
	}

	public boolean contains(Object o)
	{
		if (this.wrappedCollection==null) return false;
		return this.wrappedCollection.contains(o);
	}

	public boolean containsAll(Collection<?> c)
	{
		// preserving java.util.Collection's behavior, though buggy.
		if (c==null) throw new NullPointerException("containsAll parameter was null (c==null).");
		if (this.wrappedCollection==null) return false;
		else return this.wrappedCollection.containsAll(c);
	}

	public boolean containsAll(ImmutableCollection<?> c)
	{
		if (this == c) return true;
		else if ( (this.size()==0) && (c.size()==0) ) return true;
		else if ( (this.size()==0) && (c.size()!=0) ) return false;
		else if ( (this.size()!=0) && (c.size()==0) ) return false;
		else
		{
			boolean missingElementDetected = false;
			Iterator<T> thisIterator = this.iterator();
			while ( (thisIterator.hasNext()) && (!missingElementDetected) )
			{
				T element = thisIterator.next();
				if (c.contains(element)) ;
				else missingElementDetected = true;
			}
			return missingElementDetected;
		}
		
	}

	public abstract ImmutableCollection<T> getImmutableCollectionCopy();

	public abstract Collection<T> getMutableCollectionCopy();


	public boolean isEmpty()
	{
		if (this.wrappedCollection==null) return true;
		else return this.wrappedCollection.isEmpty();
	}

	public ImmutableIterator<T> iterator()
	{
		if (this.wrappedCollection==null) return new EmptyIterator<T>();
		else return new ImmutableIteratorWrapper<T>(this.wrappedCollection.iterator());
	}

	public int size()
	{
		if (null==this.wrappedCollection) return 0;
		else return this.wrappedCollection.size();
	}

	public T[] toArray(T[] a)
	{
		if (null==this.wrappedCollection) return null;
		else return this.wrappedCollection.toArray(a);
	}
	
	
	
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		if (null==this.wrappedCollection)
			return prime;
		else return this.wrappedCollection.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		AbstractImmutableCollectionWrapper other = (AbstractImmutableCollectionWrapper) obj;
		if (wrappedCollection == null) {
			if (other.wrappedCollection != null)
				return false;
		} else if (!wrappedCollection.equals(other.wrappedCollection))
			return false;
		return true;
	}





	protected Collection<? extends T> wrappedCollection;

}
