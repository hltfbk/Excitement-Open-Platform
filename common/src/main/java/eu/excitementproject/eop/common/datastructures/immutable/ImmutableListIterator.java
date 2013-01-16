package eu.excitementproject.eop.common.datastructures.immutable;

import java.util.ListIterator;


/**
 * 
 * @author Asher Stern
 * 
 *
 * @param <E>
 */
public abstract class ImmutableListIterator<E> implements ListIterator<E>
{
	public final void remove()
	{
		throw new UnsupportedOperationException("the remove operation is illegal for ImmutableListIterator");
	}
	
	public final void set(E e)
	{
		throw new UnsupportedOperationException("the set operation is illegal for ImmutableListIterator");
	}

	public final void add(E e)
	{
		throw new UnsupportedOperationException("the add operation is illegal for ImmutableListIterator");
	}
}
