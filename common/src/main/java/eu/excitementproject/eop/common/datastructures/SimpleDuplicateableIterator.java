package eu.excitementproject.eop.common.datastructures;

import java.util.ArrayList;
import java.util.Iterator;

// NOT thread safe


/**
 * An implementation of {@link DuplicateableIterator}, in a simple way.
 * This implementation is not very good from performance (memory) point
 * of view, and is <B> NOT THREAD SAFE </B>.
 * 
 * @author Asher Stern
 *
 * @param <E>
 */
public class SimpleDuplicateableIterator<E> implements DuplicateableIterator<E>
{
	////////////////////// PUBLIC PART //////////////////////////
	
	public SimpleDuplicateableIterator(Iterator<E> iterator)
	{
		this.currentIndex = 0;
		this.oldElements = new ArrayList<E>();
		this.iterator = iterator;
	}
	

	public DuplicateableIterator<E> duplicate()
	{
		return new SimpleDuplicateableIterator<E>(this);
	}

	public boolean hasNext()
	{
		if (this.currentIndex>=this.oldElements.size())
			return this.iterator.hasNext();
		else
			return true;
	}

	public E next()
	{
		if (this.currentIndex>=this.oldElements.size())
		{
			E ret = this.iterator.next();
			this.oldElements.add(ret);
			this.currentIndex++;
			return ret;
		}
		else
		{
			E ret = this.oldElements.get(this.currentIndex);
			this.currentIndex++;
			return ret;
		}

	}

	public void remove()
	{
		throw new UnsupportedOperationException();
	}
	
	
	
	
	/////////////////////// PROTECTED PART /////////////////////////////
	
	protected SimpleDuplicateableIterator(SimpleDuplicateableIterator<E> from)
	{
		this.currentIndex = from.currentIndex;
		this.iterator = from.iterator;
		this.oldElements = from.oldElements;
	}

	

	protected Iterator<E> iterator;
	protected ArrayList<E> oldElements;
	protected int currentIndex=0;
	

}
