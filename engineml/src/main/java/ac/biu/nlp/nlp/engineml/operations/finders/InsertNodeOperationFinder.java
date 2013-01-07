package ac.biu.nlp.nlp.engineml.operations.finders;

import java.util.LinkedHashSet;
import java.util.Set;

import ac.biu.nlp.nlp.engineml.alignment.AlignmentCalculator;
import ac.biu.nlp.nlp.engineml.alignment.AlignmentCriteria;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.specifications.InsertNodeSpecification;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.utilities.parsetreeutils.TreeUtilities;
import ac.biu.nlp.nlp.general.ValueSetMap;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;


/**
 * This class finds nodes that can be added to a given text tree, to make it
 * closed (i.e. more equal) to a given hypothesis tree.
 * It finds which nodes can be inserted, and where in the tree they should be inserted.
 * That information can be used to make an unjustified insert operation. 
 * 
 * @author Asher Stern
 * @since Dec 30, 2010
 *
 */
public class InsertNodeOperationFinder implements Finder<InsertNodeSpecification>
{
	public InsertNodeOperationFinder(TreeAndParentMap<ExtendedInfo,ExtendedNode> textTree,
			TreeAndParentMap<ExtendedInfo,ExtendedNode> hypothesisTree,
			AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria) throws OperationException
	{
		super();
		if (null==textTree) throw new OperationException("null");
		if (null==hypothesisTree) throw new OperationException("null");
		this.textTree = textTree;
		this.hypothesisTree = hypothesisTree;
		this.alignmentCriteria = alignmentCriteria;
	}
	
	@Override
	public void find() throws OperationException
	{
		insertSpecifications = new LinkedHashSet<InsertNodeSpecification>();
		
//		Set<ExtendedNode> missingNodes;
//		if (AdvancedEqualities.USE_ADVANCED_EQUALITIES)
//		{
//			matchingNodesHypothesisToTextMap = AdvancedEqualities.findMatchingNodes(textTree.getTree(),hypothesisTree.getTree());
//			missingNodes = AdvancedEqualities.findMissingRelations(this.textTree,this.hypothesisTree);
//		}
//		else
//		{
//			matchingNodesHypothesisToTextMap = TreeUtilities.findAllMatchingNodes(hypothesisTree.getTree(), textTree.getTree());
//			missingNodes = TreeUtilities.findRelationsNoMatch(textTree,hypothesisTree);
//		}
		
		AlignmentCalculator alignmentCalculator = new AlignmentCalculator(alignmentCriteria, textTree, hypothesisTree);
		Set<ExtendedNode> missingNodes = alignmentCalculator.getMissingTriples();
		matchingNodesHypothesisToTextMap = alignmentCalculator.getMapAlignedNodesFromHypothesisToText();
		
		
		for (ExtendedNode missingNode : missingNodes)
		{
			// missingNode is a node in the hypothesis tree.
			if (hypothesisTree.getParentMap().containsKey(missingNode))
			{
				ExtendedNode missingNodeParent = hypothesisTree.getParentMap().get(missingNode);
				// missingNodeParent is also a node in the hypothesis tree.
				if (TreeUtilities.notEmpty(matchingNodesHypothesisToTextMap.get(missingNodeParent)))
				{
					for (ExtendedNode textMatchingParent : matchingNodesHypothesisToTextMap.get(missingNodeParent))
					{
						insertSpecifications.add(new InsertNodeSpecification(missingNode, textMatchingParent));
					}
				}
			}
			else
			{
				throw new OperationException("Since an artificial root should exist - the root should not be returned.");
			}
		}
		
		
		
		
		
		
		
		
		
		
//		Set<EnglishNode> hypothesisNodes = AbstractNodeUtils.treeToSet(this.hypothesisTree.getTree());
//		Set<EnglishNode> hypothesisMissingNodes = TreeUtilities.findNodesNoMatch(textTree, hypothesisTree);
//		
//		for (EnglishNode hypothesisNode : hypothesisNodes)
//		{
//			EnglishNode parentNode = hypothesisTree.getParentMap().get(hypothesisNode);
//			if (parentNode!=null)
//			{
//				if (
//					hypothesisMissingNodes.contains(hypothesisNode) // current node is missing
//					&&
//					TreeUtilities.notEmpty(matchingNodesHypothesisToTextMap.get(parentNode)) // but parent does exist
//					)
//				{
//					for (EnglishNode optionalParentInText : matchingNodesHypothesisToTextMap.get(parentNode))
//					{
//						insertSpecifications.add(new InsertNodeSpecification(hypothesisNode, optionalParentInText));
//					}
//				}
//					
//					
//			}
//		}
		
	}
	

	public Set<InsertNodeSpecification> getSpecs() throws OperationException
	{
		if (null==insertSpecifications) throw new OperationException("find() was not called.");
		return insertSpecifications;
	}


	
	private final TreeAndParentMap<ExtendedInfo,ExtendedNode> textTree;
	private final TreeAndParentMap<ExtendedInfo,ExtendedNode> hypothesisTree;
	private final AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria;
	
	private ValueSetMap<ExtendedNode, ExtendedNode> matchingNodesHypothesisToTextMap = null;
	private Set<InsertNodeSpecification> insertSpecifications = null;
	
	
	
	
}
