package eu.excitementproject.eop.common.utilities.configuration;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;

/**
 * 
 * @author Asher Stern
 * @since Dec 18, 2013
 *
 */
public class CommonConfigWrapperConfigurationFile implements UnderlyingConfigurationFile
{
	private static final long serialVersionUID = 1632860238502900978L;
	
	public CommonConfigWrapperConfigurationFile(CommonConfig commonConfig, ConfigurationFile configurationFileReference) throws ConfigurationException
	{
		this.commonConfig = commonConfig;
		this.configurationFileReference = configurationFileReference;
		
		// Make sure there are no duplicate sections
		List<String> listSectionNames = this.commonConfig.getSectionNames();
		if (null==listSectionNames) {throw new ConfigurationException("The given CommonConfig has no contents (null list of sections).");}
		sectionNames = new LinkedHashSet<>();
		for (String sectionName : listSectionNames)
		{
			if (sectionNames.contains(sectionName)) {throw new ConfigurationException("Duplicate section has been detected: "+sectionName);}
			sectionNames.add(sectionName);
		}
		sectionNames = Collections.unmodifiableSet(sectionNames);
	}

	@Override
	public boolean isExpandingEnvironmentVariables()
	{
		return expandingEnvironmentVariables;
	}

	@Override
	public void setExpandingEnvironmentVariables(boolean expandingEnvironmentVariables)
	{
		this.expandingEnvironmentVariables = expandingEnvironmentVariables;
	}

	@Override
	public ConfigurationParams getParams()
	{
		return null;
	}

	@Override
	public ConfigurationParams getModuleConfiguration(String iModuleName) throws ConfigurationException
	{
		try
		{
			if (sectionNames.contains(iModuleName))
			{
				NameValueTable table = null;
				synchronized(this)
				{
					if (sectionTables.containsKey(iModuleName))
					{
						table = sectionTables.get(iModuleName);
					}
					else
					{
						table = commonConfig.getSection(iModuleName);
						sectionTables.put(iModuleName, table);
					}
				}
				return new ExcitementConfigurationParams(commonConfig, table, iModuleName, expandingEnvironmentVariables, configurationFileReference);
			}
			else
			{
				throw new ConfigurationException("The module \""+iModuleName+"\" does not exist in the configuration file.");
			}
		}
		catch (eu.excitementproject.eop.common.exception.ConfigurationException e)
		{
			throw new ConfigurationException("Failed to get module "+iModuleName+". See nested exception.",e);
		}
	}

	@Override
	public boolean isModuleExist(String moduleName) throws ConfigurationException
	{
		try
		{
			commonConfig.getSection(moduleName);
			return true;
		}
		catch(eu.excitementproject.eop.common.exception.ConfigurationException e)
		{
			return false;
		}
	}

	@Override
	public void addModuleConfiguration(String iModuleName) throws ConfigurationException
	{
		throw new ConfigurationException("Operation addModuleConfiguration is not supported in this implementation ("+this.getClass().getSimpleName()+").");
	}

	@Override
	public void removeModuleConfiguration(String iModuleName) throws ConfigurationException
	{
		throw new ConfigurationException("Operation removeModuleConfiguration is not supported in this implementation ("+this.getClass().getSimpleName()+").");
	}

	@Override
	public File getConfFile()
	{
		return new File(commonConfig.getConfigurationFileName());
	}

	protected final CommonConfig commonConfig;
	protected final ConfigurationFile configurationFileReference;
	protected boolean expandingEnvironmentVariables = false;
	protected Set<String> sectionNames = null;
	protected Map<String, NameValueTable> sectionTables = new LinkedHashMap<>();
}
