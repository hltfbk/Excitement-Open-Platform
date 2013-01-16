package eu.excitementproject.eop.common.datastructures.immutable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Simple implementation of {@link ImmutableList}.
 * It gets a <code> java.util.List </code> in its constructor,
 * and exposes only "read" operations on that list.
 * <P>
 * Note that the underlying list is the same list
 * supplied in the constructor, and therefore, if the list
 * is changed by another code that holds a reference to that
 * list, the {@link ImmutableList} is changed as well.
 * 
 * This implementation only insures that the code that holds
 * the {@link ImmutableList} cannot change it.
 * @author Asher Stern
 *
 * @param <T>
 */
public class ImmutableListWrapper<T> extends AbstractImmutableCollectionWrapper<T> implements ImmutableList<T>
{
	private static final long serialVersionUID = -3548171688023441969L;

	public ImmutableListWrapper(List<? extends T> realList)
	{
		super(realList);
		this.realList = realList;
	}
	
	
	public T get(int index)
	{
		if (null==this.realList)
			return null;
		else
			return this.realList.get(index);
	}
	
	public int indexOf(Object o)
	{
		if (null==this.realList)
			return -1;
		else
			return this.realList.indexOf(o);
	}
	
	public int lastIndexOf(Object o)
	{
		if (null==this.realList)
			return -1;
		else
			return this.realList.lastIndexOf(o);
	}
	
	public ImmutableList<T> subList(int fromIndex, int toIndex)
	{
		if (null==this.realList)
		{
			if ( (fromIndex==0) && (toIndex==0) )
				return new ImmutableListWrapper<T>(new LinkedList<T>());
			else
				throw new IndexOutOfBoundsException();
		}
		else
			return new ImmutableListWrapper<T>(this.realList.subList(fromIndex, toIndex));
	}
	
	
	@SuppressWarnings("unchecked")
	public List<T> getMutableListCopy()
	{
		if (null==this.realList)
		{
			return new LinkedList<T>();
		}
		else
		{
			List<T> copy = null;
			try
			{
				copy = this.realList.getClass().newInstance();
				copy.addAll(this.realList);
			}
			catch(Exception e)
			{
				copy = new ArrayList<T>(this.realList);
			}
			
			return copy;
		}
	}
	

	public ImmutableList<T> getImmutableListCopy()
	{
		return new ImmutableListWrapper<T>(this.getMutableListCopy());
	}
	
	
	public ImmutableCollection<T> getImmutableCollectionCopy()
	{
		return this.getImmutableListCopy();
		
	}

	public Collection<T> getMutableCollectionCopy()
	{
		return this.getMutableListCopy();
	}
	
	
	public ImmutableListIterator<T> listIterator()
	{
		if (this.realList==null) return new EmptyImmutableListIterator<T>();
		else return new ImmutableListIteratorWrapper<T>(this.realList.listIterator());
	}

	
	public String toString()
	{
		return this.getClass().getSimpleName()+" wrapping "+realList.getClass().getSimpleName()+": "+realList.toString();
	}
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((realList == null) ? 0 : realList.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImmutableListWrapper<?> other = (ImmutableListWrapper<?>) obj;
		if (realList == null)
		{
			if (other.realList != null)
				return false;
		} else if (!realList.equals(other.realList))
			return false;
		return true;
	}









	protected List<? extends T> realList;
}
