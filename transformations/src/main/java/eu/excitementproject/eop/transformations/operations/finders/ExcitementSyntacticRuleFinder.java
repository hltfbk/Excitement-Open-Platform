package eu.excitementproject.eop.transformations.operations.finders;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.common.component.syntacticknowledge.RuleMatch;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticResource;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticResourceException;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeCopier;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNodeConstructor;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleWithConfidenceAndDescription;
import eu.excitementproject.eop.transformations.operations.specifications.RuleSpecification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;

/**
 * 
 * @author Asher Stern
 * @since Feb 16, 2014
 *
 */
public class ExcitementSyntacticRuleFinder implements Finder<RuleSpecification>
{
	public ExcitementSyntacticRuleFinder(
			SyntacticResource<Info, BasicNode> ruleBase, String ruleBaseName,
			ExtendedNode textTree, BasicNode hypothesisTree)
	{
		super();
		this.ruleBase = ruleBase;
		this.ruleBaseName = ruleBaseName;
		this.textTree = textTree;
		this.hypothesisTree = hypothesisTree;
	}

	@Override
	public void find() throws OperationException
	{
		specs = null;
		try
		{
			TreeCopier<ExtendedInfo, ExtendedNode, Info, BasicNode> treeCopier =
					new TreeCopier<ExtendedInfo, ExtendedNode, Info, BasicNode>(
							textTree,
							new TreeCopier.InfoConverter<ExtendedNode, Info>()
							{
								public Info convert(ExtendedNode oi)
								{
									return oi.getInfo();
								}
							},
							new BasicNodeConstructor()
							);
			treeCopier.copy();
			BasicNode textTreeAsBasicNode = treeCopier.getGeneratedTree();
			BidirectionalMap<ExtendedNode, BasicNode> mapTextTree = treeCopier.getNodesMap();

			specs = new LinkedHashSet<>();
			List<RuleMatch<Info,BasicNode>> matches = ruleBase.findMatches(textTreeAsBasicNode, hypothesisTree);
			for (RuleMatch<Info,BasicNode> match : matches)
			{
				specs.add(convertRuleMatch(match, mapTextTree));
			}
		}
		catch (SyntacticResourceException e)
		{
			throw new OperationException("Syntactic resource error. See nested exception.",e);
		}
	}

	@Override
	public void optionallyOptimizeRuntimeByAffectedNodes(Set<ExtendedNode> affectedNodes) throws OperationException
	{
		// do nothing.
	}

	@Override
	public Set<RuleSpecification> getSpecs() throws OperationException
	{
		if (null==specs) {throw new OperationException("find() was not called.");}
		return specs;
	}

	
	private RuleSpecification convertRuleMatch(RuleMatch<Info,BasicNode> ruleMatch, BidirectionalMap<ExtendedNode, BasicNode> mapTextTree)
	{
		BidirectionalMap<BasicNode, ExtendedNode> mapLhsToTree = new SimpleBidirectionalMap<>();
		BidirectionalMap<BasicNode,BasicNode> mapLhsToTreeInRuleMatch = ruleMatch.getMapLHStoTree();
		for (BasicNode lhsNode : mapLhsToTreeInRuleMatch.leftSet())
		{
			BasicNode treeNodeInBasicNodeTree = mapLhsToTreeInRuleMatch.leftGet(lhsNode);
			ExtendedNode treeNodeInExtendedTree = mapTextTree.rightGet(treeNodeInBasicNodeTree);
			mapLhsToTree.put(lhsNode, treeNodeInExtendedTree);
		}
		RuleWithConfidenceAndDescription<Info,BasicNode> rule = new RuleWithConfidenceAndDescription<Info,BasicNode>(ruleMatch.getRule(), E_MINUS_1, "syntactic rule");
		
		return new RuleSpecification(ruleBaseName,rule,mapLhsToTree,false);
	}
	
	
	
	private final SyntacticResource<Info, BasicNode> ruleBase;
	private final String ruleBaseName;
	private final ExtendedNode textTree;
	private final BasicNode hypothesisTree;
	
	private Set<RuleSpecification> specs = null;
	
	private static final double E_MINUS_1 = Math.exp(-1);
}
