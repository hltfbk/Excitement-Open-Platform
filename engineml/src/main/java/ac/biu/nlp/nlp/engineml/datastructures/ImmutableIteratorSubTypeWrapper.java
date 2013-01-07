package ac.biu.nlp.nlp.engineml.datastructures;

import ac.biu.nlp.nlp.general.immutable.ImmutableIterator;

/**
 * 
 * @author Asher Stern
 * @since Dec 24, 2012
 *
 * @param <T>
 * @param <U>
 */
public class ImmutableIteratorSubTypeWrapper<T, U extends T> extends ImmutableIterator<T>
{
	public ImmutableIteratorSubTypeWrapper(ImmutableIterator<U> realIterator)
	{
		super();
		this.realIterator = realIterator;
	}


	@Override
	public boolean hasNext()
	{
		return realIterator.hasNext();
	}

	@Override
	public T next()
	{
		return realIterator.next();
	}
	
	
	private final ImmutableIterator<U> realIterator;
}
