package eu.excitementproject.eop.common.configuration;

import java.io.File;
import java.util.List;

import eu.excitementproject.eop.common.exception.ConfigurationException;



/**
 * This abstract class outlines the methods and capability of 
 * EXCITEMENT CommonConfiguration. This is described with an abstract class, 
 * but WP4 may decide to replace this with a concrete implementation.  
 * 
 * @author Gil
 */
/**
 * @author tailblues
 *
 */
public abstract class CommonConfig 
{
	/**
	 * Load a common configuration from XML configuration file 
	 * @param f
	 */
	public CommonConfig(File f) 
	{
		
	}
	
	/**
	 * Start a new, empty configuration file that can be set and saved,  
	 * 
	 */
	public CommonConfig()
	{
		
	}
	
	/**
	 * Returns a list of sections in this configuration file.
	 * Returns null if the configuration file was not specified.
	 * @return a list of sections in this configuration file
	 */
	abstract public List<String> getSectionNames();
	
	/**
	 * This method returns the name-value table that is associated with the componentName. If there is no such section, the method
will raise an exception.
	 * @param componentName
	 * @return
	 * @throws ConfigurationException
	 */
	abstract public NameValueTable getSection(String componentName) throws ConfigurationException;
	
	/**This method returns the name-value table that is associated with the componentName and instanceName.
	 * @param componentName
	 * @param instanceName
	 * @return
	 * @throws ConfigurationException
	 */
	abstract public NameValueTable getSubSection(String componentName, String instanceName) throws ConfigurationException;
		
	/**
	 * This method saves current configuration to an XML file. It will save whole values as a single XML file. Note that this save method should provide a safety mechanism
that will prevent overwriting existing XML configurations (ie. only permitting generating new configuration
files, etc).
Note that saveConfiguration() method is provided mainly for user level or transduction layer level
access. The methods are not expected to be called from a entailment core component.
	 * @param f
	 * @throws ConfigurationException
	 */
	abstract public void saveConfiguration(File f) throws ConfigurationException;
	
	/**
	 * This method is a convenience method that returns the
full path name of the current configuration file. Will return null, if the configuration is not originated from
a file and never saved before.
	 * @return
	 */
	abstract public String getConfigurationFileName(); 
} 
