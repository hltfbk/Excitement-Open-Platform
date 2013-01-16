package eu.excitementproject.eop.biutee.script;
import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.plugin.PluginRegistry;
import eu.excitementproject.eop.biutee.rteflow.macro.DefaultOperationScript;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;

/**
 * A factory which returns an {@link OperationsScript}.
 * 
 * @author Asher Stern
 * @since 2011
 *
 */
public class ScriptFactory
{
	public ScriptFactory(ConfigurationFile configurationFile, PluginRegistry pluginRegistry)
	{
		this.configurationFile = configurationFile;
		this.pluginRegistry = pluginRegistry;
	}

	public OperationsScript<Info, BasicNode> getDefaultScript()
	{
		return new DefaultOperationScript(configurationFile,pluginRegistry);
	}
	
	
	private ConfigurationFile configurationFile;
	private PluginRegistry pluginRegistry;
	
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(ScriptFactory.class);
}
