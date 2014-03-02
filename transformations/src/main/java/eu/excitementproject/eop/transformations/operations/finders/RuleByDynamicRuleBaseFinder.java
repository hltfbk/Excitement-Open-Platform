package eu.excitementproject.eop.transformations.operations.finders;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.component.syntacticknowledge.RuleWithConfidenceAndDescription;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.FlippedBidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.match.AllEmbeddedMatcher;
import eu.excitementproject.eop.common.representation.parse.tree.match.MatcherException;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.transformations.datastructures.LemmaAndPos;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.DynamicRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.RuleBase;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.operations.specifications.RuleSpecification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedMatchCriteria;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.InfoObservations;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * 
 * Given a tree, and a rule base of the type {@link DynamicRuleBase},
 * this class finds which rules from that rule base can be applied on the text tree.
 * This class does not look at the hypothesis at all.
 * <P>
 * No caching is done in this finder, since the assumption is that
 * caching is done in the {@linkplain RuleBase} itself (the {@link DynamicRuleBase}).
 * 
 * @deprecated No longer used.
 * 
 * @author Asher Stern
 * @since Feb 14, 2011
 *
 */
@Deprecated
public class RuleByDynamicRuleBaseFinder implements Finder<RuleSpecification>
{
	public RuleByDynamicRuleBaseFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap,
			boolean introduction, DynamicRuleBase<Info, BasicNode> ruleBase,
			String ruleBaseName)
	{
		super();
		this.treeAndParentMap = treeAndParentMap;
		this.introduction = introduction;
		this.ruleBase = ruleBase;
		this.ruleBaseName = ruleBaseName;
	}
	
	@Override public void optionallyOptimizeRuntimeByAffectedNodes(Set<ExtendedNode> affectedNodes) throws OperationException
	{}

	@Override
	public void find() throws OperationException
	{
			findMatchs();
			extractMatchedRules();
			buildSpecifications();
	}
	
	@Override
	public Set<RuleSpecification> getSpecs() throws OperationException
	{
		if (null==specs)
			throw new OperationException("You did not call find()");
		return specs;
	}


	private void constructSetOfLemmas() throws TeEngineMlException
	{
		lemmas = new LinkedHashSet<LemmaAndPos>();
		for (ExtendedNode node : AbstractNodeUtils.treeToSet(treeAndParentMap.getTree()))
		{
			if (InfoObservations.infoHasLemma(node.getInfo()))
			{
				String lemma = InfoGetFields.getLemma(node.getInfo());
				PartOfSpeech pos = InfoGetFields.getPartOfSpeechObject(node.getInfo());
				LemmaAndPos lemmaAndPos = new LemmaAndPos(lemma, pos);
				
				lemmas.add(lemmaAndPos);
			}
		}
	}
	
	private void findAllLeftHandSides() throws RuleBaseException, TeEngineMlException
	{
		constructSetOfLemmas();
		this.leftHandSides = new LinkedHashSet<BasicNode>();
		
		for (LemmaAndPos lemmaAndPos : lemmas)
		{
			ImmutableSet<BasicNode> lhss = ruleBase.getLeftHandSidesByLemmaAndPos(lemmaAndPos);
			for (BasicNode lhs : lhss)
			{
				this.leftHandSides.add(lhs);
			}
		}
	}
	
	private void findMatchs() throws OperationException
	{
		try
		{
			findAllLeftHandSides();
			matchedLeftHandSides = new SimpleValueSetMap<BasicNode, BidirectionalMap<BasicNode,ExtendedNode>>();
			for (BasicNode lhs : leftHandSides)
			{
				AllEmbeddedMatcher<ExtendedInfo, Info, ExtendedNode, BasicNode> matcher = new AllEmbeddedMatcher<ExtendedInfo, Info, ExtendedNode, BasicNode>(new ExtendedMatchCriteria());
				matcher.setTrees(treeAndParentMap.getTree(), lhs);
				matcher.findMatches();
				Set<BidirectionalMap<ExtendedNode, BasicNode>> matches = matcher.getMatches();
				if (matches!=null){if (matches.size()>0)
				{
					for (BidirectionalMap<ExtendedNode, BasicNode> singleMatch : matches)
					{
						matchedLeftHandSides.put(lhs, new FlippedBidirectionalMap<BasicNode, ExtendedNode>(singleMatch));
					}
				}}
			}
		}
		catch (RuleBaseException e)
		{
			throw new OperationException("error",e);
		} catch (TeEngineMlException e)
		{
			throw new OperationException("error",e);
		} catch (MatcherException e)
		{
			throw new OperationException("error",e);
		}
	}
	
	private void extractMatchedRules() throws OperationException
	{
		try
		{
			allMatchedRules = new LinkedHashMap<BasicNode, ImmutableSet<RuleWithConfidenceAndDescription<Info,BasicNode>>>();
			for (BasicNode lhs : matchedLeftHandSides.keySet())
			{
				if (!allMatchedRules.containsKey(lhs))
				{
					ImmutableSet<RuleWithConfidenceAndDescription<Info, BasicNode>> rules = ruleBase.getRulesByLeftHandSide(lhs);
					if (null==rules)throw new OperationException("DynamicRuleBase returned null for a certain left hand side, that was earlier returned by getLeftHandSidesByLemmaAndPos()");
					allMatchedRules.put(lhs, rules);
				}
			}
		}
		catch (RuleBaseException e)
		{
			throw new OperationException("error",e);
		}
	}

	private void buildSpecifications()
	{
		specs = new LinkedHashSet<RuleSpecification>();
		for (BasicNode lhs : matchedLeftHandSides.keySet())
		{
			for (RuleWithConfidenceAndDescription<Info, BasicNode> rule : allMatchedRules.get(lhs))
			{
				for (BidirectionalMap<BasicNode,ExtendedNode> mapLhsToTree : matchedLeftHandSides.get(lhs))
				{
					specs.add(new RuleSpecification(ruleBaseName,rule,mapLhsToTree,this.introduction));
				}
				
			}
		}
	}
	
	
	
	
	
	
	
	private TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap;
	private boolean introduction;
	private DynamicRuleBase<Info, BasicNode> ruleBase;
	private String ruleBaseName;
	
	private Set<LemmaAndPos> lemmas;
	private Set<BasicNode> leftHandSides;
	
	/**
	 * map from left hand side to a bidirectional map from lhs to tree
	 */
	private ValueSetMap<BasicNode, BidirectionalMap<BasicNode,ExtendedNode>> matchedLeftHandSides;
	private Map<BasicNode, ImmutableSet<RuleWithConfidenceAndDescription<Info, BasicNode>>> allMatchedRules;
	
	
	private Set<RuleSpecification> specs = null;
}
