package eu.excitementproject.eop.transformations.operations.operations;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeConstructor;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.InfoServices;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * Given an original tree, a {@link SyntacticRule} and a mapping that represents the "match" of the
 * rule's left-hand-side to the tree, this class creates the instantiation of the right hand side
 * on that original tree.
 * <BR>
 * The right-hand-side instantiation can be later used in rule application:
 * For a substitution rule application, it will be a sub tree of the new tree (the generated tree).
 * For an introduction rule application, it will be the generated tree itself 
 *  
 * @author Asher Stern
 * @since Feb 13, 2011
 *
 * @param <IT> type of information on the tree's nodes.
 * @param <ST> type of tree's nodes (e.g. {@link ExtendedNode}).
 * @param <IR> type of information on the rule's nodes.
 * @param <SR> type of rule's nodes (e.g. {@link BasicNode}).
 */
public class RuleRhsInstantiation<IT , ST extends AbstractNode<IT, ST>, IR, SR extends AbstractNode<IR, SR>>
{
	/**
	 * 
	 * edgeInfoForRoot may be null
	 * 
	 * @param infoServices
	 * @param nodeConstructor
	 * @param tree
	 * @param rule
	 * @param mapRuleLhsToTree
	 * @param edgeInfoForRoot may be null. It represent the edge information from the
	 * root to its parent. It has no real meaning for introduction rule (unless an artificial
	 * root is added later), but it has meaning for substitution rules - it is the edge
	 * that connects the sub tree created by the rule to its parent.
	 */
	public RuleRhsInstantiation(InfoServices<IT, IR> infoServices,
			AbstractNodeConstructor<IT, ST> nodeConstructor, ST tree,
			SyntacticRule<IR, SR> rule, BidirectionalMap<SR, ST> mapRuleLhsToTree,
			IT edgeInfoForRoot)
	{
		this.infoServices = infoServices;
		this.nodeConstructor = nodeConstructor;
		this.tree = tree;
		this.rule = rule;
		this.mapRuleLhsToTree = mapRuleLhsToTree;
		this.edgeInfoForRoot = edgeInfoForRoot;
	}



	public void generate() throws TeEngineMlException
	{
		mapOrigToGenerated = new SimpleBidirectionalMap<ST, ST>();
		affectedNodes = new LinkedHashSet<ST>();
		generatedTree = copyRuleSubTree(rule.getRightHandSide(),edgeInfoForRoot);
	}

	public ST getGeneratedTree()
	{
		return generatedTree;
	}

	public BidirectionalMap<ST, ST> getMapOrigToGenerated()
	{
		return mapOrigToGenerated;
	}
	
	public Set<ST> getAffectedNodes()
	{
		return affectedNodes;
	}



