package eu.excitementproject.eop.transformations.operations.finders;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.component.syntacticknowledge.RuleWithConfidenceAndDescription;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.FlippedBidirectionalMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.match.MatcherException;
import eu.excitementproject.eop.common.representation.parse.tree.match.pathmatcher.PathAllEmbeddedMatcher;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.operations.rules.distsimnew.DirtDBRuleBase;
import eu.excitementproject.eop.transformations.operations.specifications.RuleSpecification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedMatchCriteria;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.Constants;

/**
 * Finds rules that match the text, from the given {@link DirtDBRuleBase}.
 * <P>
 * The rules might be restricted to only rules for which the right-hand-side
 * matches the hypothesis, or the right-hand-side-head matches the hypothesis, or
 * not restricted at all.<BR>
 * These restrictions are controlled by {@link Constants#DIRT_LIKE_FILTER_BY_HYPOTHESIS_TEMPLATES}
 * and {@link Constants#DIRT_LIKE_FILTER_BY_HYPOTHESIS_WORDS}.
 *  
 * @author Asher Stern
 * @since Aug 3, 2011
 *
 */
public class RulesFromDirtDBFinder implements Finder<RuleSpecification>
{
	public RulesFromDirtDBFinder(DirtDBRuleBase ruleBase, String ruleBaseName,
			ExtendedNode textTree, ImmutableSet<String> hypothesisTemplates, Iterable<String> hypothesisLemmas)
	{
		super();
		this.ruleBase = ruleBase;
		this.ruleBaseName = ruleBaseName;
		this.textTree = textTree;
		this.hypothesisTemplates = hypothesisTemplates;
		this.hypothesisLemmas = hypothesisLemmas;
	}

	@Override public void optionallyOptimizeRuntimeByAffectedNodes(Set<ExtendedNode> affectedNodes) throws OperationException
	{}

	@Override
	public void find() throws OperationException
	{
		try
		{
			ImmutableSet<RuleWithConfidenceAndDescription<Info,BasicNode>> rules =
					ruleBase.getRulesForLeftByTree(textTree,hypothesisTemplates,hypothesisLemmas);

			specs = new LinkedHashSet<RuleSpecification>();
			for (RuleWithConfidenceAndDescription<Info,BasicNode> rule : rules)
			{
				BasicNode lhs = rule.getRule().getLeftHandSide();
				Collection<? extends BidirectionalMap<ExtendedNode, BasicNode>> matches = getMatches(lhs);
				
//				AllEmbeddedMatcher<ExtendedInfo, Info, ExtendedNode, EnglishNode> matcher =
//						new AllEmbeddedMatcher<ExtendedInfo, Info, ExtendedNode, EnglishNode>(new ExtendedMatchCriteria());
//
//				matcher.setTrees(textTree, lhs);
//				matcher.findMatches();
//				Set<BidirectionalMap<ExtendedNode, EnglishNode>> matches = matcher.getMatches();

				for (BidirectionalMap<ExtendedNode, BasicNode> match : matches)
				{
					RuleSpecification spec = new RuleSpecification(ruleBaseName,rule,new FlippedBidirectionalMap<BasicNode, ExtendedNode>(match),false);
					specs.add(spec);
				}
			}
		}
		catch(RuleBaseException e)
		{
			throw new OperationException("Finder failed, see nested.",e);
		}
		catch(MatcherException e)
		{
			throw new OperationException("Finder failed, see nested.",e);
		}

	}
	
	@Override
	public Set<RuleSpecification> getSpecs()
	{
		return specs;
	}
	
	private Collection<? extends BidirectionalMap<ExtendedNode, BasicNode>> getMatches(BasicNode lhs) throws MatcherException
	{
		PathAllEmbeddedMatcher<ExtendedInfo, ExtendedNode,Info,BasicNode> matcher = 
				new PathAllEmbeddedMatcher<ExtendedInfo, ExtendedNode,Info,BasicNode>(extendedMatchCriteria);
		matcher.setTrees(textTree, lhs);
		matcher.findMatches();
		Collection<? extends BidirectionalMap<ExtendedNode, BasicNode>> ret = matcher.getMatches();
		
		return ret;
	}
	
	private ExtendedMatchCriteria extendedMatchCriteria = new ExtendedMatchCriteria();




	protected DirtDBRuleBase ruleBase;
	protected String ruleBaseName;
	protected ExtendedNode textTree;
	protected ImmutableSet<String> hypothesisTemplates;
	protected Iterable<String> hypothesisLemmas;
	
	protected Set<RuleSpecification> specs = null;
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(RulesFromDirtDBFinder.class);
}
