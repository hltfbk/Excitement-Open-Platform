package eu.excitementproject.eop.transformations.datastructures;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * TODO: I think I can delete this class and use instead {@link Collections#singleton(Object)}
 * @author Asher Stern
 * @since Sep 2, 2011
 *
 * @param <T>
 */
public class SingleItemSet<T> implements Set<T>, Serializable
{
	private static final long serialVersionUID = 7059020826851863710L;

	public SingleItemSet(T element)
	{
		super();
		this.element = element;
	}

	public int size()
	{
		return 1;
	}

	public boolean isEmpty()
	{
		return false;
	}

	public boolean contains(Object o)
	{
		if (element==o)return true;
		if (element==null)return false;
		return element.equals(o);
	}

	public Iterator<T> iterator()
	{
		return new SingleItemIterator<T>(element);
	}

	public Object[] toArray()
	{
		Object[] ret = new Object[1];
		ret[0]=this.element;
		return ret;
	}

	@SuppressWarnings("unchecked")
	public <TT> TT[] toArray(TT[] a)
	{
        // Estimate size of array; be prepared to see more or fewer elements
        int size = size();
		TT[] r = a.length >= size ? a :
                  (TT[])java.lang.reflect.Array
                  .newInstance(a.getClass().getComponentType(), size);
		
		r[0]=(TT)this.element;
		return r;
	}

	public boolean add(T e)
	{
		throw new UnsupportedOperationException();
	}

	public boolean remove(Object o)
	{
		throw new UnsupportedOperationException();
	}

	public boolean containsAll(Collection<?> c)
	{
		if (c.size()>1)return false;
		boolean foundNotContained = false;
		for (Object o : c)
		{
			if (!element.equals(o))
				foundNotContained=true;
		}
		return !foundNotContained;
	}

	public boolean addAll(Collection<? extends T> c)
	{
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}

	public void clear()
	{
		throw new UnsupportedOperationException();
	}
	
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((element == null) ? 0 : element.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SingleItemSet<?> other = (SingleItemSet<?>) obj;
		if (element == null)
		{
			if (other.element != null)
				return false;
		} else if (!element.equals(other.element))
			return false;
		return true;
	}



	private T element;
}
