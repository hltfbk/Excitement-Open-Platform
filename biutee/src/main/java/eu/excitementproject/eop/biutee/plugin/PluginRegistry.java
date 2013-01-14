package eu.excitementproject.eop.biutee.plugin;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMapWrapper;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.transformations.datastructures.DsUtils;

/**
 * Contains all the factories for all plug-ins registered in the system.
 * The main capability of this class is to return the plug-ins factories, from which
 * the plug-ins are instantiated.
 * <P>
 * To register plug-in, the user has to implement a sub-class of {@link PluginRegisterer},
 * and in {@link PluginRegisterer#register()} call the methods:
 * {@link PluginRegistry#registerPlugin(PluginFactory)} and {@link PluginRegistry#registerInstanceBasedPlugin(InstanceBasedPluginFactory)}.
 * 
 * @see PluginRegisterer
 *  
 * 
 * @author Asher Stern
 * @since Jan 27, 2012
 *
 */
public class PluginRegistry
{
	///////////////// PUBLIC ///////////////// 
	
	public synchronized void registerPlugin(PluginFactory pluginFactory) throws PluginAdministrationException
	{
		registerAbstractPluginFactory(pluginFactory,registeredPlugins);
	}
	public synchronized void registerInstanceBasedPlugin(InstanceBasedPluginFactory pluginFactory) throws PluginAdministrationException
	{
		registerAbstractPluginFactory(pluginFactory,registeredInstanceBasedPlugins);
	}
	
	
	public synchronized void sealRegistry() throws PluginAdministrationException
	{
		this.sealed = true;
		sortedCustomFeatures = DsUtils.createSortedImmutableList(customFeatures);
		immutableRegisteredPlugins = new ImmutableMapWrapper<String, PluginFactory>(registeredPlugins);
		immutableRegisteredInstanceBasedPlugins = new ImmutableMapWrapper<String, InstanceBasedPluginFactory>(registeredInstanceBasedPlugins);
	}
	
	

	public ImmutableList<String> getSortedCustomFeatures() throws PluginAdministrationException
	{
		if (!sealed) throw new PluginAdministrationException("Plugin registry has not been sealed yet.");
		return sortedCustomFeatures;
	}
	
	public ImmutableMap<String, PluginFactory> getRegisteredPlugins() throws PluginAdministrationException
	{
		if (!sealed) throw new PluginAdministrationException("Plugin registry has not been sealed yet.");
		return immutableRegisteredPlugins;
	}
	public ImmutableMap<String, InstanceBasedPluginFactory> getImmutableRegisteredInstanceBasedPlugins() throws PluginAdministrationException
	{
		if (!sealed) throw new PluginAdministrationException("Plugin registry has not been sealed yet.");
		return immutableRegisteredInstanceBasedPlugins;
	}



	///////////////// PROTECTED & PRIVATE ///////////////// 

	
	protected <P extends Plugin, T extends AbstractPluginFactory<P>> void registerAbstractPluginFactory(T pluginFactory, Map<String, T> mapRegistered) throws PluginAdministrationException
	{
		if (sealed) throw new PluginAdministrationException("Plugin registry has been sealed. No additional plugins are allowed.");
		if (null==pluginFactory) throw new PluginAdministrationException("Null pluginFactory");
		String id = pluginFactory.getPluginId();
		if (null==id) throw new PluginAdministrationException("Null id");
		if (pluginsIds.contains(id)) throw new PluginAdministrationException("plugin with id \""+id+"\" already exists.");
		// if (mapRegistered.containsKey(id)) throw new PluginAdministrationException("plugin with id \""+id+"\" already exists.");
		mapRegistered.put(id, pluginFactory);
		pluginsIds.add(id);
		ImmutableSet<String> customFeaturesOfPlugin = pluginFactory.getCustomFeatures();
		if (customFeaturesOfPlugin!=null)
		{
			for (String customFeature : customFeaturesOfPlugin)
			{
				if (customFeatures.contains(customFeature)) throw new PluginAdministrationException("Feature: \""+customFeature+"\" already exist as a plug-in feature. It seems that two plug-ins use the same feature name: \""+customFeature+"\"");
				customFeatures.add(customFeature);
			}
		}
	}




	private final Set<String> pluginsIds = new LinkedHashSet<String>();
	private final Map<String, PluginFactory> registeredPlugins = new LinkedHashMap<String, PluginFactory>();
	private ImmutableMap<String, PluginFactory> immutableRegisteredPlugins = null;
	private final Map<String, InstanceBasedPluginFactory> registeredInstanceBasedPlugins = new LinkedHashMap<String, InstanceBasedPluginFactory>();
	private ImmutableMap<String, InstanceBasedPluginFactory> immutableRegisteredInstanceBasedPlugins = null;
	
	private final Set<String> customFeatures = new LinkedHashSet<String>();
	private boolean sealed = false;
	private ImmutableList<String> sortedCustomFeatures;
}
