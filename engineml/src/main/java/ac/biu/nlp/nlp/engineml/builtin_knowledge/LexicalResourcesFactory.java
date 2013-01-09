package ac.biu.nlp.nlp.engineml.builtin_knowledge;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;

import ac.biu.nlp.nlp.engineml.operations.rules.ByLemmaPosLexicalRuleBase;
import ac.biu.nlp.nlp.engineml.operations.rules.ByLemmaPosLexicalRuleBaseWrapper;
import ac.biu.nlp.nlp.engineml.operations.rules.LexicalRule;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseException;
import ac.biu.nlp.nlp.engineml.operations.rules.lexical.GeoFromDBLexicalRuleBase;
import ac.biu.nlp.nlp.engineml.operations.rules.lexical.LexicalResourceWrapper;
import ac.biu.nlp.nlp.engineml.operations.rules.lexical.LinReutersFromDBLexicalResource;
import ac.biu.nlp.nlp.engineml.operations.rules.lexicalchain.ChainOfLexicalRules;
import ac.biu.nlp.nlp.engineml.operations.rules.lexicalchain.graphbased.PlisRuleBase;
import ac.biu.nlp.nlp.engineml.rteflow.systems.Constants.Workarounds;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.lexical_resource.LexicalResource;
import ac.biu.nlp.nlp.lexical_resource.LexicalResourceException;
import ac.biu.nlp.nlp.lexical_resource.RuleInfo;
import ac.biu.nlp.nlp.lexical_resource.impl.catvar.CatvarLexicalResource;
import ac.biu.nlp.nlp.lexical_resource.impl.similarity.Direct200LexicalResource;
import ac.biu.nlp.nlp.lexical_resource.impl.similarity.LinDependencyOriginalLexicalResource;
import ac.biu.nlp.nlp.lexical_resource.impl.similarity.LinDistsimLexicalResource;
import ac.biu.nlp.nlp.lexical_resource.impl.similarity.LinProximityOriginalLexicalResource;
import ac.biu.nlp.nlp.lexical_resource.impl.verb_ocean.VerbOceanLexicalResource;
import ac.biu.nlp.nlp.lexical_resource.impl.wikipedia.WikiLexicalResource;
import ac.biu.nlp.nlp.lexical_resource.impl.wordnet.WordnetLexicalResource;

/**
 * 
 * Constructs a {@link LexicalResource} that corresponds to an
 * enum-constant of {@link KnowledgeResource} enum.
 * 
 * @author Asher Stern
 * @since Mar 4, 2012
 *
 */
public class LexicalResourcesFactory
{
	public LexicalResourcesFactory(ConfigurationFile configurationFile)
	{
		super();
		this.configurationFile = configurationFile;
	}

