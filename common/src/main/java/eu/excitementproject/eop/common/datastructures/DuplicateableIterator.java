package eu.excitementproject.eop.common.datastructures;

import java.util.Iterator;


/**
 * An {@link Iterator} with an additional method <code>duplicate</code>
 * <P>
 * The {@link #duplicate()} method just duplicates the iterator - such
 * that after the duplication there are two identical iterators.
 * After duplication - each {@link #next()} method returns the same
 * object. (I.e. calling {@link #next()} on the original iterator, and
 * calling {@link #next()} on the duplicated iterator - return the same
 * object).
 * 
 * <P>
 * 
 * The {@linkplain DuplicateableIterator} purpose is to be able to go
 * on to the next elements of the collection being iterated, but
 * later returning to the same point in which the {@link #duplicate()}
 * was called.
 * @author Asher Stern
 *
 * @param <E>
 */
public interface DuplicateableIterator<E> extends Iterator<E>
{
	public DuplicateableIterator<E> duplicate();
}