	protected ST copyNonRuleSubTree(ST subtree)
	{
		ST ret = nodeConstructor.newNode(subtree.getInfo());
		if (subtree.getChildren()!=null)
		{
			for (ST child : subtree.getChildren())
			{
				if (!mapRuleLhsToTree.rightContains(child))
				{
					ret.addChild(copyNonRuleSubTree(child));
				}
			}
		}
		mapOrigToGenerated.put(subtree, ret);
		return ret;
	}
	
	
	/**
	 * 
	 * The edgeInfo should be null, except the case when ruleRhsSubTree is the root of RHS.
	 * @param ruleRhsSubTree
	 * @param edgeInfo
	 * @return
	 * @throws TeEngineMlException
	 */
	protected ST copyRuleSubTree(SR ruleRhsSubTree, IT edgeInfo) throws TeEngineMlException
	{
		IT retInfo = null;
		if (infoServices.isVariableR(ruleRhsSubTree.getInfo()))
		{
			SR lhsMappedNode = rule.getMapNodes().rightGet(ruleRhsSubTree);
			if (null==lhsMappedNode)
				throw new TeEngineMlException("unmapped variable");
			ST originalTreeMappedNode = mapRuleLhsToTree.leftGet(lhsMappedNode);
			if (null==edgeInfo)
			{
				retInfo = infoServices.newInfoFromTreeNodeAndRhsNodeAndRhsEdge(originalTreeMappedNode.getInfo(),ruleRhsSubTree.getInfo(),ruleRhsSubTree.getInfo());
			}
			else
			{
				retInfo = infoServices.newInfoFromTreeNodeRhsNodeAndEdge(originalTreeMappedNode.getInfo(),ruleRhsSubTree.getInfo(), edgeInfo);
			}
		}
		else
		{
			// not variable
			
			// if there is a corresponding node in the text tree, then
			// the AdditionalNodeInformation should come from that node.
			// See the main comment of the class GenerationOperation.
			if (rule.getMapNodes().rightContains(ruleRhsSubTree))
			{
				SR lhsMappedNode = rule.getMapNodes().rightGet(ruleRhsSubTree);
				ST originalTreeNode = mapRuleLhsToTree.leftGet(lhsMappedNode);
				if (null==originalTreeNode) throw new TeEngineMlException("left-hand-side or rule without a corresponding node in the text tree.");
				if (null==edgeInfo)
				{
					retInfo = infoServices.convertFromIRT(ruleRhsSubTree.getInfo(),originalTreeNode.getInfo());
				}
				else
				{
					retInfo = infoServices.newInfoRTT(ruleRhsSubTree.getInfo(), edgeInfo,originalTreeNode.getInfo());
				}
			}
			else
			{
				if (null==edgeInfo)
				{
					retInfo = infoServices.convertFromIR(ruleRhsSubTree.getInfo());
				}
				else
				{
					retInfo = infoServices.newInfoRT(ruleRhsSubTree.getInfo(), edgeInfo);
				}
			}
		}
		ST ret = nodeConstructor.newNode(retInfo);
		affectedNodes.add(ret);
		
		if (ruleRhsSubTree.getChildren()!=null)
		{
			for (SR child : ruleRhsSubTree.getChildren())
			{
				ST copiedChild = copyRuleSubTree(child, null);
				ret.addChild(copiedChild);
			}
		}
		
		// the rhs node has a corresponding lhs node
		if (rule.getMapNodes().rightContains(ruleRhsSubTree))
		{
			SR lhs = rule.getMapNodes().rightGet(ruleRhsSubTree);
			ST originalTreeNode = mapRuleLhsToTree.leftGet(lhs);
			if (originalTreeNode.getChildren()!=null)
			{
				// for each child of that lhs (in the tree) that is not mapped to the rule - copy it.
				for (ST originalTreeChild : originalTreeNode.getChildren())
				{
					if (!mapRuleLhsToTree.rightContains(originalTreeChild))
					{
						ST copiedChild = copyNonRuleSubTree(originalTreeChild);
						ret.addChild(copiedChild);
						
						// Asher 7-Dec-2011: Commenting this line. Not sure that children not-involved should be marked as affected-nodes.
						// affectedNodes.add(copiedChild); // Only child of a node that is part of the rule. Not other nodes in that sub-tree.
					}
				}
			}
		}
		
		if (rule.getMapNodes().rightContains(ruleRhsSubTree))
		{
			SR lhs = rule.getMapNodes().rightGet(ruleRhsSubTree);
			ST originalTreeNode = mapRuleLhsToTree.leftGet(lhs);
			mapOrigToGenerated.put(originalTreeNode, ret);
		}
		
		return ret;
	}
	
	
	protected InfoServices<IT, IR> infoServices;
	protected AbstractNodeConstructor<IT, ST> nodeConstructor;
	protected ST tree;
	protected SyntacticRule<IR,SR> rule;
	protected BidirectionalMap<SR, ST> mapRuleLhsToTree;
	protected IT edgeInfoForRoot;
	
	protected ST generatedTree;
	protected BidirectionalMap<ST, ST> mapOrigToGenerated;

	protected Set<ST> affectedNodes;

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(RuleRhsInstantiation.class);
}
