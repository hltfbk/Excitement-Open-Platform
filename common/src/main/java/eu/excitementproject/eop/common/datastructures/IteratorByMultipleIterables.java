package eu.excitementproject.eop.common.datastructures;

import java.util.Iterator;


/**
 * Implementation of <code>java.util.Iterator</code> that iterates on the elements
 * of several <code>java.util.Iterable</code>s in the order they are given.
 * <P>
 * <B>NOT THREAD SAFE!!!</B>
 * <P>
 * The constructor gets an <code>Iterable</code> of <code>Iterable</code>s. The {@link #next()}
 * method returns the first element of the first Iterable, then the second element of the
 * first Iterable, and so on, until <B>all</B> first Iterable's elements were returned, and then it
 * returns the first element of the second Iterable, and so on.
 * <P>
 * <B>NOT THREAD SAFE!!!</B>
 * 
 *  
 * @author Asher Stern
 * 
 *
 * @param <T>
 */
public class IteratorByMultipleIterables<T> implements Iterator<T>
{
	public IteratorByMultipleIterables(Iterable<? extends Iterable<T>> multipleIterables)
	{
		if (null==multipleIterables)
			hasNextFlag = false;
		else
		{
			outerIterator = multipleIterables.iterator();
			if (null==outerIterator)
				hasNextFlag = false;
			else
			{
				computeNext();
			}
		}
		
		
	}
	
	public boolean hasNext()
	{
		return hasNextFlag;
	}

	public T next()
	{
		T ret = nextObject;
		computeNext();
		return ret;
	}

	public void remove()
	{
		throw new UnsupportedOperationException("IteratorByMultipleIterables does not support remove() method.");
	}
	
	
	protected void computeNext()
	{
		// The function assumes that outerIterator is not null
		hasNextFlag = false;
		boolean stop = false;
		while (!stop)
		{
			if (innerIterator!=null)
			{
				if (innerIterator.hasNext())
				{
					nextObject = innerIterator.next();
					hasNextFlag = true;
					stop = true;
				}
			}
			if (!hasNextFlag)
			{
				if (!outerIterator.hasNext())
					stop = true;
				else
				{
					innerIterator = null;
					Iterable<T> nextIterable = outerIterator.next();
					if (nextIterable!=null)
						innerIterator = nextIterable.iterator();
				}
			}
		}
		
		
		
		
	}
	
	protected Iterator<? extends Iterable<T>> outerIterator;
	protected Iterator<T> innerIterator = null;
	protected boolean hasNextFlag = false;
	protected T nextObject;
	
	

}
