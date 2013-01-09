package eu.excitementproject.eop.common.datastructures.immutable;

import java.io.Serializable;
import java.util.Collection;

/**
 * A collection with only operations that make no changes on
 * the collection.
 * @author Asher Stern
 *
 * @param <T>
 */
public interface ImmutableCollection<T> extends Iterable<T>, Serializable
{
	public boolean contains(Object o);

	public boolean containsAll(Collection<?> c);
	
	public boolean containsAll(ImmutableCollection<?> c);

	public boolean isEmpty();
	
	public ImmutableIterator<T> iterator();

	public int size();

	public T[] toArray(T[] a);
	
	public Collection<T> getMutableCollectionCopy();
	
	public ImmutableCollection<T> getImmutableCollectionCopy();


}
