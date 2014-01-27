package eu.excitementproject.eop.common.utilities.configuration;

import java.util.Set;

import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;

/**
 * Wraps Excitement {@link NameValueTable} as a {@link ConfigurationParams}.
 * 
 * @author Asher Stern
 * @since Dec 18, 2013
 *
 */
public class ExcitementConfigurationParams extends AbstractConfigurationParams
{
	public ExcitementConfigurationParams(CommonConfig commonConfig, NameValueTable nameValueTable, String sectionName, boolean expandingEnvironmentVariables, ConfigurationFile configurationFileReference) throws ConfigurationException
	{
		super();
		this.commonConfig = commonConfig;
		this.nameValueTable = nameValueTable;
		this.sectionName = sectionName;
		this.configurationFileReference = configurationFileReference;
		if (null==this.nameValueTable) throw new ConfigurationException("Null NameValueTable was provided.");
		
		this.setExpandingEnvironmentVariables(expandingEnvironmentVariables);
	}
	
	
	
	
	@Override
	public String getModuleName()
	{
		return this.sectionName;
	}
	
	@Override
	public ConfigurationParams getSisterModuleConfiguration(String iModuleName) throws ConfigurationException
	{
		try
		{
			NameValueTable sisterTable = this.commonConfig.getSection(iModuleName);
			return new ExcitementConfigurationParams(this.commonConfig, sisterTable, iModuleName, isExpandingEnvironmentVariables(), this.configurationFileReference);
		}
		catch (eu.excitementproject.eop.common.exception.ConfigurationException e)
		{
			throw new ConfigurationException("Failed to get the require module \""+iModuleName+"\". See nested exception.",e);
		}
	}
	
	public ConfigurationFile getConfigurationFile()
	{
		return this.configurationFileReference;
	}
	
	
	@Override
	public Set<String> keySet()
	{
		return nameValueTable.keySet();
	}
	
	@Override
	public boolean containsKey(Object key)
	{
		return nameValueTable.keySet().contains(key);
	}
	
	
	@Override
	public String put(String key, String value)
	{
		String ret = null;
		try {ret = nameValueTable.getString(key);}
		catch(eu.excitementproject.eop.common.exception.ConfigurationException e){} // the return value would be null. No need to throw an exception.
		nameValueTable.setString(key, value);
		return ret;
	}
	
	@Override
	public void setString(String name, String value)
	{
		this.nameValueTable.setString(name, value);
	}
	
	@Override
	protected String rawGet(String paramName) throws ConfigurationException
	{
		try
		{
			return nameValueTable.getString(paramName);
		}
		catch(eu.excitementproject.eop.common.exception.ConfigurationException e)
		{
			throw new ConfigurationException("Failed to get parameter value. See nested exception.",e);
		}
	}
	
	@Override
	protected String whoAmI_forException()
	{
		String stringConfigurationFile = "Configuration file not available";
		String stringSection = "Section name not available";
		if (commonConfig!=null)
		{
			stringConfigurationFile = "Configuration File: "+commonConfig.getConfigurationFileName();
		}
		if (sectionName!=null)
		{
			stringSection = "Section: "+sectionName;
		}
		return stringConfigurationFile+stringSection;
	}
	
	
	
	protected final CommonConfig commonConfig;
	protected final NameValueTable nameValueTable;
	protected final String sectionName;
	protected final ConfigurationFile configurationFileReference;
}
