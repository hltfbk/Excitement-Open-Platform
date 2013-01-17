package eu.excitementproject.eop.common.datastructures;

import java.util.ListIterator;

import eu.excitementproject.eop.common.datastructures.immutable.EmptyIterator;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableListIterator;



/**
 * TODO seems duplicate with {@link ImmutableListIterator}
 * @author Asher Stern
 * @since Feb 6, 2012
 *
 * @param <E>
 */
public class EmptyListIterator<E> extends EmptyIterator<E> implements ListIterator<E>
{
	public void add(E e)
	{
		throw new UnsupportedOperationException();
	}

	public boolean hasPrevious()
	{
		return false;
	}

	public int nextIndex()
	{
		return 0;
	}

	public E previous()
	{
		return null;
	}

	public int previousIndex()
	{
		return -1;
	}

	public void set(E e)
	{
		throw new UnsupportedOperationException();
	}
}
