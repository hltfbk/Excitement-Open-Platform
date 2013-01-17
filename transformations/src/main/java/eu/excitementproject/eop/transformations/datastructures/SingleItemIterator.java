package eu.excitementproject.eop.transformations.datastructures;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 
 * A java.util.Iterator that iterates over a collection that holds exactly one element.
 * {@link #remove()} is not supported.
 * 
 * @see SingleItemSet
 * @see SingleItemList
 * 
 * @author Asher Stern
 * @since 2011
 *
 * @param <T>
 */
public class SingleItemIterator<T> implements Iterator<T>
{
	public SingleItemIterator(T element)
	{
		super();
		this.element = element;
	}

	public boolean hasNext()
	{
		return !nextCalled;
	}

	public T next()
	{
		if (nextCalled)throw new NoSuchElementException();
		nextCalled=true;
		return element;
	}

	public void remove()
	{
		throw new UnsupportedOperationException();
	}
	
	protected final T element;
	protected boolean nextCalled=false;
}
