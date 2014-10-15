package eu.excitementproject.eop.common.datastructures.immutable;

import java.io.Serializable;
import java.util.List;

/**
 * List, like <code> java.util.List </code>, but has no
 * operation that changes the list.
 * Only "read" operations are supplied.
 * 
 * @author Asher Stern
 *
 * @param <T>
 */
public interface ImmutableList<T> extends ImmutableCollection<T>, Serializable
{
	public T get(int index);
	
	public int indexOf(Object o);
	
	public int lastIndexOf(Object o);
	
	public ImmutableList<T> subList(int fromIndex, int toIndex);
	
	public List<T> getMutableListCopy();
	
	public ImmutableList<T> getImmutableListCopy();
	
	public ImmutableListIterator<T> listIterator();
	
	public String toString();

	public String mutableListToString();

}
