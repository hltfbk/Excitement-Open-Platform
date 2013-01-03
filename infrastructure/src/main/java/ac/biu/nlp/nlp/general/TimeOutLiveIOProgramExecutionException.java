package ac.biu.nlp.nlp.general;

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
