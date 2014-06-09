/**
 * 
 */
package eu.excitementproject.eop.common.utilities.configuration;

/**
 * <B>This class has to be removed, since Excitement uses another type of
 * configuration files.</B>
 * 
 * @author Amnon Lotan
 * @since Dec 7, 2010
 *
 */
@SuppressWarnings("serial")
public class ConfigurationFileDuplicateKeyException extends ConfigurationException {
	public ConfigurationFileDuplicateKeyException(String message)
	{
		super(message);
	}
}
