package eu.excitementproject.eop.transformations.datastructures;
import java.util.ListIterator;
import java.util.NoSuchElementException;


/**
 * A java.util.ListIterator that iterates over a list that holds exactly one element.
 * {@link #set(Object)}, {@link #add(Object)} and {@link #remove()} are not supported.
 * 
 * 
 * @author Asher Stern
 * @since Oct 3, 2011
 *
 * @param <T>
 */
public class SingleItemListIterator<T> extends SingleItemIterator<T> implements ListIterator<T>
{
	public SingleItemListIterator(T element)
	{
		super(element);
	}

	public boolean hasPrevious()
	{
		return this.nextCalled;
	}

	public T previous()
	{
		if (nextCalled)
		{
			nextCalled=false;
			return element;
		}
		else
		{
			throw new NoSuchElementException();
		}
	}

	public int nextIndex()
	{
		if (nextCalled)
			return 1;
		else
			return 0;
	}

	public int previousIndex()
	{
		if (nextCalled)
			return 0;
		else
			return -1;
	}

	public void set(T e)
	{
		throw new UnsupportedOperationException();
	}

	public void add(T e)
	{
		throw new UnsupportedOperationException();
	}

}
