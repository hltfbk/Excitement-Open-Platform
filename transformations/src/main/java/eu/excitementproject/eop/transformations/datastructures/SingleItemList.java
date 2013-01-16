package eu.excitementproject.eop.transformations.datastructures;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * TODO I think I can delete this class and use {@link Collections#singletonList(Object)} instead.
 * A java.util.List that can contain exactly one element. Does not support modification
 * methods (i.e. add, remove, etc.).
 * 
 * @author Asher Stern
 * @since Oct 3, 2011
 *
 * @param <E>
 */
public class SingleItemList<E> implements List<E>
{
	public SingleItemList(E item)
	{
		super();
		this.item = item;
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
		if (this.item == o) return true;
		else if ( (null==this.item) || (null==o) ) return false;
		else return this.item.equals(o);
	}

	public Iterator<E> iterator()
	{
		return new SingleItemIterator<E>(this.item);
	}

	public Object[] toArray()
	{
		Object[] ret = new Object[1];
		ret[0]=this.item;
		return ret;
	}

	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a)
	{
        if (a.length < 1)
            a = (T[])java.lang.reflect.Array.newInstance(
                                a.getClass().getComponentType(), 1);
        a[0] = (T) this.item;

        if (a.length > 1)
            a[1] = null;

        return a;
	}

	public boolean add(E e)
	{
		throw new UnsupportedOperationException(this.getClass().getSimpleName()+" does not support modification operations.");
	}

	public boolean remove(Object o)
	{
		throw new UnsupportedOperationException(this.getClass().getSimpleName()+" does not support modification operations.");
	}

	public boolean containsAll(Collection<?> c)
	{
		boolean ret = true;
		for (Object o : c)
		{
			if (!this.contains(o))
				ret = false;
		}
		return ret;
	}

	public boolean addAll(Collection<? extends E> c)
	{
		throw new UnsupportedOperationException(this.getClass().getSimpleName()+" does not support modification operations.");
	}

	public boolean addAll(int index, Collection<? extends E> c)
	{
		throw new UnsupportedOperationException(this.getClass().getSimpleName()+" does not support modification operations.");
	}

	public boolean removeAll(Collection<?> c)
	{
		throw new UnsupportedOperationException(this.getClass().getSimpleName()+" does not support modification operations.");
	}

	public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException(this.getClass().getSimpleName()+" does not support modification operations.");
	}

	public void clear()
	{
		throw new UnsupportedOperationException(this.getClass().getSimpleName()+" does not support modification operations.");
	}

	public E get(int index)
	{
		if (index!=0) throw new IndexOutOfBoundsException();
		else
		{
			return this.item;
		}
	}

	public E set(int index, E element)
	{
		throw new UnsupportedOperationException(this.getClass().getSimpleName()+" does not support modification operations.");
	}

	public void add(int index, E element)
	{
		throw new UnsupportedOperationException(this.getClass().getSimpleName()+" does not support modification operations.");
	}

	public E remove(int index)
	{
		throw new UnsupportedOperationException(this.getClass().getSimpleName()+" does not support modification operations.");
	}

	public int indexOf(Object o)
	{
		if (this.contains(o))
			return 0;
		else
			return -1;
	}

	public int lastIndexOf(Object o)
	{
		if (this.contains(o))
			return 0;
		else
			return -1;
	}

	public ListIterator<E> listIterator()
	{
		return new SingleItemListIterator<E>(this.item);
	}

	public ListIterator<E> listIterator(int index)
	{
		if (0==index)
			return new SingleItemListIterator<E>(this.item);
		else
		{
			throw new IndexOutOfBoundsException();
		}
	}

	public List<E> subList(int fromIndex, int toIndex)
	{
		if ( (fromIndex==0) && (toIndex==1) )
			return this;
		else
		{
			return new ArrayList<E>(0);
		}
	}
	

	private E item;
}
