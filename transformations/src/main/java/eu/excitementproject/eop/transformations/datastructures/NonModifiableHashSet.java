package eu.excitementproject.eop.transformations.datastructures;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIteratorWrapper;



/**
 * This class is used by {@link FastIntegerKeyMap}.
 * <P>
 * Calling any "edit" method (i.e. a method that intends to change the set
 * e.g. add an element or remove an element) throws a runtime-exception.
 * <BR>
 * Only detection methods (i.e. methods that return information about the set,
 * but are not intended to change it) work.
 * 
 * @author Asher Stern
 * @since Mar 2, 2011
 *
 * @param <T>
 */
public class NonModifiableHashSet<T> implements Set<T>, Serializable
{
	private static final long serialVersionUID = 4248175263246459250L;
	
	public NonModifiableHashSet(Set<T> realSet)
	{
		this.realSet = realSet;
	}

	public Iterator<T> iterator()
	{
		return new ImmutableIteratorWrapper<T>(realSet.iterator());
	}

	public boolean add(T e)
	{
		throw new UnsupportedOperationException("NonModifiableHashSet does not support modify operations.");
	}

	public boolean remove(Object o)
	{
		throw new UnsupportedOperationException("NonModifiableHashSet does not support modify operations.");
	}


	public boolean addAll(Collection<? extends T> c)
	{
		throw new UnsupportedOperationException("NonModifiableHashSet does not support modify operations.");
	}

	public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException("NonModifiableHashSet does not support modify operations.");
	}

	public boolean removeAll(Collection<?> c)
	{
		throw new UnsupportedOperationException("NonModifiableHashSet does not support modify operations.");
	}

	public void clear()
	{
		throw new UnsupportedOperationException("NonModifiableHashSet does not support modify operations.");
	}

	

	public int size()
	{
		return realSet.size();
	}

	public boolean isEmpty()
	{
		return realSet.isEmpty();
	}

	public boolean contains(Object o)
	{
		return realSet.contains(o);
	}

	public Object[] toArray()
	{
		return realSet.toArray();
	}

	public <TA> TA[] toArray(TA[] a)
	{
		return realSet.toArray(a);
	}

	public boolean containsAll(Collection<?> c)
	{
		return realSet.containsAll(c);
	}
	
	private Set<T> realSet;
}
