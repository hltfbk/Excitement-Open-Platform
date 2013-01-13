package eu.excitementproject.eop.common.component.lexicalknowledge;

/**
 * Indicates that {@link LexicalResource#close()} failed.
 * 
 * @author Asher Stern
 * @since Jun 26, 2012
 *
 */
@SuppressWarnings("serial")
public class LexicalResourceCloseException extends Exception
{
	public LexicalResourceCloseException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public LexicalResourceCloseException(String message)
	{
		super(message);
	}
}
