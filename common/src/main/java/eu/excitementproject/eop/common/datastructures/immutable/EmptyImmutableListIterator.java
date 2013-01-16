package eu.excitementproject.eop.common.datastructures.immutable;

import java.util.NoSuchElementException;

/**
 * 
 * @author Asher Stern
 * @since Nov 9, 2011
 *
 * @param <E>
 */
public class EmptyImmutableListIterator<E> extends ImmutableListIteratorWrapper<E>
{
	public EmptyImmutableListIterator()
	{
		super(null);
	}
	
	public boolean hasNext()
	{
		return false;
	}

	public E next()
	{
		throw new NoSuchElementException();
	}

	public boolean hasPrevious()
	{
		return false;
	}

	public E previous()
	{
		throw new NoSuchElementException();
	}

	public int nextIndex()
	{
		return 0;
	}

	public int previousIndex()
	{
		return -1;
	}

//	public void remove()
//	{
//		throw new UnsupportedOperationException("the remove operation is illegal for ImmutableListIterator");
//	}
//
//	
//	public void set(E e)
//	{
//		throw new UnsupportedOperationException("the set operation is illegal for ImmutableListIterator");
//	}
//
//	public void add(E e)
//	{
//		throw new UnsupportedOperationException("the add operation is illegal for ImmutableListIterator");
//	}
}
