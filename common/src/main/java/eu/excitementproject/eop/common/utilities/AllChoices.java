package eu.excitementproject.eop.common.utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import eu.excitementproject.eop.common.datastructures.ObservableStack;
import eu.excitementproject.eop.common.datastructures.SimpleObservableStack;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;



/**
 * See {@link AllChoicesDemo} to understand this class.
 * @author asher
 *
 * @param <T>
 * 
 * @see AllChoicesDemo
 */
public class AllChoices<T>
{
	
	// Nested exception class
	@SuppressWarnings("serial")
	public static class AllChoicesException extends Exception
	{public AllChoicesException(String message){super(message);}
	public AllChoicesException(String message,Throwable cause){super(message,cause);}}
	
	
	public AllChoices(Iterable<T>[] iterablesArray, ChoiceHandler<T> handler) throws AllChoicesException
	{
		super();
		if (null==iterablesArray) throw new AllChoicesException("null==iterablesArray");
		if (null==handler) throw new AllChoicesException("null==handler");
		
		this.iterablesArray = iterablesArray;
		this.handler = handler;
	}
	
	public void run()
	{
		initStack();
		while (!stack.empty())
		{
			handler.handleChoice(getCurrentChoice());
			next();
		}
	}

	
	
	private void initStack()
	{
		stack = new SimpleObservableStack<IterableIteratorItemAndIndex<T>>();
		for (int index=0;index<iterablesArray.length;++index)
		{
			Iterator<T> iterator = iterablesArray[index].iterator();
			T item = null;
			if (iterator.hasNext()) item = iterator.next();
			stack.push(new IterableIteratorItemAndIndex<T>(iterablesArray[index], iterator, item, index));
		}
	}
	
	private void next()
	{
		IterableIteratorItemAndIndex<T> top = stack.pop();
		while ( (!top.iterator.hasNext()) && (!stack.empty()) )
		{
			top = stack.pop();
		}
		if (top.iterator.hasNext())
		{
			stack.push(new IterableIteratorItemAndIndex<T>(top.iterable, top.iterator, top.iterator.next(), top.index));
		}
		
		if (!stack.empty())
		{
			int index = top.index;
			index++;
			
			while (index<iterablesArray.length)
			{
				Iterator<T> iterator = iterablesArray[index].iterator();
				T item = null;
				if (iterator.hasNext()) item = iterator.next();
				IterableIteratorItemAndIndex<T> newStackItem = new IterableIteratorItemAndIndex<T>(iterablesArray[index],iterator,item,index);
				stack.push(newStackItem);
				
				index++;
			}
		}
	}
	
	private List<T> getCurrentChoice()
	{
		ImmutableList<IterableIteratorItemAndIndex<T>> stackList = stack.getAsList();
		List<T> ret = new ArrayList<T>(stackList.size());
		for (IterableIteratorItemAndIndex<T> item : stackList)
		{
			ret.add(item.item);
		}
		return ret;
	}
	
	
	private static class IterableIteratorItemAndIndex<T>
	{
		public IterableIteratorItemAndIndex(Iterable<T> iterable,
				Iterator<T> iterator, T item, int index)
		{
			this.iterable = iterable;
			this.iterator = iterator;
			this.item = item;
			this.index = index;
		}
		public Iterable<T> iterable;
		public Iterator<T> iterator;
		public T item;
		public int index;
	}
	
	private ObservableStack<IterableIteratorItemAndIndex<T>> stack;
	private Iterable<T>[] iterablesArray;
	private ChoiceHandler<T> handler;
}
