package eu.excitementproject.eop.transformations.builtin_knowledge;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.core.component.lexicalknowledge.catvar.CatvarLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.similarity.Direct200LexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.similarity.LinDependencyOriginalLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.similarity.LinDistsimLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.similarity.LinProximityOriginalLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.verb_ocean.VerbOceanLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.wikipedia.WikiLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetLexicalResource;
import eu.excitementproject.eop.transformations.operations.rules.ByLemmaPosLexicalRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.ByLemmaPosLexicalRuleBaseWrapper;
import eu.excitementproject.eop.transformations.operations.rules.LexicalRule;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.operations.rules.lexical.GeoFromDBLexicalRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.lexical.LexicalResourceWrapper;
import eu.excitementproject.eop.transformations.operations.rules.lexical.LinReutersFromDBLexicalResource;
import eu.excitementproject.eop.transformations.operations.rules.lexicalchain.ChainOfLexicalRules;
import eu.excitementproject.eop.transformations.operations.rules.lexicalchain.graphbased.PlisRuleBase;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.Constants.Workarounds;

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
				logger.warn("Using workaround-wrapper for Lin-Reuters. Note that scores are ignored in this wrapper (all rules have the same constant score).");
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
