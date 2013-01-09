package eu.excitementproject.eop.common.datastructures.immutable;

import java.util.Iterator;


/**
 * 
 * @author Asher Stern
 * @since Mar 29, 2012
 *
 * @param <E>
 */
public class ImmutableIteratorWrapper<E> extends ImmutableIterator<E>
{
	
	public ImmutableIteratorWrapper(Iterator<? extends E> realIterator)
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

	
	protected Iterator<? extends E> realIterator;
}
