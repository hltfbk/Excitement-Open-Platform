package eu.excitementproject.eop.biutee.script;

public class ScriptException extends Exception
{
	private static final long serialVersionUID = 6218120466855770545L;

	public ScriptException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ScriptException(String message)
	{
		super(message);
	}
}
