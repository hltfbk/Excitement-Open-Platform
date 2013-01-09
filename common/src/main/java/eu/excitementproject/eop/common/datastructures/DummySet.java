package eu.excitementproject.eop.common.datastructures;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.immutable.EmptyIterator;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;



/**
 * This is a dummy implementation of <code>java.util.Set</code>.
 * The set is empty, and cannot be changed (i.e. add, remove, and other operations are
 * not supported and always throw <code>UnsupportedOperationException</code>.
 * <P>
 * It is not a good idea to use this class. The correct way is to use the interface
 * {@link ImmutableSet}.
 * 
 * @author Asher Stern
 *
 * @param <E>
 */
public class DummySet<E> implements Set<E>, Serializable
{
	private static final long serialVersionUID = 8760761339702570671L;

	public boolean add(E o)
	{
		throw new UnsupportedOperationException();
	}

	public boolean addAll(Collection<? extends E> c)
	{
		throw new UnsupportedOperationException();
	}

	public void clear()
	{
		throw new UnsupportedOperationException();
	}

	public boolean contains(Object o)
	{
		return false;
	}

	public boolean containsAll(Collection<?> c)
	{
		return false;
	}

	public boolean isEmpty()
	{
		return true;
	}

	public Iterator<E> iterator()
	{
		return new EmptyIterator<E>();
	}

	public boolean remove(Object o)
	{
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}

	public int size()
	{
		return 0;
	}

	public Object[] toArray()
	{
		return new Object[0];
	}

	public <T> T[] toArray(T[] a)
	{
		if (a.length>=1)
		{
			a[0]=null;
		}
		return a;
	}
}
