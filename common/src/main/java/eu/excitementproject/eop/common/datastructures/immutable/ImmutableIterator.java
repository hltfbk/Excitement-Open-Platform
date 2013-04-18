package eu.excitementproject.eop.common.datastructures.immutable;

import java.util.Iterator;



/**
 * This class is an <code>Iterator</code>, that does not
 * support {@link #remove()} method.
 * Calling {@link #remove()} will cause an exception to be thrown.
 * @author Asher Stern
 *
 * @param <E>
 */
public abstract class ImmutableIterator<E> implements Iterator<E>
{
	/**
	 * Throws an exception. Calling this method is considered as a bug.
	 */
	public final void remove()
	{
		throw new UnsupportedOperationException("the remove operation is illegal for ImmutableIterator");
	}
}
