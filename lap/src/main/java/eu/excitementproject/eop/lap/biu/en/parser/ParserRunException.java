package eu.excitementproject.eop.lap.biu.en.parser;

/**
 * Exception that signals an error when trying to parse a sentence. 
 */
public class ParserRunException extends Exception
{
	private static final long serialVersionUID = 1L;

	public ParserRunException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ParserRunException(String message)
	{
		super(message);
	}
}
