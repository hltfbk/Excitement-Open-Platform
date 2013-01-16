package eu.excitementproject.eop.biutee.plugin;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;

/**
 * A base class for subclasses by which plug-ins are registered.
 * A user has to implement a sub-class of {@link PluginRegisterer}, but the user
 * <B>does not</B> instantiates the class (s)he created!
 * The sub-class full-name should be given in the configuration file, and an
 * instance will be created using reflection (this is the one and only one
 * place in the whole project in which reflection is used).
 * <P>
 * The sub-class should implement the method {@link #register()}, as a method that calls
 * the methods of the member variable <code>pluginRegistry</code>: the
 * methods {@link PluginRegistry#registerPlugin(PluginFactory)} and
 * {@link PluginRegistry#registerInstanceBasedPlugin(InstanceBasedPluginFactory)}.
 * 
 * @author Asher Stern
 * @since Jan 27, 2012
 *
 */
public abstract class PluginRegisterer
{
	public PluginRegisterer(ConfigurationFile configurationFile, PluginRegistry pluginRegistry) throws PluginException
	{
		super();
		this.configurationFile = configurationFile;
		this.pluginRegistry = pluginRegistry;
	}

	public abstract void register() throws PluginException;



	protected final PluginRegistry pluginRegistry;
	protected final ConfigurationFile configurationFile;
}
