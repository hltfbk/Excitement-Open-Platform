/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.truthteller.application;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.generic.truthteller.application.merge.AnnotationsMerger;
import eu.excitementproject.eop.transformations.generic.truthteller.application.merge.AnnotationsMergerException;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.AnnotationRule;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.BasicRuleAnnotations;
import eu.excitementproject.eop.transformations.generic.truthteller.services.StrictExtendedMatchCriteria;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.representation.AdditionalNodeInformation;
import eu.excitementproject.eop.transformations.representation.ExtendedConstructionNode;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;

/**
 * This class applies an {@link AnnotationRule} to an {@link ExtendedConstructionNode} tree, given a map between the tree and the LHS of the rule. 
 * Applying an annotation rule means to replace (some of) the annotations on some tree nodes, so this is done in one scan of the tree, where each tree node's
 * annotations are superimposed by the annotations from the matching rule-RHS-node. Typically, each RHS node only holds values for 1 or 2 annotations, and only 
 * those are imposed on the matching tree node.
 * <P>
 * {@link #getAffectedNodes()} indicates whether the last application altered the content of the tree. 
 * {@link #getAffectedNodes()} returns the nodes affected technically by the application, but this may mean that some of them had their {@link AdditionalNodeInformation}
 * replaced with an equivalent one (same content). 
 * 
 *  
 * 
 * @author amnon
 * @since Dec 25, 2011
 */
public class DefaultAnnotationRuleApplicationOperation {	 

	private final Map<ExtendedNode, BasicRuleAnnotations> mapLhsToAnnotations;
	private final BidirectionalMap<ExtendedNode, ExtendedConstructionNode> mapLhsToTree;
	private final TreeAndParentMap<ExtendedInfo, ExtendedConstructionNode> treeAndParentMap;
	private final AnnotationsMerger annotationsMerger;
	private Set<ExtendedConstructionNode> affectedNodes;
	/**
	 * indicates whether the last application alter the content of the tree
	 */
	private boolean treeWasAltered = false;

	/**
	 * Ctor
	 * @param textTreeAndParentMap
	 * @param mapLhsToAnnotations 
	 * @param matchOfLhsToTree 
	 * @param annotatiorMerger 
	 * @throws OperationException
	 */
	public DefaultAnnotationRuleApplicationOperation(TreeAndParentMap<ExtendedInfo, ExtendedConstructionNode> textTreeAndParentMap, 
			BidirectionalMap<ExtendedNode, ExtendedConstructionNode> matchOfLhsToTree, 
			Map<ExtendedNode, BasicRuleAnnotations> mapLhsToAnnotations, AnnotationsMerger annotatiorMerger) throws OperationException {
		if (null==textTreeAndParentMap) 
			throw new AnnotationOperationException("null textTree");
		if (mapLhsToAnnotations == null)
			throw new AnnotationOperationException("Got null mapLhsToAnnotations");
		if (matchOfLhsToTree == null)
			throw new AnnotationOperationException("Got null mapLhsToTree");
		if (mapLhsToAnnotations.size() != matchOfLhsToTree.size())
			throw new AnnotationOperationException("The number of tree nodes to map ("+matchOfLhsToTree.size()+")and the number of new annotation records to apply ("+mapLhsToAnnotations.size()+") don't match");
		if (annotatiorMerger == null)
			throw new AnnotationOperationException("null annotatiorMerger");

		this.treeAndParentMap = textTreeAndParentMap;
		this.mapLhsToAnnotations = mapLhsToAnnotations;
		this.mapLhsToTree = matchOfLhsToTree;
		this.annotationsMerger = annotatiorMerger;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.annotation.match.AnnotationRuleApplicationOperation#generate()
	 */
	public void annotateTheTree() throws AnnotationOperationException {
		
		treeWasAltered = false;
		
		// cross the tree-to-LHS map with the LHS-to-RHS-annotations map
		Map<ExtendedConstructionNode, BasicRuleAnnotations> mapTreeNodesToAnnotations = new LinkedHashMap<ExtendedConstructionNode, BasicRuleAnnotations>();
		for (ExtendedNode lhsNode : mapLhsToAnnotations.keySet())
		{
			if (!mapLhsToTree.leftContains(lhsNode))
				throw new AnnotationOperationException("Matching bug! This node is mapped in the rule but not matched against the text tree: " + lhsNode);
			mapTreeNodesToAnnotations.put(mapLhsToTree.leftGet(lhsNode), mapLhsToAnnotations.get(lhsNode));
		}
			
		// replace annotations in the tree
		replaceAnnotationsOfTree(treeAndParentMap.getTree(), mapTreeNodesToAnnotations);
		
		affectedNodes = mapTreeNodesToAnnotations.keySet(); 
	}
	
	/**
	 * @return the affectedNodes
	 */
	public Set<ExtendedConstructionNode> getAffectedNodes() {
		return affectedNodes;
	}
	
	/**
	 * indicates whether the last application alter the content of the tree
	 * @return the treeWasAltered
	 */
	public boolean treeWasAltered() {
		return treeWasAltered;
	}
	
	///////////////////////////////////////////////////// protected	 /////////////////////////////////////////////////////////////////////////////

	/**
	 	recursively scan the tree and convert all its annotation according to the mapping
	 * @param tree
	 * @param mapTreeNodesToAnnotations
	 * @throws AnnotationOperationException 
	 */
	protected void replaceAnnotationsOfTree(ExtendedConstructionNode tree, Map<ExtendedConstructionNode, BasicRuleAnnotations> mapTreeNodesToAnnotations) 
			throws AnnotationOperationException 
	{
		replaceAnnotationsOfNode(tree, mapTreeNodesToAnnotations);
		if (tree.hasChildren())
			for (ExtendedConstructionNode child : tree.getChildren())
				replaceAnnotationsOfTree(child, mapTreeNodesToAnnotations); 
	}

	
	/**
	 * replace the given node's annotations with the given map
	 * @param node
	 * @param mapTreeNodesToAnnotations 
	 * @return
	 * @throws AnnotationOperationException 
	 */
	protected void replaceAnnotationsOfNode(ExtendedConstructionNode node, Map<ExtendedConstructionNode, BasicRuleAnnotations> mapTreeNodesToAnnotations) 
			throws AnnotationOperationException {
		AdditionalNodeInformation origAnnotaions = node.getInfo().getAdditionalNodeInformation();
		BasicRuleAnnotations newAnnotations = mapTreeNodesToAnnotations.get(node);
		
		AdditionalNodeInformation mergedAnnotations;
		try {
			mergedAnnotations = annotationsMerger.mergeAnnotations(origAnnotaions, newAnnotations);
		} catch (AnnotationsMergerException e) {
			throw new AnnotationOperationException("See nested", e);
		}
		
		node.setInfo(new ExtendedInfo(node.getInfo(),  mergedAnnotations));
		treeWasAltered |= !StrictExtendedMatchCriteria.additionalNodeInformationMatch(origAnnotaions, mergedAnnotations);
	}
}
