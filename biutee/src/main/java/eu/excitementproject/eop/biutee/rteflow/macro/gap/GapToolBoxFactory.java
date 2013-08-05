package eu.excitementproject.eop.biutee.rteflow.macro.gap;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;

/**
 * 
 * @author Asher Stern
 * @since Aug 5, 2013
 *
 */
public class GapToolBoxFactory
{
	public GapToolBoxFactory(ConfigurationFile configurationFile,
			ConfigurationParams configurationParams)
	{
		super();
		this.configurationFile = configurationFile;
		this.configurationParams = configurationParams;
	}
	
	public GapToolBox<ExtendedInfo, ExtendedNode> createGapToolBox() throws GapException
	{
		logger.info("Create a dummy gap tool box.");
		return new GapToolBox<ExtendedInfo, ExtendedNode>()
		{
			@Override
			public boolean isHybridMode() throws GapException
			{
				return false;
			}
			
			@Override
			public GapToolsFactory<ExtendedInfo, ExtendedNode> getGapToolsFactory() throws GapException
			{
				return null;
			}
		};
	}
	
	@SuppressWarnings("unused")
	private final ConfigurationFile configurationFile;
	@SuppressWarnings("unused")
	private final ConfigurationParams configurationParams;
	
	
	private static final Logger logger = Logger.getLogger(GapToolBoxFactory.class);
}
