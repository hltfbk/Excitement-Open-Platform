package eu.excitementproject.eop.transformations.operations.rules;

@SuppressWarnings("serial")
public class RuleBaseException extends Exception
{
	public RuleBaseException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public RuleBaseException(String message)
	{
		super(message);
	}
}
