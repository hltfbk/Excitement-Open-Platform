package eu.excitementproject.eop.biutee.rteflow.micro.perform;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.plugin.Plugin;
import eu.excitementproject.eop.biutee.plugin.PluginException;
import eu.excitementproject.eop.biutee.rteflow.macro.multiword_namedentity_utils.MultiWordNamedEntityUtils;
import eu.excitementproject.eop.biutee.rteflow.micro.OperationsEnvironment;
import eu.excitementproject.eop.biutee.rteflow.micro.perform.dummy.DummyPerformFactory;
import eu.excitementproject.eop.biutee.script.RuleBasesAndPluginsContainer;
import eu.excitementproject.eop.biutee.script.SingleOperationItem;
import eu.excitementproject.eop.common.codeannotations.NotThreadSafe;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.transformations.datastructures.SingleItemList;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.specifications.Specification;
import eu.excitementproject.eop.transformations.utilities.Constants;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * @author Asher Stern
 * @since Jan 23, 2012
 *
 */
@NotThreadSafe
public class PerformFactoryFactory
{
	public PerformFactoryFactory(OperationsEnvironment operationsEnvironment,
			RuleBasesAndPluginsContainer<Info, BasicNode> ruleBasesContainer) throws TeEngineMlException
	{
		if (null==operationsEnvironment) throw new TeEngineMlException("Mull operationsEnvironment");
		this.operationsEnvironment = operationsEnvironment;
		if (null==ruleBasesContainer) throw new TeEngineMlException("Null ruleBasesContainer");
		this.ruleBasesContainer = ruleBasesContainer;
	}
	