	/**
	 * Constructs a {@link LexicalResourceException} for the given
	 * {@link KnowledgeResource} enum-constant.
	 * 
	 * This function is allowed to return null.<BR>
	 * The caller must check if the return value is null!
	 * 
	 * @param knowledgeResource
	 * @return
	 * @throws ConfigurationException
	 * @throws LexicalResourceException
	 */
	public LexicalResource<? extends RuleInfo> createLexicalResource(KnowledgeResource knowledgeResource) throws ConfigurationException, LexicalResourceException
	{
		LexicalResource<? extends RuleInfo> ret = null;
		String moduleName = knowledgeResource.getInfrastructureModuleName();
		if (moduleName!=null)
		{
			ConfigurationParams params = configurationFile.getModuleConfiguration(moduleName);

			switch(knowledgeResource)
			{
			case BAP:
				ret = new Direct200LexicalResource(params);
				break;
			case CATVAR:
				ret = new CatvarLexicalResource(params);
				break;
			case VERB_OCEAN:
				ret = new VerbOceanLexicalResource(params);
				break;
			case WIKIPEDIA:
				ret = new WikiLexicalResource(params);
				break;
			case WORDNET:
				ret = new WordnetLexicalResource(params);
				break;
			case LIN_DEPENDENCY_ORIGINAL:
				ret = new LinDependencyOriginalLexicalResource(params);
				break;
			case LIN_PROXIMITY_ORIGINAL:
				ret = new LinProximityOriginalLexicalResource(params);
				break;
			case LIN_DEPENDENCY_REUTERS:
				ret = new LinDistsimLexicalResource(params);
				break;
			default:
				ret = null; // leave ret as null
				break;
			}

		}
		return ret;
	}

	
	public ByLemmaPosLexicalRuleBase<LexicalRule> createByLemmaPosLexicalRuleBase(KnowledgeResource knowledgeResource) throws ConfigurationException, LexicalResourceException, RuleBaseException, TeEngineMlException, SQLException
	{
		ByLemmaPosLexicalRuleBase<LexicalRule> ret = null;
		
		// Some explanation about what you see here:
		// The GEO lexical rule base should be applied only on nodes that are
		// named-entities. Thus, it should be created separately, such that the
		// ByLemmaPosLexicalRuleBase will also implement "RuleBaseWithNamedEntities".
		// Next, in LexicalRuleByLemmaPos2DPerformFactory - it is detected by
		// an RTTI that this rule base implements "RuleBaseWithNamedEntities",
		// and the appropriate operation, which applies rules only on named-entity nodes
		// is used. (I know, RTTI is bad, in the future it should be changed).
		//
		// Note that for multi-word there is no "RuleBaseWithNamedEntities", thus
		// in the configuration file, the GEO lexical resource should NOT
		// be in the list of multi-word lexical resources!
		if (knowledgeResource.equals(KnowledgeResource.GEO))
		{
			ConfigurationParams resourceParams = getParamsOfKnowledgeResource(knowledgeResource);
			GeoFromDBLexicalRuleBase geoRuleBase = GeoFromDBLexicalRuleBase.fromConfigurationParams(resourceParams);
			ret = geoRuleBase;
		}
		else if (knowledgeResource.equals(KnowledgeResource.LIN_DEPENDENCY_REUTERS))
		{
			if (Workarounds.LIN_REUTERS_USE_CONSTANT_SCORE)
			{
				logger.warn("Using Lin-Reuters with constant score.");
				ConfigurationParams resourceParams = getParamsOfKnowledgeResource(knowledgeResource);
				LinReutersFromDBLexicalResource linReutersFromDB =
						LinReutersFromDBLexicalResource.fromParams(resourceParams);
				ret = linReutersFromDB;
			}
		}
		else if (knowledgeResource.equals(KnowledgeResource.PLIS_GRAPH))
		{
			ByLemmaPosLexicalRuleBase<ChainOfLexicalRules> plisGraphRuleBase = null;
			ConfigurationParams paramsForPlis = getParamsOfKnowledgeResource(knowledgeResource);
			plisGraphRuleBase = new PlisRuleBase(paramsForPlis);
			ret = new ByLemmaPosLexicalRuleBaseWrapper<LexicalRule,ChainOfLexicalRules>(plisGraphRuleBase);
		}
		
		if (null==ret)
		{
			LexicalResource<? extends RuleInfo> lexicalResource = createLexicalResource(knowledgeResource);
			if (lexicalResource!=null)
			{
				ret = new LexicalResourceWrapper(lexicalResource);
			}
		}
		return ret;
	}
	
	protected ConfigurationParams getParamsOfKnowledgeResource(KnowledgeResource knowledgeResource) throws TeEngineMlException, ConfigurationException
	{
		String name = knowledgeResource.getInfrastructureModuleName();
		if (null==name)
		{
			name = knowledgeResource.getModuleName();
		}
		if (null==name)
			throw new TeEngineMlException("null name");
			
		return configurationFile.getModuleConfiguration(name);
	}

	protected ConfigurationFile configurationFile;
	
	private static final Logger logger = Logger.getLogger(LexicalResourcesFactory.class);
}
