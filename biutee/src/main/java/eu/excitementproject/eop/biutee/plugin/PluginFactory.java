package eu.excitementproject.eop.biutee.plugin;
import eu.excitementproject.eop.common.codeannotations.ThreadSafe;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;

/**
 * A base class for factory-class that creates an instance of a particular plug-in.
 * <P>
 * <B>Thread safety:</B> {@link PluginFactory} is shared among threads (i.e.,
 * two threads will have the same instance of {@link PluginFactory}, and might
 * call its methods simultaneously).
 * 
 * @author Asher Stern
 * @since Jan 27, 2012
 *
 */
@ThreadSafe
public abstract class PluginFactory extends AbstractPluginFactory<Plugin>
{
	protected PluginFactory(String pluginId, ImmutableSet<String> customFeatures)
			throws PluginException
	{
		super(pluginId, customFeatures);
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.plugin.AbstractPluginFactory#createPluginImplementation()
	 */
	@Override
	public abstract Plugin createPluginImplementation() throws PluginException;
}
