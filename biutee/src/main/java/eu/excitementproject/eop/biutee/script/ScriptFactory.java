package eu.excitementproject.eop.biutee.script;
import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.plugin.PluginRegistry;
import eu.excitementproject.eop.biutee.rteflow.macro.DefaultOperationScript;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapException;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.transformations.utilities.ParserSpecificConfigurations;

/**
 * A factory which returns an {@link OperationsScript}.
 * 
 * @author Asher Stern
 * @since 2011
 *
 */
public class ScriptFactory
{
	@Deprecated
	public ScriptFactory(ConfigurationFile configurationFile, PluginRegistry pluginRegistry)
	{
		this.configurationFile = configurationFile;
		this.pluginRegistry = pluginRegistry;
		this.parser = ParserSpecificConfigurations.getParserMode();
		this.hybridGapMode = false;
	}

	public ScriptFactory(ConfigurationFile configurationFile, PluginRegistry pluginRegistry, TESystemEnvironment teSystemEnvironment) throws GapException
	{
		this.configurationFile = configurationFile;
		this.pluginRegistry = pluginRegistry;
		this.parser = teSystemEnvironment.getParser();
		hybridGapMode = teSystemEnvironment.getGapToolBox().isHybridMode();
	}

	
	public OperationsScript<Info, BasicNode> getDefaultScript()
	{
		return new DefaultOperationScript(configurationFile,parser,pluginRegistry,hybridGapMode);
	}
	
	
	private ConfigurationFile configurationFile;
	private PluginRegistry pluginRegistry;
	private final ParserSpecificConfigurations.PARSER parser;
	private final boolean hybridGapMode;
	
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(ScriptFactory.class);
}
