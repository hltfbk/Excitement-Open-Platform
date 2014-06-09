/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.rule_compiler.charger;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import eu.excitementproject.eop.common.datastructures.Pair;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractConstructionNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.transformations.generic.rule_compiler.CompilationException;
import eu.excitementproject.eop.transformations.generic.rule_compiler.RuleCompileServices;
import eu.excitementproject.eop.transformations.generic.rule_compiler.utils.PairSet;
import eu.excitementproject.eop.transformations.representation.ExtendedInfoGetFields;

/**
 * This class contains some static generic utilities helpful for building an entailment/annotation rule
 * from smaller bits and pieces
 * @author Amnon Lotan
 *
 * @since Jul 2, 2012
 */
public class RuleBuildingUtils {
	
	/**
	 * Scan for the key to a unique vlaue that is a Cgx node's label
	 * @param nodesMap
	 * @param label 
	 * @return
	 * @throws CompilationException 
	 */
	public static <I> I getIdOfUniqueLabel(Map<I, String> nodesMap, String label) throws CompilationException {
		I idOfLabel = null;
		for (I id : nodesMap.keySet())
			if (nodesMap.get(id) != null & nodesMap.get(id).toUpperCase().equals(label))
				if (idOfLabel == null)
					idOfLabel = id;
				else
					throw new CompilationException("There is more than one "+label+" Concept in the graph");
		if (idOfLabel == null)
			throw new CompilationException("There are no "+ label+" Concepts in the graph. You must make one");
		
		return idOfLabel;
	}

	/**
	 * @param originId
	 * @param undirectedEdges
	 * @return
	 */
	public static List<Long> getNeighboringIds(Long originId,	PairSet<Long> undirectedEdges) {
		List<Long> neighborIds = new Vector<Long>();
		ImmutableSet<Pair<Long>> edgesFromOriginId = undirectedEdges.getPairContaining(originId);
		if (edgesFromOriginId != null)
			for (Pair<Long> edge : edgesFromOriginId)
			{
				Long[] ids = edge.toSet().toArray(new Long[2]);
				neighborIds.add(ids[0].equals(originId) ?	ids[1] :	ids[0]);	// add the id on the other side of the edge
			}
		return neighborIds;
	}
	
	/**
	 * 1. get the id of the root node of the tree (beneath the label), AND remove the edge to the label from the edge set<br>
	 * 2. build a tree under the rootId, based on the map of nodes and the set of edges.<br>
	 * In the process, the undirectedEdges pair set is emptied of all the traversed edges.<br>
	 * <b>Assumption</b>: the root has no upwards edge!
	 * 
	 * @param nodeLabelsMap
	 * @param undirectedEdges
	 * @param treeRootLabel
	 * @param nodesMap
	 * @param compilationServices
	 * @return
	 * @throws CompilationException
	 */
	public static  <I extends Info, N extends AbstractNode<I, N>, CN extends AbstractConstructionNode<I, CN>> 
		CN buildTreeUnderLabel( 
		String treeRootLabel, Map<Long, String> nodeLabelsMap, PairSet<Long> undirectedEdges,  Map<Long, CN> nodesMap, RuleCompileServices<I, N, CN> compilationServices)
			throws CompilationException
	{
		if (nodeLabelsMap == null || nodeLabelsMap.isEmpty())		throw new CompilationException("got null nodeLabelsMap");
		if (undirectedEdges == null)		throw new CompilationException("got null undirectedEdges");
		if (nodesMap == null)		throw new CompilationException("got null nodesMap");
		if (compilationServices == null)	throw new CompilationException("got null compilationServices");
		
		// get the id of the root node of the tree (beneath the label)
		Long rootId = RuleBuildingUtils.getIdOfTreeRoot(nodeLabelsMap, undirectedEdges, treeRootLabel);
		
		CN root = RuleBuildingUtils.buildTree(rootId, nodeLabelsMap, undirectedEdges, nodesMap, compilationServices);
		
		return root;
	}

	/**
	 * get the id of the root node of the tree (beneath the label), AND remove the edge to the label from the edge set
	 * 
	 * @param nodesMap
	 * @param undirectedEdges
	 * @param treeRootLabel
	 * @return
	 * @throws CompilationException 
	 */
	public static Long getIdOfTreeRoot(Map<Long, String> nodesMap, PairSet<Long> undirectedEdges, String treeRootLabel) throws CompilationException {

		Long lhsLabelId = RuleBuildingUtils.getIdOfUniqueLabel(nodesMap, treeRootLabel);
		List<Long> childrenIds = RuleBuildingUtils.getNeighboringIds(lhsLabelId , undirectedEdges);
		if (childrenIds.size() > 1)
			throw new CompilationException("The " + treeRootLabel +" Concept has more than 1 child. It must have exactly one");
		if (childrenIds.isEmpty())
			throw new CompilationException("The " + treeRootLabel +" Concept has no child. It must have exactly one");
		undirectedEdges.removePairsContaining(lhsLabelId);	// remove the edge to the label
		return  childrenIds.get(0);
	}

	
	/**
	 * If alpha has no value, then take beta, else, take alpha
	 * @param alphaObj
	 * @param betaObj
	 * @return alphaObj != null ? alphaObj : betaObj
	 */
	public static Object chooseAlphaBeta(Object alphaObj, Object betaObj) {
		return alphaObj != null ? alphaObj : betaObj;
	}


