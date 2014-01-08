package eu.excitementproject.eop.transformations.operations.finders;
import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.alignment.AlignmentCalculator;
import eu.excitementproject.eop.transformations.alignment.AlignmentCriteria;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.specifications.MoveNodeSpecification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;

/**
 * This class finds nodes in the text tree, that do exist in the hypothesis tree, but
 * in a different location (i.e. modify a different parent).
 * The class finds the nodes and specifies where they should be moved to.
 * 
 * @author Asher Stern
 * @since Dec 30, 2010
 *
 */
public class MoveNodeOperationFinder implements Finder<MoveNodeSpecification>
{
	public MoveNodeOperationFinder(TreeAndParentMap<ExtendedInfo,ExtendedNode> textTree,
			TreeAndParentMap<ExtendedInfo,ExtendedNode> hypothesisTree,
			AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria) throws OperationException
	{
		super();
		this.textTree = textTree;
		this.hypothesisTree = hypothesisTree;
		this.alignmentCriteria = alignmentCriteria;
	}
	
	@Override public void optionallyOptimizeRuntimeByAffectedNodes(Set<ExtendedNode> affectedNodes) throws OperationException
	{}

	@Override
	public void find() throws OperationException
	{
		moveSpecifications = new LinkedHashSet<MoveNodeSpecification>();
		AlignmentCalculator alignmentCalculator = new AlignmentCalculator(alignmentCriteria, textTree, hypothesisTree);
		ValueSetMap<ExtendedNode, ExtendedNode> matchHypothesisToTextForParent = alignmentCalculator.getMapAlignedNodesFromHypothesisToText();
		ValueSetMap<ExtendedNode, ExtendedNode> matchHypothesisToTextForChild = alignmentCalculator.getMapSimilarNodesFromHypothesisToText();
		Set<ExtendedNode> relationsNoMatch = alignmentCalculator.getMissingTriples();
		
//		if (AdvancedEqualities.USE_ADVANCED_EQUALITIES)
//		{
//			matchHypothesisToTextForParent = AdvancedEqualities.findMatchingNodes(textTree.getTree(), hypothesisTree.getTree());
//			matchHypothesisToTextForChild = AdvancedEqualities.findSimilarContentNodes(textTree.getTree(), hypothesisTree.getTree());
//			relationsNoMatch = AdvancedEqualities.findMissingRelations(textTree, hypothesisTree);
//		}
//		else
//		{
//			matchHypothesisToTextForParent = TreeUtilities.findAllMatchingNodes(hypothesisTree.getTree(), textTree.getTree());
//			matchHypothesisToTextForChild = matchHypothesisToTextForParent;
//			relationsNoMatch = TreeUtilities.findRelationsNoMatch(textTree, hypothesisTree);
//		}

		
		for (ExtendedNode hypothesisChild : relationsNoMatch)
		{
			ExtendedNode hypothesisParent = hypothesisTree.getParentMap().get(hypothesisChild);
			if (hypothesisParent!=null)
			{
				if (
						TreeUtilities.notEmpty(matchHypothesisToTextForChild.get(hypothesisChild))
						&&
						TreeUtilities.notEmpty(matchHypothesisToTextForParent.get(hypothesisParent))
				)
				{
					// matching nodes for the hypothesis node and for its parent exist in the text
					for (ExtendedNode textChild : matchHypothesisToTextForChild.get(hypothesisChild))
					{
						for (ExtendedNode textParent : matchHypothesisToTextForParent.get(hypothesisParent))
						{
							if (textChild!=textParent)
							{
								moveSpecifications.add(
										new MoveNodeSpecification(textChild, textParent, hypothesisChild.getInfo().getEdgeInfo(),hypothesisChild.getInfo())
								);
							}
						}
					}
				}
			}
			
		}
		
		LinkedHashSet<MoveNodeSpecification> withDuplicates = new LinkedHashSet<MoveNodeSpecification>();
		withDuplicates.addAll(moveSpecifications);
		for (MoveNodeSpecification spec : moveSpecifications)
		{
			withDuplicates.add(new MoveNodeSpecification(spec, true));
		}
		this.moveSpecifications = withDuplicates;
	}
	
	@Override
	public Set<MoveNodeSpecification> getSpecs() throws OperationException
	{
		if (null==moveSpecifications) throw new OperationException("find() was not called.");
		return moveSpecifications;
	}

	private TreeAndParentMap<ExtendedInfo,ExtendedNode> textTree;
	private TreeAndParentMap<ExtendedInfo,ExtendedNode> hypothesisTree;
	private AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria;
	
	private Set<MoveNodeSpecification> moveSpecifications = null;


	
	
	
	
	
	
	
	
	
	
	
	
	
//	public void findold() throws OperationException
//	{
//		moveSpecifications = new LinkedHashSet<MoveNodeSpecification>();
//		Set<ExtendedNode> hypothesisNodesToMove = TreeUtilities.findNodeBadParents(textTree, hypothesisTree);
//		ValueSetMap<ExtendedNode, ExtendedNode> matchingNodesHypothesisToText = TreeUtilities.findAllMatchingNodes(hypothesisTree.getTree(), textTree.getTree());
//		
//		for (ExtendedNode hypothesisNodeToMove : hypothesisNodesToMove)
//		{
//			if (!TreeUtilities.notEmpty(matchingNodesHypothesisToText.get(hypothesisNodeToMove)))
//				throw new OperationException("Bug. That node returned by TreeUtilities.findNodeBadParents(), so there must be matching node in the text.");
//			
//			ExtendedNode hypothesisParent = hypothesisTree.getParentMap().get(hypothesisNodeToMove);
//			if (null==hypothesisParent) throw new OperationException("Bug. That node returned by TreeUtilities.findNodeBadParents(), so it is not root.");
//			if (TreeUtilities.notEmpty(matchingNodesHypothesisToText.get(hypothesisParent)))
//			{
//				// we have a node (hypothesisNodeToMove) that exists in the text, and its
//				// (optional) parent (or parents) also exists in the text.
//				for (ExtendedNode matchingTextNodeToMove : matchingNodesHypothesisToText.get(hypothesisNodeToMove))
//				{
//					for (ExtendedNode matchingTextParent : matchingNodesHypothesisToText.get(hypothesisParent))
//					{
//						moveSpecifications.add(new MoveNodeSpecification(matchingTextNodeToMove, matchingTextParent,hypothesisNodeToMove.getInfo().getEdgeInfo()));
//					}
//				}
//			}
//		}
//	}

	
}
