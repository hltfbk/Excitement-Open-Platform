package eu.excitementproject.eop.core.utilities.dictionary.wordnet;

@SuppressWarnings("serial")
public class EmptySynsetException extends WordNetException
{

	public EmptySynsetException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public EmptySynsetException(String message)
	{
		super(message);
	}

}
