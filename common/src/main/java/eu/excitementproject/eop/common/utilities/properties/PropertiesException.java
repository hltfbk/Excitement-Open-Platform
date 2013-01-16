package eu.excitementproject.eop.common.utilities.properties;

@SuppressWarnings("serial")
public class PropertiesException extends Exception
{
	public PropertiesException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public PropertiesException(String message)
	{
		super(message);
	}
}
