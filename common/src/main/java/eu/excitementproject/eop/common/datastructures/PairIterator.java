package eu.excitementproject.eop.common.datastructures;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 
 * @author Asher Stern
 * @since May 28, 2013
 *
 * @param <T>
 */
public class PairIterator<T> implements Iterator<T>
{
	public PairIterator(T t1, T t2)
	{
		super();
		this.t1 = t1;
		this.t2 = t2;
	}

	@Override
	public boolean hasNext()
	{
		return (begin||middle);
	}

	@Override
	public T next()
	{
		if (begin)
		{
			begin=false;
			middle=true;
			return t1;
		}
		else if (middle)
		{
			middle=false;
			return t2;
		}
		else
		{
			throw new NoSuchElementException();
		}
			
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}

	private final T t1;
	private final T t2;
	
	private boolean begin=true;
	private boolean middle=false;
}
