package eu.excitementproject.eop.common.datastructures;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import eu.excitementproject.eop.common.datastructures.immutable.EmptyIterator;


/**
 * 
 * @author Asher Stern
 * @since Feb 6, 2012
 *
 */
public class DummyList<E> implements List<E>
{
	public boolean add(E e)
	{
		throw new UnsupportedOperationException();
	}

	public void add(int index, E element)
	{
		throw new UnsupportedOperationException();
	}

	public boolean addAll(Collection<? extends E> c)
	{
		throw new UnsupportedOperationException();
	}

	public boolean addAll(int index, Collection<? extends E> c)
	{
		throw new UnsupportedOperationException();
	}

	public void clear()
	{
	}

	public boolean contains(Object o)
	{
		return false;
	}

	public boolean containsAll(Collection<?> c)
	{
		if (null==c)return true;
		if (c.size()==0)return true;
		return false;
	}

	public E get(int index)
	{
		return null;
	}

	public int indexOf(Object o)
	{
		return -1;
	}

	public boolean isEmpty()
	{
		return true;
	}

	public Iterator<E> iterator()
	{
		return new EmptyIterator<E>();
	}

	public int lastIndexOf(Object o)
	{
		return -1;
	}

	public ListIterator<E> listIterator()
	{
		return new EmptyListIterator<E>();
	}

	public ListIterator<E> listIterator(int index)
	{
		return new EmptyListIterator<E>();
	}

	public boolean remove(Object o)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public E remove(int index)
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

	public E set(int index, E element)
	{
		throw new UnsupportedOperationException();
	}

	public int size()
	{
		return 0;
	}

	public List<E> subList(int fromIndex, int toIndex)
	{
		if ( (0==fromIndex) && (0==toIndex) )
			return this;
		else
			throw new IndexOutOfBoundsException();
	}

	public Object[] toArray()
	{
		return new Object[0];
	}

	public <T> T[] toArray(T[] a)
	{
		if (a.length==0)return a;
		else
		{
			a[0]=null;
			return a;
		}
	}

}
