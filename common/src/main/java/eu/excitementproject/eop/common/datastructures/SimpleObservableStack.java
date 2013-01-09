package eu.excitementproject.eop.common.datastructures;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableListWrapper;



/**
 * A <B> non thread safe </B> simple implementation of {@link ObservableStack}.
 * 
 * <P><B>!!!!!!!!!!! NOT THREAD SAFE !!!!!!!!!!!!!</B>
 * 
 * @author Asher Stern
 *
 * @param <E>
 */
public class SimpleObservableStack<E> implements ObservableStack<E>
{
	private static final long serialVersionUID = -8364068817035189197L;
	
	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ObservableStack#empty()
	 */
	public boolean empty()
	{
		return list.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ObservableStack#getAsList()
	 */
	public ImmutableList<E> getAsList()
	{
		return new ImmutableListWrapper<E>(this.list);
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ObservableStack#peek()
	 */
	public E peek() throws EmptyStackException
	{
		if (nextIndex<=0) throw new EmptyStackException();
		return list.get((nextIndex-1));
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ObservableStack#pop()
	 */
	public E pop() throws EmptyStackException
	{
		E ret = null;
		if (nextIndex<=0) throw new EmptyStackException();
		ret = list.get((nextIndex-1));
		list.remove((nextIndex-1));
		nextIndex--;
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.ObservableStack#push(java.lang.Object)
	 */
	public E push(E item)
	{
		list.add(item);
		++nextIndex;
		return item;
	}

	protected List<E> list = new ArrayList<E>(); // I use ArrayList to get the last element in O(1) time.
	protected int nextIndex = 0;
}
