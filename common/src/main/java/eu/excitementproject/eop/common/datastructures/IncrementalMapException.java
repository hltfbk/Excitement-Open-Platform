package eu.excitementproject.eop.common.datastructures;


/**
 * Thrown by {@link IncrementalMap}, usually if the
 * condition of "being incremental" is violated.
 * 
 * @author Asher Stern
 */
@SuppressWarnings("serial")
public class IncrementalMapException extends Exception
{
	public IncrementalMapException(String str,Object key)
	{
		super(str);
		this.key = key;
	}
	
	public Object getKey()
	{
		return key;
	}
	
	protected final Object key;
}
