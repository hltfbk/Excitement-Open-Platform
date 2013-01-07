package ac.biu.nlp.nlp.engineml.utilities;


/**
 * All exceptions of engineml project should be subclasses of this exception.
 * 
 * @author Asher Stern
 * @since Jan 29, 2012
 *
 */
public abstract class BIUTEEBaseException extends Exception
{
	private static final long serialVersionUID = 6146425305941939566L;

	public BIUTEEBaseException()
	{
		super();
	}

	public BIUTEEBaseException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public BIUTEEBaseException(String message)
	{
		super(message);
	}

	public BIUTEEBaseException(Throwable cause)
	{
		super(cause);
	}
}
