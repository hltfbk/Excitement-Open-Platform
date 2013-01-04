package eu.excitementproject.eop.common.utilities.immutable; 


import java.util.NoSuchElementException;

import eu.excitementproject.eop.common.utilities.immutable.ImmutableIteratorWrapper;

/**
 * An iterator over an empty collection.
 * <code> hasNext() </code> is alwayes false.
 * @author Asher Stern
 *
 * @param <E>
 */
public class EmptyIterator<E> extends ImmutableIteratorWrapper<E>
{
	public EmptyIterator()
	{
		super(null);
	}

	public boolean hasNext()
	{
		return false;
	}

	public E next()
	{
		// preserving java.util.Iterator's behavior, though buggy.
		throw new NoSuchElementException();
	}

//	public void remove()
//	{
//		// preserving java.util.Iterator's behavior, though buggy.
//		throw new IllegalStateException();
//	}
}
