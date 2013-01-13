package ac.biu.nlp.nlp.engineml.operations.finders;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import ac.biu.nlp.nlp.engineml.datastructures.FlippedBidirectionalMap;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseException;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleWithConfidenceAndDescription;
import ac.biu.nlp.nlp.engineml.operations.rules.distsimnew.DirtDBRuleBase;
import ac.biu.nlp.nlp.engineml.operations.specifications.RuleSpecification;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedMatchCriteria;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.rteflow.systems.Constants;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;
import ac.biu.nlp.nlp.instruments.parse.tree.match.MatcherException;
import ac.biu.nlp.nlp.instruments.parse.tree.match.pathmatcher.PathAllEmbeddedMatcher;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;

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
