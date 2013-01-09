package ac.biu.nlp.nlp.engineml.script;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;

import ac.biu.nlp.nlp.engineml.plugin.PluginRegistry;
import ac.biu.nlp.nlp.engineml.rteflow.macro.DefaultOperationScript;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;

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
