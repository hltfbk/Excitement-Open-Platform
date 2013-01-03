package ac.biu.nlp.nlp.instruments.dictionary.wordnet;

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