	/**
	 * Returns <code>null</code> if there is no {@link PerformFactory} for the
	 * given {@linkplain SingleOperationItem}, or the appropriate factory, if such exists.
	 * <P>
	 * Note again: <B> null return value is legal here </B> 
	 * 
	 * @param item
	 * @return 
	 * @throws TeEngineMlException
	 * @throws OperationException 
	 * @throws PluginException 
	 */
	public List<PerformFactory<? extends Specification>> getFactory(SingleOperationItem item) throws TeEngineMlException, OperationException, PluginException
	{
		switch(item.getType())
		{
		case UNJUSTIFIED_INSERTION:
		{
			return new SingleItemList<PerformFactory<? extends Specification>>(new InsertPerformFactory(operationsEnvironment.getAlignmentCriteria()));
		}
		case UNJUSTIFIED_MOVE:
		{
			return new SingleItemList<PerformFactory<? extends Specification>>(new MovePerformFactory(operationsEnvironment.getAlignmentCriteria()));
		}
		case MULTIWORD_SUBSTITUTION:
		{
			if (Constants.HANDLE_MULTI_WORD_NAMED_ENTITIES)
			{
				ArrayList<PerformFactory<? extends Specification>> ret = new ArrayList<PerformFactory<? extends Specification>>();
				ret.add(new SubstituteMultiWordPerformFactory(operationsEnvironment.getSubstitutionMultiWordFinder()));
				ret.add(new SubstituteMultiWordMultiNodePerformFactory(operationsEnvironment.getMultiWordNamedEntityRuleBase(), MultiWordNamedEntityUtils.RULE_BASE_NAME));
				return ret;
			}
			else
			{
				return new SingleItemList<PerformFactory<? extends Specification>>(new SubstituteMultiWordPerformFactory(operationsEnvironment.getSubstitutionMultiWordFinder()));
			}
		}
		case FLIP_POS_SUBSTITUTION:
		{
			return new SingleItemList<PerformFactory<? extends Specification>>(new SubstitutionFlipPosPerformFactory(operationsEnvironment.getLemmatizer()));
		}
		case PARSER_ANTECEDENT_SUBSTITUTION:
		{
			return new SingleItemList<PerformFactory<? extends Specification>>(new SubstitutionParserAntecedentPerformFactory(operationsEnvironment.getParser()));
		}
		case COREFERENCE_SUBSTITUTION:
		{
			return new SingleItemList<PerformFactory<? extends Specification>>(new SubstitutionCoreferencePerformFactory(operationsEnvironment.getCoreferenceInformation()));
		}
		case IS_A_COREFERENCE_CONSTRUCTION:
		{
			return new SingleItemList<PerformFactory<? extends Specification>>(new IsA_ByCoreferencePerformFactory(operationsEnvironment.getCoreferenceInformation(),operationsEnvironment.getParser(),operationsEnvironment.isCollapseMode()));
		}
		case LEXICAL_RULE_BY_LEMMA_AND_POS_APPLICATION:
		{
			boolean withMultiWord = false;
			if (
					(operationsEnvironment.getMapLexicalMultiWord().containsKey(item.getRuleBaseName()))
					&&
					Constants.HANDLE_LEXICAL_MULTI_WORD
				)
			{
				withMultiWord = true;
			}
			
			if (withMultiWord)
			{
				List<PerformFactory<? extends Specification>> ret = new ArrayList<PerformFactory<? extends Specification>>(1+1);
				ret.add(new LexicalRuleByLemmaPosPerformFactory(operationsEnvironment.getStopWords()));
				ret.add(new LexicalMultiWordPerformFactory(operationsEnvironment.getMapLexicalMultiWord()));
				return ret;
			}
			else
			{
				return new SingleItemList<PerformFactory<? extends Specification>>(new LexicalRuleByLemmaPosPerformFactory(operationsEnvironment.getStopWords()));
			}
		}
		case LEXICAL_RULE_BY_LEMMA_AND_POS_APPLICATION_2D:
		{
			boolean withMultiWord = false;
			if (
					(operationsEnvironment.getMapLexicalMultiWord().containsKey(item.getRuleBaseName()))
					&&
					Constants.HANDLE_LEXICAL_MULTI_WORD
				)
			{
				withMultiWord = true;
			}
			
			if (withMultiWord)
			{
				List<PerformFactory<? extends Specification>> ret = new ArrayList<PerformFactory<? extends Specification>>(1+1);
				ret.add(new LexicalRuleByLemmaPos2DPerformFactory(operationsEnvironment.getHypothesisLemmasAndCanonicalPos(),operationsEnvironment.getHypothesisLemmasOnly(),operationsEnvironment.getStopWords()));
				ret.add(new LexicalMultiWordPerformFactory(operationsEnvironment.getMapLexicalMultiWord()));
				return ret;
			}
			else
			{
				return new SingleItemList<PerformFactory<? extends Specification>>(new LexicalRuleByLemmaPos2DPerformFactory(operationsEnvironment.getHypothesisLemmasAndCanonicalPos(),operationsEnvironment.getHypothesisLemmasOnly(),operationsEnvironment.getStopWords()));
			}
		}
		case RULE_APPLICATION:
		{
			return new SingleItemList<PerformFactory<? extends Specification>>(new RulePerformFactory(operationsEnvironment.getHypothesisTemplates(), operationsEnvironment.getHypothesisLemmasOnly(), operationsEnvironment.getHypothesisTreeAsBasicNode(), operationsEnvironment.isCollapseMode()));
		}
		case META_RULE_APPLICATION:
		{
			if (ruleBasesContainer.getMetaRuleBaseEnvelope(item.getRuleBaseName()).getChainOfLexicalRulesRuleBase()!=null)
			{
				boolean withMultiWord = false;
				if (Constants.HANDLE_LEXICAL_MULTI_WORD)
				{
					if (operationsEnvironment.getMapLexicalMultiWord().containsKey(item.getRuleBaseName()))
					{
						withMultiWord=true;
					}
				}
				if (withMultiWord)
				{
					List<PerformFactory<? extends Specification>> ret = new ArrayList<PerformFactory<? extends Specification>>(1+1);
					ret.add(new ChainOfLexicalRulesPerformFactory(operationsEnvironment.getHypothesisLemmasAndCanonicalPos(),operationsEnvironment.getHypothesisLemmasOnly(),operationsEnvironment.getStopWords()));
					ret.add(new MultiWordChainOfLexicalRulesPerformFactory(operationsEnvironment.getMapLexicalMultiWord()));
					return ret;
				}
				else
				{
					return new SingleItemList<PerformFactory<? extends Specification>>(new ChainOfLexicalRulesPerformFactory(operationsEnvironment.getHypothesisLemmasAndCanonicalPos(),operationsEnvironment.getHypothesisLemmasOnly(),operationsEnvironment.getStopWords()));
				}
			}
			else
			{
				throw new TeEngineMlException("Unsupported rule base envelope for meta-rule-base: "+item.getRuleBaseName());
			}
		}
		case CHANGE_PREDICATE_TRUTH:
		{
			if (Constants.REQUIRE_PREDICATE_TRUTH_EQUALITY)
			{
				return new SingleItemList<PerformFactory<? extends Specification>>(new ChangePredicateTruthPerformFactory());
				
			}
			else
			{
				return new SingleItemList<PerformFactory<? extends Specification>>(new DummyPerformFactory<Specification>());
			}
		}
		case PLUGIN_APPLICATION:
		{
			String pluginId = item.getPluginId();
			Plugin plugin = ruleBasesContainer.getPlugin(pluginId);
			try
			{
				return plugin.getPerformFactories();
			}
			catch(RuntimeException e)
			{
				throw new PluginException("Plugin has thrown a runtime-exception",e); 
			}
		}
		default:
		{
			logger.warn(this.getClass().getSimpleName()+": Returning null. An exception will be thrown by the caller.");
			return null;
		}
		}
	}
	
	private OperationsEnvironment operationsEnvironment;
	private RuleBasesAndPluginsContainer<Info, BasicNode> ruleBasesContainer;
	
	private static final Logger logger = Logger.getLogger(PerformFactoryFactory.class);
}
