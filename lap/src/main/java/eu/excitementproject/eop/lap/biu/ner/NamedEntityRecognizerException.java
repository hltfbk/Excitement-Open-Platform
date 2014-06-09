package eu.excitementproject.eop.lap.biu.ner;

/**
 * Exception class thrown by {@link NamedEntityRecognizer} in any error case.
 * 
 * @author Asher Stern
 *
 */
public class NamedEntityRecognizerException extends Exception
{
	private static final long serialVersionUID = 1L;

	public NamedEntityRecognizerException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public NamedEntityRecognizerException(String message)
	{
		super(message);
	}

}
