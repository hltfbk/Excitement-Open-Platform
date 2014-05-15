package eu.excitementproject.eop.common.utilities.configuration;


import java.io.File;
import java.io.Serializable;

import eu.excitementproject.eop.common.configuration.CommonConfig;




/**
 * A class for accessing a configuration file.
 * The configuration file has sections (modules), where each section contains key-value parameters.
 * The actual configuration file might be either Excitement configuration file ({@link CommonConfig}) or BIU
 * legacy configuration file.
 * 
 * @author Asher Stern
 * @since Dec 18, 2013
 *
 */
public class ConfigurationFile implements Serializable
{
	private static final long serialVersionUID = -160667366987997027L;

	///////////////////////////////////////////////////////// public section /////////////////////////////////////////////////

	// Constants here for backward compatibility
	public static final String MODULE_ATTRIBUTE_NAME = LegacyConfigurationFile.MODULE_ATTRIBUTE_NAME;
	public static final String MODULE_ELEMENT_NAME = LegacyConfigurationFile.MODULE_ELEMENT_NAME;

	
	/**
	 * Constructor for Excitement configuration file.
	 * @param commonConfig
	 * @throws ConfigurationException 
	 */
	public ConfigurationFile(CommonConfig commonConfig) throws ConfigurationException
	{
		this.underlyingConfigurationFile = new CommonConfigWrapperConfigurationFile(commonConfig,this);
	}
	
	/**
	 * Constructor for BIU legacy configuration file
	 * 
	 * @param iConfigurationXmlFile the input xml file
	 * @throws ConfigurationFileDuplicateKeyException if the iConfigurationXmlFile has duplicate keys 
	 * @throws ConfigurationException If any unexpected JAXBException errors occur while Unmarshaller unmarshalls 
	 */
	public ConfigurationFile(File iConfigurationXmlFile) throws ConfigurationFileDuplicateKeyException, ConfigurationException
	{
		this.underlyingConfigurationFile = new LegacyConfigurationFile(iConfigurationXmlFile,this);
	}
		
	/**
	 * Constructor for BIU legacy configuration file
	 * 
	 * @param iFileName the xml file name
	 * @throws ConfigurationFileDuplicateKeyException if the iConfigurationXmlFile has duplicate keys 
	 * @throws ConfigurationException If any unexpected JAXBException errors occur while Unmarshaller unmarshalls 
	 */
	public ConfigurationFile(String iFileName) throws ConfigurationFileDuplicateKeyException, ConfigurationException 
	{
		this(new File(iFileName));
	}
	
	
	
	

	/**
	 * Indicates whether the {@link ConfigurationParams}s returned by getXXX methods
	 * of this configuration-file will expand environment variables.
	 * Default - <tt>false</tt>. See {@link #setExpandingEnvironmentVariables(boolean)}.
	 * 
	 * @return <tt>true</tt> if {@link ConfigurationParams} returned by this configuration-file
	 * will expand environment variables.
	 */
	public boolean isExpandingEnvironmentVariables()
	{
		return underlyingConfigurationFile.isExpandingEnvironmentVariables();
	}

	/**
	 * Sets whether the {@link ConfigurationParams} returned by getXXX methods of this
	 * configuration-file are set to expand environment variables.
	 * Default - <tt>false</tt>
	 * 
	 * @param expandingEnvironmentVariables
	 */
	public void setExpandingEnvironmentVariables(boolean expandingEnvironmentVariables)
	{
		underlyingConfigurationFile.setExpandingEnvironmentVariables(expandingEnvironmentVariables);
	}
	
	/**
	 * return the ConfigurationFile's main ConfigurationParams  
	 * @return the ConfigurationFile's main ConfigurationParams 
	 */
	public ConfigurationParams getParams()
	{
		return underlyingConfigurationFile.getParams();
	}


	/**
	 * return the ConfigurationParams that match the given module
	 * @param iModuleName
	 * @return the ConfigurationParams for the given module
	 * @throws ConfigurationException if iModuleName doesn't exist in this ConfigurationFile
	 */
	public ConfigurationParams getModuleConfiguration(String iModuleName) throws ConfigurationException
	{
		return underlyingConfigurationFile.getModuleConfiguration(iModuleName);
	}
	
	public boolean isModuleExist(String moduleName) throws ConfigurationException
	{
		return underlyingConfigurationFile.isModuleExist(moduleName);
	}

	/**
	 * create a new entry in the ConfigurationParams table, under the given module name, without content 
	 * @param iModuleName
	 * @throws ConfigurationException if a module already exists by the same name
	 */
	public void addModuleConfiguration(String iModuleName) throws ConfigurationException
	{
		underlyingConfigurationFile.addModuleConfiguration(iModuleName);
	}

	/**
	 * Remove the given module from the ConfigurationParams modules table
	 * @param iModuleName
	 * @throws ConfigurationException if any parameter is null
	 */
	public void removeModuleConfiguration(String iModuleName) throws ConfigurationException
	{			
		underlyingConfigurationFile.removeModuleConfiguration(iModuleName);
	}

	/**
	 * return the file the configuration was read from
	 * @return the file the configuration was read from
	 */
	public File getConfFile()
	{
		return underlyingConfigurationFile.getConfFile();
	}
		
	///////////////////////////////// private methods ///////////////////////////////////////////////
	
	
	
	protected final UnderlyingConfigurationFile underlyingConfigurationFile;
}