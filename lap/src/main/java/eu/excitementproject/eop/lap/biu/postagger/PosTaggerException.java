package eu.excitementproject.eop.lap.biu.postagger;


/**
 * An exception that might be thrown by {@link PosTagger}.
 * 
 * @see PosTagger
 * 
 * @author Asher Stern
 * @since Jan 10, 2011
 *
 */
public class PosTaggerException extends Exception
{
	private static final long serialVersionUID = 2748614840354387343L;

	public PosTaggerException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public PosTaggerException(String message)
	{
		super(message);
	}
}
