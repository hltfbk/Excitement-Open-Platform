package eu.excitementproject.eop.common.datastructures;

/**
 * A generic holder for an object of any type.
 * May be used for implementing variations of a Singleton.
 * 
 * @author Asher Stern
 * @since Nov 14, 2012
 *
 * @param <T> the inner object's type
 */
public class Envelope<T>
{
	public T getT()
	{
		return t;
	}
	
	public void setT(T t)
	{
		this.t = t;
	}

	private T t = null;
}