	/**
	 * sanity check that the root has no relation, that every other node does, and that all nodes have a POS.<br>
	 * This method scans the root of the tree, and  therefore does not care if it has a relation or not.
	 * 
	 * @param root
	 * @throws CompilationException
	 */
	public static <I extends Info, N extends AbstractNode<I,N>> void sanityCheckRuleTree(N root) throws CompilationException {
		checkPos(root);
		sanityCheckChildren(root);
	}
	
	/**
	 * Assumption: this sanity check is performed after building trees out of the rule.<br> 
	 * Check that all edges pass through at least of the nodes 
	 * @param undirectedEdges the undirected edges of the rule <b>that remain </b>after excluding all edges that pass through tree nodes  
	 * @param nodesIds
	 * @throws CompilationException 
	 */
	public static void sanityCheckEdges(PairSet<Long> undirectedEdges, Set<Long> nodesIds) throws CompilationException {
		if (undirectedEdges != null & undirectedEdges.iterator().hasNext())
			throw new CompilationException("This rule has an edge that is not part of a tree (LHS or RHS, depending on the rule type)");
	}

	///////////////////////////////////////////// PRIVATE	//////////////////////////////////////////////////////////////////////////
	
	/**
	 * @param root
	 * @throws CompilationException 
	 */
	private static  <I extends Info, N extends AbstractNode<I,N>> void checkPos(N root) throws CompilationException {
		String pos = ExtendedInfoGetFields.getPartOfSpeech(root.getInfo(), null);
		if (pos == null || pos.isEmpty())
			throw new CompilationException("This node has no POS: " + root);
	}

	/**
	 * make sure each node under the root has a relation and a POS
	 * @param root
	 * @throws CompilationException 
	 */
	private static <I extends Info, N extends AbstractNode<I,N>> void sanityCheckChildren(N root) throws CompilationException {
		if (root.hasChildren())
			for (N child : root.getChildren())
			{
				// make sure each node under the root has a relation
				if (ExtendedInfoGetFields.getRelation(child .getInfo(), null) == null)
					throw new CompilationException("This node has no relation: " + child );
				checkPos(child );
				sanityCheckChildren(child);
			}
	}

	/**
	 * build a tree under the rootId, based on the map of nodes and the set of edges.<br>
	 * In the process, the undirectedEdges pair set is emptied of all the traversed edges.<br>
	 * <b>Assumption</b>: the root has no upwards edge
	 * 
	 * @param rootId
	 * @param nodeLabelsMap
	 * @param undirectedEdges
	 * @param compilationServices 
	 * @return
	 * @throws CompilationException
	 */
	private static <I extends Info, N extends AbstractNode<I, N>, CN extends AbstractConstructionNode<I, CN>> 
		CN buildTree(Long rootId,  Map<Long, String> nodeLabelsMap, PairSet<Long> undirectedEdges, Map<Long, CN> nodesMap, 
			RuleCompileServices<I, N, CN> compilationServices) throws CompilationException 
	{
		CN root = compilationServices.label2Node(nodeLabelsMap.get(rootId));
		nodesMap.put(rootId, root);		// build up the nodes map

		ImmutableSet<Pair<Long>> edgesFromRoot = undirectedEdges.getPairContaining(rootId);
		if (edgesFromRoot != null)
		{
			Set<Long> childrenIds = new LinkedHashSet<Long>();
			for (Pair<Long> edge : undirectedEdges.getPairContaining(rootId))
			{
				Long[] sides = edge.toSet().toArray(new Long[2]);
				Long childId = sides[0].equals(rootId) ? sides[1] : sides[0];
				childrenIds.add(childId);
			}
			// assure the recursive call doesn't see the last edges to rootId, which we just traversed (this maintains our assumption that the root has no edge upwards)
			undirectedEdges.removePairsContaining(rootId);
			for (Long childId : childrenIds)
			{
				CN child = buildTree(childId, nodeLabelsMap, undirectedEdges, nodesMap, compilationServices);
				root.addChild(child);
				child.setAntecedent(root);
			}
		}
		return root;
	}
}
