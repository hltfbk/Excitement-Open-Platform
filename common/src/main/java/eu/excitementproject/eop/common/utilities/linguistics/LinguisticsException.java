package eu.excitementproject.eop.common.utilities.linguistics;

/**
 * 
 * @author Asher Stern
 * @since October 9 2012
 *
 */
@SuppressWarnings("serial")
public class LinguisticsException extends Exception
{
	public LinguisticsException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public LinguisticsException(String message)
	{
		super(message);
	}
}
