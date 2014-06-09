package eu.excitementproject.eop.common.utilities;

public class ProgramExecutionException extends Exception
{
	private static final long serialVersionUID = 1L;

	public ProgramExecutionException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ProgramExecutionException(String message)
	{
		super(message);
	}
}
