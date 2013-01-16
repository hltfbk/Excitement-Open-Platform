package eu.excitementproject.eop.common.utilities;

@SuppressWarnings("serial")
public class TimeOutLiveIOProgramExecutionException extends LiveIOProgramExecutionException
{
	public TimeOutLiveIOProgramExecutionException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public TimeOutLiveIOProgramExecutionException(String message)
	{
		super(message);
	}
}
