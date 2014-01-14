package eu.excitementproject.eop.common.utilities.configuration;

import java.util.Set;

import eu.excitementproject.eop.common.datastructures.KeyCaseInsensitiveHashTable;

/**
 * Accesses key-value parameters from a module in a configuration file.
 * Used for BIU legacy configuration file.
 * 
 * @author Copy of BIU legacy code, and Amnon Lotan code + reorganizations by Asher Stern
 *
 */
public class LegacyConfigurationParams extends AbstractConfigurationParams
{
	public LegacyConfigurationParams(KeyCaseInsensitiveHashTable<String> parametersHashTable, ConfigurationFile configurationFile, String moduleName) throws ConfigurationException
	{
		this.parametersHashTable = parametersHashTable;
		this.configurationFile = configurationFile;
		this.moduleName = moduleName;
		
		if (null==this.parametersHashTable) throw new ConfigurationException("Null KeyCaseInsensitiveHashTable was provided.");
	}
	
	public LegacyConfigurationParams() throws ConfigurationException
	{
		this(new KeyCaseInsensitiveHashTable<String>(),null,null);
	}

	@Override
	public String getModuleName()
	{
		return this.moduleName;
	}

	@Override
	public ConfigurationParams getSisterModuleConfiguration(String iModuleName) throws ConfigurationException
	{
		if (null==configurationFile) {throw new ConfigurationException("Cannot provide sister module, since configuration file is unavailable.");}
		return this.configurationFile.getModuleConfiguration(iModuleName);
	}
	
	@Override
	public ConfigurationFile getConfigurationFile()
	{
		return this.configurationFile;
	}


	@Override
	public void setString(String name, String value)
	{
		this.parametersHashTable.put(name, value);
	}
	
	
	public Set<String> keySet()
	{
		return this.parametersHashTable.keySet();
	}
	
	public boolean containsKey(Object key)
	{
		return this.parametersHashTable.containsKey(key);
	}
	
	public String put(String key, String value)
	{
		return this.parametersHashTable.put(key, value);
	}
	
	

	
	
	
	


	@Override
	protected String rawGet(String paramName) throws ConfigurationException
	{
		return this.parametersHashTable.get(paramName);
	}

	@Override
	protected String whoAmI_forException()
	{
		String stringConfigurationFile = "Unknown configuration file";
		String stringModule = "Unknown module";
		if (configurationFile!=null)
		{
			stringConfigurationFile = "Configuration file: "+this.configurationFile.getConfFile().getAbsolutePath();
		}
		if (moduleName!=null)
		{
			stringModule = " Module: "+moduleName;
		}
		return stringConfigurationFile+stringModule;
	}

	
	
	protected final KeyCaseInsensitiveHashTable<String> parametersHashTable;
	protected final ConfigurationFile configurationFile;
	protected final String moduleName;
}
