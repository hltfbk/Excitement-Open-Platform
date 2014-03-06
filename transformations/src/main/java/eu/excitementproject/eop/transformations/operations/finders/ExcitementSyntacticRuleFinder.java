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
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.TreeCopier;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNodeConstructor;
import eu.excitementproject.eop.transformations.operations.OperationException;
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
			copiedTextTreeNodes = AbstractNodeUtils.treeToLinkedHashSet(textTreeAsBasicNode);

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

	
	private RuleSpecification convertRuleMatch(RuleMatch<Info,BasicNode> ruleMatch, BidirectionalMap<ExtendedNode, BasicNode> mapTextTree) throws OperationException
	{
		BidirectionalMap<BasicNode, ExtendedNode> mapLhsToTree = new SimpleBidirectionalMap<>();
		BidirectionalMap<BasicNode,BasicNode> mapLhsToTreeInRuleMatch = ruleMatch.getMapLHStoTree();
		for (BasicNode lhsNode : mapLhsToTreeInRuleMatch.leftSet())
		{
			if (null==lhsNode) {throw new OperationException("Malformed rule match has returned from the resource. A null node is contained as a rule node in the map from LHS to tree.");}
			BasicNode treeNodeInBasicNodeTree = mapLhsToTreeInRuleMatch.leftGet(lhsNode);
			if (null==treeNodeInBasicNodeTree)
			{
				String lhsLemma = InfoGetFields.getLemma(lhsNode.getInfo());
				throw new OperationException("Malformed rule match has returned from the resource. The map from LHS to tree maps a rule node to a null.\n" +
						"The rule node is: \""+lhsLemma+"\".");
			}
			if (!copiedTextTreeNodes.contains(treeNodeInBasicNodeTree))
			{
				String lemmaOfTextTreeNode = InfoGetFields.getLemma(treeNodeInBasicNodeTree.getInfo());
				String idOfTextTreeNode = "info_is_null";
				if (treeNodeInBasicNodeTree.getInfo()!=null)
				{
					idOfTextTreeNode = treeNodeInBasicNodeTree.getInfo().getId();
					if (null==idOfTextTreeNode) {idOfTextTreeNode="id_is_null";}
				}
				throw new OperationException("The map from LHS to tree maps into a tree node that did not exist in the original tree given to the resource.\nThe node lemma is \""+lemmaOfTextTreeNode+"\", the ID is \""+idOfTextTreeNode+"\".");
			}
			ExtendedNode treeNodeInExtendedTree = mapTextTree.rightGet(treeNodeInBasicNodeTree);
			mapLhsToTree.put(lhsNode, treeNodeInExtendedTree);
		}
		
		return new RuleSpecification(ruleBaseName,ruleMatch.getRule(),mapLhsToTree,false);
	}
	
	
	
	private final SyntacticResource<Info, BasicNode> ruleBase;
	private final String ruleBaseName;
	private final ExtendedNode textTree;
	private final BasicNode hypothesisTree;
	
	private Set<BasicNode> copiedTextTreeNodes;
	
	private Set<RuleSpecification> specs = null;
	
}
