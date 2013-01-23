package eu.excitementproject.eop.lap.biu.lemmatizer;

/**
 * 
 * @author Asher Stern
 * @since Jan 18, 2011
 *
 */
public class LemmatizerException extends Exception
{
	private static final long serialVersionUID = -1789459162924913026L;

	public LemmatizerException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public LemmatizerException(String message)
	{
		super(message);
	}
}
