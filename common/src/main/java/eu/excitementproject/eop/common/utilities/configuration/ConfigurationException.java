package eu.excitementproject.eop.common.utilities.configuration;

/**
 * <B>This class has to be removed, since Excitement uses another type of
 * configuration files.</B>
 * 
 * An Exception for errors raised in ConfigurationParams or ConfigurationFile.
 * Generally, these errors are limited to problems with the given xml
 * 
 * @author Amnon Lotan
 * @since Dec 7, 2010
 *
 */
public class ConfigurationException extends Exception {
	public ConfigurationException(String iDesc)
	{
		super(iDesc);
	}
	
	public ConfigurationException(String iDesc, Throwable iOther)
	{
		super(iDesc, iOther);
	}
	
	private static final long serialVersionUID = -5090795228682356498L;
}
