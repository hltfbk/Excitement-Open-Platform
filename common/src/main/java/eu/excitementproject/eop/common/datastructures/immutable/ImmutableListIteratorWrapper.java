package eu.excitementproject.eop.common.datastructures.immutable;

import java.util.ListIterator;


/**
 * 
 * @author Asher Stern
 * @since Mar 29, 2012
 *
 * @param <E>
 */
public class ImmutableListIteratorWrapper<E> extends ImmutableListIterator<E>
{
	public ImmutableListIteratorWrapper(ListIterator<? extends E> realIterator)
	{
		this.realIterator = realIterator;
	}

	public boolean hasNext()
	{
		return realIterator.hasNext();
	}

	public E next()
	{
		return realIterator.next();
	}

	public boolean hasPrevious()
	{
		return realIterator.hasPrevious();
	}

	public E previous()
	{
		return realIterator.previous();
	}

	public int nextIndex()
	{
		return realIterator.nextIndex();
	}

	public int previousIndex()
	{
		return realIterator.previousIndex();
	}

	protected ListIterator<? extends E> realIterator;
}
