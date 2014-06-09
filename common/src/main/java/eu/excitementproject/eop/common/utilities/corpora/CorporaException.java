package eu.excitementproject.eop.common.utilities.corpora;

/**
 * 
 * @author Asher Stern
 * @since Oct 18, 2012
 *
 */
public class CorporaException extends Exception
{
	private static final long serialVersionUID = 6811051983249212142L;

	public CorporaException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public CorporaException(String message)
	{
		super(message);
	}
}
