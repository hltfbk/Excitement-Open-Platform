package eu.excitementproject.eop.biutee.plugin;
import eu.excitementproject.eop.common.codeannotations.ThreadSafe;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;

/**
 * A base class for factory-class that creates an instance of a particular plug-in.
 * <P>
 * <B>Thread safety:</B> {@link InstanceBasedPluginFactory} is shared among threads
 * (i.e., two threads will have the same instance of {@link InstanceBasedPluginFactory},
 * and might call its methods simultaneously).
 * 
 * @author Asher Stern
 * @since Jan 29, 2012
 *
 */
@ThreadSafe
public abstract class InstanceBasedPluginFactory extends AbstractPluginFactory<InstanceBasedPlugin>
{
	protected InstanceBasedPluginFactory(String pluginId,
			ImmutableSet<String> customFeatures) throws PluginException
	{
		super(pluginId, customFeatures);
	}

	@Override
	public abstract InstanceBasedPlugin createPluginImplementation() throws PluginException;

}
