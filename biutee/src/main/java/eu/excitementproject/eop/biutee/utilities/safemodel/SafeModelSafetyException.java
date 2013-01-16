package eu.excitementproject.eop.biutee.utilities.safemodel;

/**
 * 
 * @author Asher Stern
 * @since Dec 19, 2012
 *
 */
@SuppressWarnings("serial")
public class SafeModelSafetyException extends RuntimeException
{
	public SafeModelSafetyException(String message)
	{
		super(message);
	}
}
