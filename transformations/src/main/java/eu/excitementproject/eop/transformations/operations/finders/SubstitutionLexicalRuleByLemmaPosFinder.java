package eu.excitementproject.eop.transformations.operations.finders;
import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultNodeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultSyntacticInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeIterator;
import eu.excitementproject.eop.transformations.datastructures.LemmaAndPos;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.ByLemmaPosLexicalRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.LexicalRule;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.operations.specifications.RuleSubstituteNodeSpecification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.Constants;
import eu.excitementproject.eop.transformations.utilities.InfoObservations;
import eu.excitementproject.eop.transformations.utilities.ParserSpecificConfigurations;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.AdvancedEqualities;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.Equalities;



/**
 * This {@linkplain Finder}, given a text tree and a rule base of the type
 * {@link ByLemmaPosLexicalRuleBase}, finds which rules can be applied on the text tree.
 * 
 * @author Asher Stern
 * @since Feb 9, 2011
 *
 */
public class SubstitutionLexicalRuleByLemmaPosFinder<T extends LexicalRule> implements Finder<RuleSubstituteNodeSpecification<T>>
{
	public SubstitutionLexicalRuleByLemmaPosFinder(TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap,ByLemmaPosLexicalRuleBase<T> ruleBase, String ruleBaseName,
			boolean filterLeftStopWords, boolean filterRightStopWords, ImmutableSet<String> stopWords
			) throws OperationException
	{
		this.treeAndParentMap = treeAndParentMap;
		this.ruleBase = ruleBase;
		this.ruleBaseName = ruleBaseName;
		this.filterLeftStopWords = filterLeftStopWords;
		this.filterRightStopWords = filterRightStopWords;
		this.stopWords = stopWords;
		if ( (filterLeftStopWords||filterRightStopWords) && (null==stopWords) )
			throw new OperationException("stop-words filter is active, but stop-words list is null.");

	}
	
//	public SubstitutionLexicalRuleByLemmaPosFinder(TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap,ByLemmaPosLexicalRuleBase<T> ruleBase, String ruleBaseName) throws OperationException
//	{
//		this(treeAndParentMap, ruleBase, ruleBaseName, false, false, null);
//	}
	
	public void setSearchingForMultiWords(boolean searchingForMultiWords)
	{
		this.searchingForMultiWords = searchingForMultiWords;
	}

	@Override public void optionallyOptimizeRuntimeByAffectedNodes(Set<ExtendedNode> affectedNodes) throws OperationException
	{}


	@Override
	public void find() throws OperationException
	{
		try
		{
			findSpecs();
		}
		catch(RuleBaseException e)
		{
			throw new OperationException("Problem in rule base \"" +this.ruleBaseName+"\". See nested Exception.",e);
		}
		catch (TeEngineMlException e)
		{
			throw new OperationException("Problem in finding rules for rule base \"" +this.ruleBaseName+"\". See nested Exception.",e);
		}
	}
	
	@Override
	public Set<RuleSubstituteNodeSpecification<T>> getSpecs() throws OperationException
	{
		if (null==specs)
			throw new OperationException("You did not call find().");
		return specs;
	}
	
	protected boolean isRelevantRule(T rule) throws RuleBaseException
	{
		boolean ret = true;

		if (ruleLhsEqualsRhs(rule))
		{
			ret = false;
		}
		else
		{
			ret = relevantAccordingToMultiOrSingleWord(rule);
		}
		if (true==ret)
		{
			ret = relevantAccordingToFilterStopWords(rule);
		}

		return ret;
	}
	
	private boolean relevantAccordingToFilterStopWords(T rule)
	{
		boolean ret = true;
		if (filterLeftStopWords)
		{
			if (stopWords.contains(rule.getLhsLemma().toLowerCase()))
			{
				ret = false;
			}
		}
		if (true==ret)
		{
			if (filterRightStopWords)
			{
				if (stopWords.contains(rule.getRhsLemma().toLowerCase()))
				{
					ret = false;
				}
			}
		}
		return ret;
	}
	
	private boolean relevantAccordingToMultiOrSingleWord(T rule)
	{
		boolean ret = true;
		if (searchingForMultiWords)
		{
			if (rule.getRhsLemma().split("\\s+").length>1)
				ret=true;
			else
				ret=false;
		}
		else
		{
			// if (ParserSpecificConfigurations.doNotApplyLexicallyLexicalMultiWordRules())
			if(multiWordRulesAreIrrelevant)
			{
				if (rule.getRhsLemma().split("\\s+").length>1)
				{
					ret=false;
				}
			}
		}
		return ret;
	}
	
