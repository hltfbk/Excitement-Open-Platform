package eu.excitementproject.eop.common.utilities.configuration;

import java.io.File;
import java.io.Serializable;

/**
 * Interface of configuration file wrapper, used for both Excitement configuration file and
 * BIU legacy configuration file.
 * 
 * @author Asher Stern
 * @since Dec 18, 2013
 *
 */
public interface UnderlyingConfigurationFile extends Serializable
{
	/**
	 * Indicates whether the {@link ConfigurationParams}s returned by getXXX methods
	 * of this configuration-file will expand environment variables.
	 * Default - <tt>false</tt>. See {@link #setExpandingEnvironmentVariables(boolean)}.
	 * 
	 * @return <tt>true</tt> if {@link ConfigurationParams} returned by this configuration-file
	 * will expand environment variables.
	 */
	public boolean isExpandingEnvironmentVariables();
	
	/**
	 * Sets whether the {@link ConfigurationParams} returned by getXXX methods of this
	 * configuration-file are set to expand environment variables.
	 * Default - <tt>false</tt>
	 * 
	 * @param expandingEnvironmentVariables
	 */
	public void setExpandingEnvironmentVariables(boolean expandingEnvironmentVariables);
	
	/**
	 * return the ConfigurationFile's main ConfigurationParams  
	 * @return the ConfigurationFile's main ConfigurationParams 
	 */
	public ConfigurationParams getParams();
	
	
	/**
	 * return the ConfigurationParams that match the given module
	 * @param iModuleName
	 * @return the ConfigurationParams for the given module
	 * @throws ConfigurationException if iModuleName doesn't exist in this ConfigurationFile
	 */
	public ConfigurationParams getModuleConfiguration(String iModuleName) throws ConfigurationException;
	
	public boolean isModuleExist(String moduleName) throws ConfigurationException;
	
	/**
	 * create a new entry in the ConfigurationParams table, under the given module name, without content 
	 * @param iModuleName
	 * @throws ConfigurationException if a module already exists by the same name
	 */
	public void addModuleConfiguration(String iModuleName) throws ConfigurationException;
	
	
	/**
	 * Remove the given module from the ConfigurationParams modules table
	 * @param iModuleName
	 * @throws ConfigurationException if any parameter is null
	 */
	public void removeModuleConfiguration(String iModuleName) throws ConfigurationException;
	
	/**
	 * return the file the configuration was read from
	 * @return the file the configuration was read from
	 */
	public File getConfFile();
	
	
}
