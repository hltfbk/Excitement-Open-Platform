package eu.excitementproject.eop.transformations.operations.finders;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.component.syntacticknowledge.RuleWithConfidenceAndDescription;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.FlippedBidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeIterator;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.match.AllEmbeddedMatcher;
import eu.excitementproject.eop.common.representation.parse.tree.match.MatcherException;
import eu.excitementproject.eop.common.utilities.Cache;
import eu.excitementproject.eop.common.utilities.CacheFactory;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.finders.auxiliary.AllowedRootsByAffectedNodesUtility;
import eu.excitementproject.eop.transformations.operations.finders.auxiliary.LemmaAndSimplerCanonicalPos;
import eu.excitementproject.eop.transformations.operations.finders.auxiliary.ParseTreeCharacteristics;
import eu.excitementproject.eop.transformations.operations.finders.auxiliary.ParseTreeCharacteristicsCollector;
import eu.excitementproject.eop.transformations.operations.finders.auxiliary.PosRelPos;
import eu.excitementproject.eop.transformations.operations.finders.auxiliary.SingleItemBidirectionalMap;
import eu.excitementproject.eop.transformations.operations.rules.BagOfRulesRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.operations.specifications.RuleSpecification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedMatchCriteria;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import static eu.excitementproject.eop.transformations.utilities.Constants.CACHE_SIZE_BAG_OF_RULES;

/**
 * This {@link Finder} returns a set of {@link RuleSpecification}s, based on the
 * given text tree and a rule base. The rule-base is given as a set of rules,
 * implemented as {@link BagOfRulesRuleBase}.
 * <P>
 * Note that {@link BagOfRulesRuleBase} and this finder are not very efficient,
 * since finding a matching rules is done by trying to find a match for each rule,
 * with no prior rule filtering. 
 * 
 * @author Asher Stern
 * @since Feb 24, 2011
 *
 */
public class RulesByBagOfRulesRuleBaseFinder implements Finder<RuleSpecification>
{
	public RulesByBagOfRulesRuleBaseFinder(TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree,
			BagOfRulesRuleBase<Info, BasicNode> ruleBase, String ruleBaseName)
	{
		super();
		this.textTree = textTree;
		this.ruleBase = ruleBase;
		this.ruleBaseName = ruleBaseName;
	}
	
	@Override
	public void optionallyOptimizeRuntimeByAffectedNodes(Set<ExtendedNode> affectedNodes) throws OperationException
	{
		this.affectedNodes = affectedNodes;
	}

	@Override
	public void find() throws OperationException
	{
		try
		{
			matchCriteria = new ExtendedMatchCriteria();
			extractGivenTreeCharacteristics();
			Set<ExtendedNode> allowedRoots = null;
			if (affectedNodes!=null) {allowedRoots = AllowedRootsByAffectedNodesUtility.findAllowedRootsByAffectedNodes(textTree, affectedNodes);}
			specs = new LinkedHashSet<RuleSpecification>();
			debug_numberOfFilteredRules=0;
			for (RuleWithConfidenceAndDescription<Info, BasicNode> rule : ruleBase.getRules())
			{
				if (mightMatch(rule))
				{
					if (!(rule.getRule().getLeftHandSide().hasChildren()))
					{
						findForSingleNodeRule(rule);
					}
					else
					{
						AllEmbeddedMatcher<ExtendedInfo, Info, ExtendedNode, BasicNode> matcher = 
								new AllEmbeddedMatcher<ExtendedInfo, Info, ExtendedNode, BasicNode>(matchCriteria);
						if (allowedRoots!=null)
						{
							matcher.setAllowedRoots(allowedRoots);
						}

						matcher.setTrees(this.textTree.getTree(), rule.getRule().getLeftHandSide());
						matcher.findMatches();
						Set<BidirectionalMap<ExtendedNode, BasicNode>> matches = matcher.getMatches();

						for (BidirectionalMap<ExtendedNode, BasicNode> singleLhsMatch : matches)
						{
							BidirectionalMap<BasicNode, ExtendedNode> mapLhsToTree = new FlippedBidirectionalMap<BasicNode, ExtendedNode>(singleLhsMatch);
							boolean introduction = false;
							if (rule.getRule().isExtraction()!=null)
							{
								introduction = rule.getRule().isExtraction().booleanValue();
							}

							specs.add(new RuleSpecification(this.ruleBaseName,rule,mapLhsToTree,introduction));
						}
					}
				}
			}
			if (logger.isDebugEnabled())
			{
				
				logger.debug("Number of filtered rules: "+debug_numberOfFilteredRules+" out of "+ruleBase.getRules().size()+" total rules.");
			}
		}
		catch(MatcherException e)
		{
			throw new OperationException("Matcher failed. See nested exception.",e);
		}
		catch(RuleBaseException e)
		{
			throw new OperationException("RuleBase failure. See nested exception.",e);
			
		}
	}
	
