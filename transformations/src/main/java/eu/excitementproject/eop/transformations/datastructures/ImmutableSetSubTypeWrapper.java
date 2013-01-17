package eu.excitementproject.eop.transformations.datastructures;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableCollection;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;


/**
 * 
 * @author Asher Stern
 * @since Dec 24, 2012
 *
 * @param <T>
 * @param <U>
 */
public class ImmutableSetSubTypeWrapper<T, U extends T> implements ImmutableSet<T> 
{
	private static final long serialVersionUID = -4135759995385716124L;


	public ImmutableSetSubTypeWrapper(ImmutableSet<U> realSet)
	{
		super();
		this.realSet = realSet;
	}



	@Override
	public boolean contains(Object o)
	{
		return realSet.contains(o);
	}



	@Override
	public boolean containsAll(Collection<?> c)
	{
		return realSet.containsAll(c);
	}



	@Override
	public boolean containsAll(ImmutableCollection<?> c)
	{
		return realSet.containsAll(c);
	}



	@Override
	public boolean isEmpty()
	{
		return realSet.isEmpty();
	}



	@Override
	public ImmutableIterator<T> iterator()
	{
		return new ImmutableIteratorSubTypeWrapper<T,U>(realSet.iterator());
	}



	@Override
	public int size()
	{
		return realSet.size();
	}



	@Override
	public T[] toArray(T[] a)
	{
		@SuppressWarnings("unchecked")
		T[] ret = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), realSet.size());
		int index=0;
		for (T t : realSet)
		{
			ret[index] = t;
			++index;
		}
		return ret;
	}



	@Override
	public Collection<T> getMutableCollectionCopy()
	{
		return getMutableSetCopy();
	}



	@Override
	public ImmutableCollection<T> getImmutableCollectionCopy()
	{
		return getImmutableSetCopy();
	}



	@Override
	public Set<T> getMutableSetCopy()
	{
		Set<T> ret = new LinkedHashSet<T>();
		for (T t : realSet)
		{
			ret.add(t);
		}
		return ret;
	}



	@Override
	public ImmutableSet<T> getImmutableSetCopy()
	{
		return new ImmutableSetWrapper<T>(getMutableSetCopy());
	}
	
	
	private final ImmutableSet<U> realSet;
}
