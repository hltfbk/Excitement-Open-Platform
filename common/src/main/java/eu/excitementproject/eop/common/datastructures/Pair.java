package eu.excitementproject.eop.common.datastructures;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;


/**
 * An unordered pair of elements.
 * <P>
 * Immutable
 * @author Asher Stern
 *
 * @param <T>
 */
public class Pair<T> implements Serializable, Iterable<T>
{
	private static final long serialVersionUID = -5208740163856925391L;
	////////////////// PUBLIC CONSTRUCTOR AND METHODS ////////////////////
	
	public Pair(T element1, T element2)
	{
		this.element1 = element1;
		this.element2 = element2;
	}
	
	public boolean contains(T element)
	{
		boolean ret = false;
		if (null==element)
		{
			if ((null==element1) || (null==element2) )
				ret = true;
			else
				ret = false;
		}
		else
		{
			ret = ( element.equals(element1) || element.equals(element2) );
		}
		return ret;
	}
	
	public HashSet<T> toSet()
	{
		HashSet<T> ret = new LinkedHashSet<T>();
		ret.add(element1);
		ret.add(element2);
		return ret;
	}
	
	public Iterator<T> iterator()
	{
		return new PairIterator<T>(element1, element2);
	}
	
	// equals() and hashCode() implementations (not so trivial)

	@Override
	public int hashCode()
	{
		if (hashValueSet)
			return hashValue;
		else
		{
			final int prime = 31;
			int result = 1;
			int hashElement1 = ((element1 == null) ? 0 : element1.hashCode());
			int hashElement2 = ((element2 == null) ? 0 : element2.hashCode());
			int hashElementsXor = hashElement1^hashElement2;
			result = prime * result + hashElementsXor;
			
			// It is thread safe. Think about it.
			hashValue = result;
			hashValueSet = true;
			
			return result;
		}
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		Pair other = (Pair) obj;
		
		boolean eq1to1 = false;
		if (element1 == null)
		{
			if (other.element1 != null)
				eq1to1 =  false;
		}
		else eq1to1 = element1.equals(other.element1);

		boolean eq1to2 = false;
		if (element1 == null)
		{
			if (other.element2 != null)
				eq1to2 =  false;
		}
		else eq1to2 = element1.equals(other.element2);

		boolean eq2to1 = false;
		if (element2 == null)
		{
			if (other.element1 != null)
				eq2to1 =  false;
		}
		else eq2to1 = element2.equals(other.element1);

		boolean eq2to2 = false;
		if (element2 == null)
		{
			if (other.element2 != null)
				eq2to2 =  false;
		}
		else eq2to2 = element2.equals(other.element2);
		
		return ( ( eq1to1 && eq2to2 ) || ( eq1to2 && eq2to1 ) ); 
		

	}
	
	
	////////////////// PROTECTED & PRIVATE PART /////////////////
	
	protected T element1;
	protected T element2;
	
	transient private int hashValue = 1;
	transient private boolean hashValueSet=false;
	

}