	@Override
	public Set<RuleSpecification> getSpecs() throws OperationException
	{
		if (specs==null)
			throw new OperationException("You did not call find()");
		
		return this.specs;
	}
	
	
	
	
	private void findForSingleNodeRule(RuleWithConfidenceAndDescription<Info, BasicNode> rule)
	{
		BasicNode lhs = rule.getRule().getLeftHandSide();
		for (ExtendedNode node : TreeIterator.iterableTree(textTree.getTree()))
		{
			if (matchCriteria.nodesMatch(node, lhs))
			{
				specs.add(new RuleSpecification(this.ruleBaseName,rule,
						new SingleItemBidirectionalMap<BasicNode, ExtendedNode>(lhs, node)
						,false));
			}
		}
	}
	
	private ParseTreeCharacteristics<Info, BasicNode> getCharacteristicsOfRule(RuleWithConfidenceAndDescription<Info, BasicNode> rule)
	{
		ParseTreeCharacteristics<Info, BasicNode> ret = null;
		BasicNode lhs = rule.getRule().getLeftHandSide();
		if (ruleBaseCharacteristicsCache.containsKey(lhs))
		{
			ret = ruleBaseCharacteristicsCache.get(lhs);
		}
		else
		{
			ParseTreeCharacteristicsCollector<Info, BasicNode> collector = new ParseTreeCharacteristicsCollector<Info, BasicNode>(lhs);
			collector.extract();
			ret = new ParseTreeCharacteristics<Info, BasicNode>(collector.getPosRelPosSet(),collector.getLemmaAndPosSet());
			ruleBaseCharacteristicsCache.put(lhs, ret);
		}
		return ret;
	}
	
	private void extractGivenTreeCharacteristics()
	{
		ParseTreeCharacteristicsCollector<ExtendedInfo,ExtendedNode> collector = new ParseTreeCharacteristicsCollector<ExtendedInfo,ExtendedNode>(textTree.getTree());
		collector.extract();
		posRelPosTree = collector.getPosRelPosSet();
		lemmaAndPosTree = collector.getLemmaAndPosSet();
		
		if (logger.isDebugEnabled())
		{
			logger.debug("Given tree characteristics:");
			logger.debug("posRelPosTree = "+printSet(posRelPosTree));
			logger.debug("lemmaAndPosTree = "+printSet(lemmaAndPosTree));
		}
	}
	
	
	private boolean mightMatch(RuleWithConfidenceAndDescription<Info, BasicNode> rule)
	{
		boolean ret = false;
		ParseTreeCharacteristics<Info, BasicNode> ruleCharacteristics = getCharacteristicsOfRule(rule);
		if (posRelPosTree.containsAll(ruleCharacteristics.getPosRelPosSet()))
		{
			if (lemmaAndPosTree.containsAll(ruleCharacteristics.getLemmaAndPosSet()))
			{
				ret = true;
			}
		}
		if (!ret) {++debug_numberOfFilteredRules;}
		return ret;
	}
	
	private static <T> String printSet(Set<T> set)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{ ");
		boolean firstIteration = true;
		for (T t : set)
		{
			if (firstIteration) {firstIteration=false;}
			else {sb.append(",");}
			sb.append(t.toString());
		}
		sb.append(" }");
		return sb.toString();
	}
	
	
	
	
	
	
	
	// input
	private final TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree;
	private final BagOfRulesRuleBase<Info, BasicNode> ruleBase;
	private final String ruleBaseName;
	
	private Set<ExtendedNode> affectedNodes = null;
	
	
	// internals
	private ExtendedMatchCriteria matchCriteria = null;
	
	private Set<PosRelPos> posRelPosTree;
	private Set<LemmaAndSimplerCanonicalPos> lemmaAndPosTree;
	
	private Cache<BasicNode, ParseTreeCharacteristics<Info, BasicNode>> ruleBaseCharacteristicsCache = new CacheFactory<BasicNode, ParseTreeCharacteristics<Info, BasicNode>>().getCache(CACHE_SIZE_BAG_OF_RULES);
	
	private int debug_numberOfFilteredRules = 0;
	
	// output
	private Set<RuleSpecification> specs = null;
	
	private static final Logger logger = Logger.getLogger(RulesByBagOfRulesRuleBaseFinder.class);
}
