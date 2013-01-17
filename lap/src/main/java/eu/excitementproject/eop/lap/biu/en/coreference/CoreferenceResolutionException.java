package eu.excitementproject.eop.lap.biu.en.coreference;

@SuppressWarnings("serial")
public class CoreferenceResolutionException extends Exception
{
	public CoreferenceResolutionException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public CoreferenceResolutionException(String message)
	{
		super(message);
	}
}
