/**
 * 
 */
package eu.excitementproject.eop.common.utilities.configuration;

/**
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
