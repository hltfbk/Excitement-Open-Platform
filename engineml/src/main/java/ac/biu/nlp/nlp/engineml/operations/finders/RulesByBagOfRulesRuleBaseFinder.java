package ac.biu.nlp.nlp.engineml.operations.finders;
import java.util.LinkedHashSet;
import java.util.Set;

import ac.biu.nlp.nlp.engineml.datastructures.FlippedBidirectionalMap;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.rules.BagOfRulesRuleBase;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseException;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleWithConfidenceAndDescription;
import ac.biu.nlp.nlp.engineml.operations.specifications.RuleSpecification;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedMatchCriteria;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.match.AllEmbeddedMatcher;
import eu.excitementproject.eop.common.representation.parse.tree.match.MatcherException;

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
	public void find() throws OperationException
	{
		try
		{
			specs = new LinkedHashSet<RuleSpecification>();
			for (RuleWithConfidenceAndDescription<Info, BasicNode> rule : ruleBase.getRules())
			{
				AllEmbeddedMatcher<ExtendedInfo, Info, ExtendedNode, BasicNode> matcher = 
					new AllEmbeddedMatcher<ExtendedInfo, Info, ExtendedNode, BasicNode>(new ExtendedMatchCriteria());
				
				matcher.setTrees(this.textTree.getTree(), rule.getRule().getLeftHandSide());
				matcher.findMatches();
				Set<BidirectionalMap<ExtendedNode, BasicNode>> matches = matcher.getMatches();
				
				for (BidirectionalMap<ExtendedNode, BasicNode> singleLhsMatch : matches)
				{
					BidirectionalMap<BasicNode, ExtendedNode> mapLhsToTree = new FlippedBidirectionalMap<BasicNode, ExtendedNode>(singleLhsMatch);
					boolean introduction = false;
					if (rule.getRule().isExtraction()!=null)
						introduction = rule.getRule().isExtraction().booleanValue();
					
					specs.add(new RuleSpecification(this.ruleBaseName,rule,mapLhsToTree,introduction));
				}
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
	
	
	
	private TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree;
	private BagOfRulesRuleBase<Info, BasicNode> ruleBase;
	private String ruleBaseName;
	
	private Set<RuleSpecification> specs = null;
}
