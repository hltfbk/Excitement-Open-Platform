package ac.biu.nlp.nlp.instruments.parse.tree.match;

public class MatcherException extends Exception
{
	private static final long serialVersionUID = 7549280518576400763L;

	public MatcherException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public MatcherException(String message)
	{
		super(message);
	}
}
