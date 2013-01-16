package eu.excitementproject.eop.biutee.plugin;
import eu.excitementproject.eop.common.codeannotations.ThreadSafe;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;

/**
 * 
 * @author Asher Stern
 * @since Jan 29, 2012
 *
 * @param <T>
 */
@ThreadSafe
public abstract class AbstractPluginFactory<T extends Plugin>
{
	protected  AbstractPluginFactory(String pluginId, ImmutableSet<String> customFeatures) throws PluginException
	{
		super();
		this.pluginId = pluginId;
		this.customFeatures = customFeatures;
	}
	
	public final T createPlugin() throws PluginException
	{
		try
		{
			return createPluginImplementation();
		}
		catch(RuntimeException e)
		{
			throw new PluginException("A runtime exception was thrown by plugin-factory.",e);
		}
	}
	
	/**
	 * This method <B>creates a new instance</B> of the plug-in.
	 * 
	 * @return
	 * @throws PluginException
	 */
	public abstract T createPluginImplementation() throws PluginException;

	
	
	public String getPluginId()
	{
		return pluginId;
	}

	public ImmutableSet<String> getCustomFeatures()
	{
		return customFeatures;
	}



	private final String pluginId;
	private final ImmutableSet<String> customFeatures;
}
