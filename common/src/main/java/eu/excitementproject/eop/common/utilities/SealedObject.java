package eu.excitementproject.eop.common.utilities;

/**
 * Wraps an object, such that {@link #equals(Object)} and {@link #hashCode()}
 * methods refer to this object only.
 * This class is used only for {@link #equals(Object)} and {@link #hashCode()}.
 * Nothing else. It is used when the object itself is no longer of interest, but
 * the programmer only wants to remember that it existed. 
 * 
 * @author Asher Stern
 * @since Feb 29, 2012
 *
 * @param <T>
 */
public final class SealedObject<T>
{
	public SealedObject(T t)
	{
		super();
		this.t = t;
	}

	public boolean equals(Object obj)
	{
		if (null==obj)return false;
		if (obj instanceof SealedObject)
		{
			if (t==((SealedObject<?>)obj).t) return true;
			if (null==t) return false;
			return t.equals(((SealedObject<?>)obj).t);
		}
		return false;
	}
	
	public int hashCode()
	{
		if (null==t)return 0;
		return t.hashCode();
	}
	
	private final T t;
}