	protected boolean isRelevantNode(ExtendedNode node)
	{
		return true;
	}
	
	protected boolean ruleLhsEqualsRhs(T rule)
	{
		boolean ret = false;
		boolean lemmasAreEqual = false;
		if (Constants.USE_ADVANCED_EQUALITIES)
		{
			lemmasAreEqual = AdvancedEqualities.lemmasEqual(rule.getLhsLemma(), rule.getRhsLemma());
		}
		else
		{
			lemmasAreEqual= Equalities.lemmasEqual(rule.getLhsLemma(), rule.getRhsLemma());
		}
		boolean posesAreEqual = false;
		if (Constants.USE_ADVANCED_EQUALITIES)
		{
			posesAreEqual = AdvancedEqualities.posEqual(rule.getLhsPos(), rule.getRhsPos());
		}
		else
		{
			posesAreEqual= Equalities.posEqual(rule.getLhsPos(), rule.getRhsPos());
		}

		if (lemmasAreEqual&&posesAreEqual)
		{
			ret = true;
		}
		
		return ret;
	}
	
	protected void addAdditionalDescription(RuleSubstituteNodeSpecification<T> spec,T rule)
	{
	}
	
	protected RuleSubstituteNodeSpecification<T> createSpec(ExtendedNode node, NodeInfo newNodeInfo, String ruleBaseName, T rule)
	{
		return createSpec(node,newNodeInfo,ruleBaseName,rule,true);
	}
	
	protected RuleSubstituteNodeSpecification<T> createSpec(ExtendedNode node, NodeInfo newNodeInfo, String ruleBaseName, T rule, boolean writeConfidenceInDescription)
	{
		return new RuleSubstituteNodeSpecification<T>(node, newNodeInfo, node.getInfo().getAdditionalNodeInformation(), rule.getConfidence(), ruleBaseName, rule, writeConfidenceInDescription);
	}


	private void findSpecs() throws TeEngineMlException, RuleBaseException
	{
		this.specs = new LinkedHashSet<RuleSubstituteNodeSpecification<T>>();
		for (ExtendedNode node : TreeIterator.iterableTree(treeAndParentMap.getTree()))
		{
			if (isRelevantNode(node))
			{
				if (InfoObservations.infoHasLemma(node.getInfo()))
				{
					LemmaAndPos lemmaAndPos = new LemmaAndPos(InfoGetFields.getLemma(node.getInfo()), InfoGetFields.getPartOfSpeechObject(node.getInfo()));
					ImmutableSet<T> rules = getRulesForLemmaAndPos(lemmaAndPos);
					for (T rule : rules)
					{
						if (isRelevantRule(rule))
						{
							NodeInfo newNodeInfo = new DefaultNodeInfo(rule.getRhsLemma(), rule.getRhsLemma(), node.getInfo().getNodeInfo().getSerial(), node.getInfo().getNodeInfo().getNamedEntityAnnotation(), new DefaultSyntacticInfo(rule.getRhsPos()));
							RuleSubstituteNodeSpecification<T> spec = createSpec(node, newNodeInfo,ruleBaseName, rule); 
							// new RuleSubstituteNodeSpecification<T>(node, newNodeInfo, node.getInfo().getAdditionalNodeInformation(), rule.getConfidence(), ruleBaseName, rule);
							addAdditionalDescription(spec,rule);
							specs.add(spec);
						}
					}
				}
			}
		}

	}
	
	private ImmutableSet<T> getRulesForLemmaAndPos(LemmaAndPos lemmaAndPos) throws RuleBaseException
	{
		return ruleBase.getRules(lemmaAndPos.getLemma(), lemmaAndPos.getPartOfSpeech());
	}
	
	private TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap;
	private ByLemmaPosLexicalRuleBase<T> ruleBase;
	private String ruleBaseName;
	private boolean searchingForMultiWords=false;
	
	/**
	 * If it is not in mode of {@link #searchingForMultiWords}, then this flags
	 * indicates whether to look for rules which have right-hand-side of multi-word.
	 */
	private boolean multiWordRulesAreIrrelevant = ParserSpecificConfigurations.doNotApplyLexicallyLexicalMultiWordRules();
	
	private boolean filterLeftStopWords = false;
	private boolean filterRightStopWords = false;
	private ImmutableSet<String> stopWords = null;
	
	private Set<RuleSubstituteNodeSpecification<T>> specs = null;
}
