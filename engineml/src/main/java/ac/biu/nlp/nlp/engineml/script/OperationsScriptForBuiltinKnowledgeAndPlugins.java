package ac.biu.nlp.nlp.engineml.script;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.codeannotations.NotThreadSafe;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMapWrapper;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;

import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.plugin.InstanceBasedPlugin;
import ac.biu.nlp.nlp.engineml.plugin.InstanceBasedPluginFactory;
import ac.biu.nlp.nlp.engineml.plugin.Plugin;
import ac.biu.nlp.nlp.engineml.plugin.PluginAdministrationException;
import ac.biu.nlp.nlp.engineml.plugin.PluginException;
import ac.biu.nlp.nlp.engineml.plugin.PluginFactory;
import ac.biu.nlp.nlp.engineml.plugin.PluginRegistry;
import ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames;

/**
 * Initializes all {@link Plugin}s, in addition to the initialization of all
 * knowledge resources, performed by the base-class {@link OperationsScriptForBuiltinKnowledge}.
 * 
 * @author Asher Stern
 * @since Dec 4, 2012
 *
 */
@NotThreadSafe
public abstract class OperationsScriptForBuiltinKnowledgeAndPlugins extends OperationsScriptForBuiltinKnowledge
{
	public OperationsScriptForBuiltinKnowledgeAndPlugins(ConfigurationFile configurationFile,PluginRegistry pluginRegistry)
	{
		super(configurationFile);
		this.pluginRegistry = pluginRegistry;
	}

	@Override
	public void init() throws OperationException
	{
		super.init();

		try
		{
			// Initialization of plug-ins. A plug-in is used in the
			// system if it is registered (See PluginRegisterer),
			// AND it appears in the list of plug-ins to apply in the
			// configuration file.
			if (knowledgeResourcesParams.containsKey(ConfigurationParametersNames.PLUGINS_TO_APPLY))
			{
				pluginsToApply = knowledgeResourcesParams.getStringArray(ConfigurationParametersNames.PLUGINS_TO_APPLY);
			}
			else
			{
				pluginsToApply = new String[0];
			}
			// Initialize the plug-ins. Each plug-in should be
			// constructed.
			initPlugins(pluginsToApply);

		}
		catch (ConfigurationException e)
		{
			throw new OperationException("Failed to initialize Operations-Script. See nested exception.", e);
		}
		catch (PluginAdministrationException e)
		{
			throw new OperationException("Failed to initialize Operations-Script. See nested exception.", e);
		}
		catch (PluginException e)
		{
			throw new OperationException("Failed to initialize Operations-Script. See nested exception.", e);
		}
	}

	@Override
	public void cleanUp()
	{
		super.cleanUp();
	}
	
	
	private void initPlugins(String[] pluginsToApply) throws PluginAdministrationException, PluginException, OperationException
	{
		Set<String> initializedPlugins = new LinkedHashSet<String>();
		Set<String> pluginsToApplySet = new HashSet<String>();
		for (String pluginToApply : pluginsToApply) pluginsToApplySet.add(pluginToApply);
		Map<String, Plugin> mapOfPlugins = new LinkedHashMap<String, Plugin>();
		Set<InstanceBasedPlugin> setOfInstanceBasedPlugins = new LinkedHashSet<InstanceBasedPlugin>();
		ImmutableMap<String, PluginFactory> pluginFactories = pluginRegistry.getRegisteredPlugins();
		ImmutableMap<String, InstanceBasedPluginFactory> instanceBasedPluginFactories = pluginRegistry.getImmutableRegisteredInstanceBasedPlugins();

		for (String pluginId : pluginFactories.keySet())
		{
			if (pluginsToApplySet.contains(pluginId))
			{
				initializedPlugins.add(pluginId);
				PluginFactory pluginFactory = pluginFactories.get(pluginId);
				Plugin plugin = pluginFactory.createPlugin();
				mapOfPlugins.put(pluginId, plugin);
			}
		}
		for (String pluginId : instanceBasedPluginFactories.keySet())
		{
			if (pluginsToApplySet.contains(pluginId))
			{
				initializedPlugins.add(pluginId);
				InstanceBasedPluginFactory pluginFactory = instanceBasedPluginFactories.get(pluginId);
				InstanceBasedPlugin plugin = pluginFactory.createPlugin();
				mapOfPlugins.put(pluginId, plugin);
				setOfInstanceBasedPlugins.add(plugin);
			}
		}

		mapPlugins = new ImmutableMapWrapper<String, Plugin>(mapOfPlugins);
		instanceBasedPlugins = new ImmutableSetWrapper<InstanceBasedPlugin>(setOfInstanceBasedPlugins);

		pluginsToApplySet.removeAll(initializedPlugins);
		if (pluginsToApplySet.size()>0)
		{
			throw new OperationException("Some plugins were not initialized, since they were not registered, though the user did specify them to be performed:" +
					"\nMissed plugins are:\n"+pluginsToApplySet.toString());
		}
	}

	protected PluginRegistry pluginRegistry = null;
	protected String[] pluginsToApply = null;
}
