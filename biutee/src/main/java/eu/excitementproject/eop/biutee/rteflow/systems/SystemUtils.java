package eu.excitementproject.eop.biutee.rteflow.systems;

import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.TRANSFORMATIONS_MODULE_NAME;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.LEXICAL_RESOURCES_RETRIEVE_MULTIWORDS_PARAMETER_NAME;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.RTE_ENGINE_UNIGRAM_LIDSTON_SER_FILE;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.rteflow.macro.Feature;
import eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.core.component.syntacticknowledge.utilities.PARSER;
import eu.excitementproject.eop.transformations.builtin_knowledge.KnowledgeResource;
import eu.excitementproject.eop.transformations.utilities.MLELidstonSmoothedUnigramProbabilityEstimation;
import eu.excitementproject.eop.transformations.utilities.ParserSpecificConfigurations;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.UnigramProbabilityEstimation;

/**
 * 
 * @author Asher Stern
 * @since Jul 22, 2013
 *
 */
public class SystemUtils
{
	public static PARSER setParserMode(ConfigurationParams params) throws ConfigurationException, TeEngineMlException
	{
		if (params.containsKey(ConfigurationParametersNames.RTE_ENGINE_PARSER_PARAMETER_NAME))
		{
			PARSER parser = params.getEnum(PARSER.class, ConfigurationParametersNames.RTE_ENGINE_PARSER_PARAMETER_NAME);
			logger.info("Setting parser to "+parser.name());
			ParserSpecificConfigurations.changeParser(parser);
			return parser;
		}
		else
		{
			throw new TeEngineMlException("Parser mode parameters missing.");
			//logger.warn("Parser mode not set. Using default: "+ParserSpecificConfigurations.getParserMode().name());
		}
	}
	
	
	public static UnigramProbabilityEstimation getUnigramProbabilityEstimation(ConfigurationParams enginemlParams) throws TeEngineMlException
	{
		try
		{
			File unigramModelSerFile = enginemlParams.getFile(RTE_ENGINE_UNIGRAM_LIDSTON_SER_FILE);
			logger.info("Loading unigram model from file: "+unigramModelSerFile.getPath());
			return MLELidstonSmoothedUnigramProbabilityEstimation.fromSerializedFile(unigramModelSerFile);
		}
		catch (IOException | ClassNotFoundException | ConfigurationException e)
		{
			throw new TeEngineMlException("Could not load UnigramProbabilityEstimation",e);
		}
	}
	
	public static Set<String> getLexicalRuleBasesForMultiWords(ConfigurationFile configurationFile) throws ConfigurationException
	{
		Set<String> ret = new LinkedHashSet<String>();
		ConfigurationParams knowledgeResourcesParams = configurationFile.getModuleConfiguration(TRANSFORMATIONS_MODULE_NAME);
		String valueAsString = knowledgeResourcesParams.get(LEXICAL_RESOURCES_RETRIEVE_MULTIWORDS_PARAMETER_NAME);
		if (valueAsString.trim().length()>0)
		{
			Set<KnowledgeResource> resources = knowledgeResourcesParams.getEnumSet(KnowledgeResource.class, LEXICAL_RESOURCES_RETRIEVE_MULTIWORDS_PARAMETER_NAME);
			for (KnowledgeResource resource : resources)
			{
				ret.add(resource.getDisplayName());
			}
		}
		return ret;
	}

	public static Set<Integer> getGlobalFeatureIndexes()
	{
		Set<Feature> globalFeatures = Feature.getGlobalFeatures();
		Set<Integer> globalFeaturesIndexes = new LinkedHashSet<Integer>();
		for (Feature globalFeature : globalFeatures)
		{
			globalFeaturesIndexes.add(globalFeature.getFeatureIndex());
		}
		return globalFeaturesIndexes;
	}



	private static final Logger logger = Logger.getLogger(SystemUtils.class);
}
