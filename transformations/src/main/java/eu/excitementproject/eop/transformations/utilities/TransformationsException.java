package eu.excitementproject.eop.transformations.utilities;


/**
 * All exceptions of transformation-based classes should be subclasses of this exception.
 * 
 * @author Asher Stern
 * @since Jan 29, 2012
 *
 */
public abstract class TransformationsException extends Exception
{
	private static final long serialVersionUID = 6146425305941939566L;

	public TransformationsException()
	{
		super();
	}

	public TransformationsException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public TransformationsException(String message)
	{
		super(message);
	}

	public TransformationsException(Throwable cause)
	{
		super(cause);
	}
}
