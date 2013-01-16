package eu.excitementproject.eop.transformations.operations.rules;

@SuppressWarnings("serial")
public class LexicalRuleBaseCloseException extends Exception
{
	public LexicalRuleBaseCloseException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public LexicalRuleBaseCloseException(String message)
	{
		super(message);
	}
}
