package eu.excitementproject.eop.common.representation.coreference;

/**
 * 
 * @author Asher Stern
 *
 */
@SuppressWarnings("serial")
public class TreeCoreferenceInformationException extends Exception
{
	public TreeCoreferenceInformationException(String message)
	{
		super(message);
	}

	public TreeCoreferenceInformationException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
