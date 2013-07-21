package eu.excitementproject.eop.common.representation.parse.tree;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;

/**
 * Computes the least common ancestor for each two nodes in the given tree.
 * Usage:
 * <ol>
 * <li>constructor</li>
 * <li>{@link #compute()} - does most of the computations, and saves them in a matrix.</li>
 * <li>call {@link #getLeastCommonAncestorOf(AbstractNode, AbstractNode)} as needed - it uses the pre-computed matrix and runs in constant time.</li>
 * </ol>
 * 
 * Note that the complexity of {@link #compute()} is ~ O(n^3) where n is the number of nodes in the tree.
 * The complexity of {@link #getLeastCommonAncestorOf(AbstractNode, AbstractNode)} is O(1).
 * 
 * @param <T> The information type of a node (e.g. {@link Info}).
 * @param <S> The type of the nodes themselves (e.g. {@link BasicNode}).
 * 
 * @author Asher Stern
 *
 */
public class LeastCommonAncestor<T,S extends AbstractNode<T,S>>
{
	///////////////// PUBLIC PART ///////////////////////////
	
	//////////////// PUBLIC NESTED CLASSES //////////////////
	
	@SuppressWarnings("serial")
	public static class LeastCommonAncestorException extends Exception
	{
		public LeastCommonAncestorException(String message, Throwable cause) {
			super(message, cause);
		}

		public LeastCommonAncestorException(String message) {
			super(message);
		}
	}
	

	///////////////// PUBLIC CONSTRUCTOR & METHODS ////////////////////

	
	public LeastCommonAncestor(S root)
	{
		this.root = root;
	}
	
	/**
	 * <p>Call this method to make the pre-computation stage.
	 * <p>Running time: At least Omega(n), where n is the number of nodes in the tree.
	 * <p>Then call {@link #getLeastCommonAncestorOf(AbstractNode, AbstractNode)}.
	 * @throws LeastCommonAncestorException
	 */
	public void compute() throws LeastCommonAncestorException
	{
		init();
		buildMapToDescendants(root);
		compute(AbstractNodeUtils.getLeaves(root));
		releaseMemory();
		computed = true;
	}
	

	
	/**
	 * <p>Returns the least (lowest) common ancestor of the two given nodes.
	 * Call this method only after calling {@link #compute()} method.
	 * <p>Running time: O(1).
	 * @param node1 a node in the tree.
	 * @param node2 a node in the tree.
	 * @return the least (lowest) common ancestor of the two
	 * given nodes.
	 * 
	 * @throws LeastCommonAncestorException
	 */
	public S getLeastCommonAncestorOf(S node1, S node2) throws LeastCommonAncestorException
	{
		if (!computed) throw new LeastCommonAncestorException("Caller\'s bug. The method compute() was not called yet.");
		
		int node1Index;
		int node2Index;
		if (mapNodeToIndex.rightContains(node1))
			node1Index = mapNodeToIndex.rightGet(node1);
		else throw new LeastCommonAncestorException("Caller\'s bug. node1 does not exist.");
		if (mapNodeToIndex.rightContains(node2))
			node2Index = mapNodeToIndex.rightGet(node2);
		else throw new LeastCommonAncestorException("Caller\'s bug. node2 does not exist.");
		
		return lcaMatrix[node1Index][node2Index];
	}
	
	
	////////////////// PRIVATE & PROTECTED PART ////////////////////////
	
	

	/////////////////////////////////////////////////////
	
	
	//////////////// PROTECTED AND PRIVATE METHODS /////////////////
	
	protected void buildMapToDescendants(S root)
	{
		if (!AbstractNodeUtils.isLeaf(root))
		{
			for (S child : root.getChildren())
			{
				buildMapToDescendants(child);
			}
		}
		Set<S> set = new LinkedHashSet<S>();
		if (!AbstractNodeUtils.isLeaf(root))
		{
			for (S child : root.getChildren())
			{
				set.addAll(mapToDescendants.get(child));
			}
		}
		set.add(root);
		mapToDescendants.put(root,set);
	}
	
	protected void compute(Set<S> currentLevel) throws LeastCommonAncestorException
	{
		Set<S> nextLevel = new LinkedHashSet<S>(); 
		
		for (S node : currentLevel)
		{
			if (AbstractNodeUtils.isLeaf(node)) ;
			else
			{
				for (S child1 : node.getChildren())
				{
					for (S child2 : node.getChildren())
					{
						if (child1==child2) ;
						else
						{
							Set<S> child1Descendants = mapToDescendants.get(child1);
							Set<S> child2Descendants = mapToDescendants.get(child2);
							for (S descendant1 : child1Descendants)
							{
								for (S descendant2 : child2Descendants)
								{
									setLeastCommontAncestorFor(descendant1,descendant2,node);
								}
							}
							
						}
					}
				}
				
				for (S descendant : mapToDescendants.get(node))
				{
					setLeastCommontAncestorFor(node, descendant, node);
				}
				
			}
			setLeastCommontAncestorFor(node,node,node);
			if (parentMap.containsKey(node))
				nextLevel.add(parentMap.get(node));
			
			
		}
		if (nextLevel.size()>0)
			compute(nextLevel);
		
	}
	
	protected void setLeastCommontAncestorFor(S node1, S node2, S ancestor) throws LeastCommonAncestorException
	{
		int node1Index;
		int node2Index;
		if (mapNodeToIndex.rightContains(node1))
			node1Index = mapNodeToIndex.rightGet(node1);
		else throw new LeastCommonAncestorException("Caller\'s bug. node1 does not exist.");
		if (mapNodeToIndex.rightContains(node2))
			node2Index = mapNodeToIndex.rightGet(node2);
		else throw new LeastCommonAncestorException("Caller\'s bug. node2 does not exist.");
		
		lcaMatrix[node1Index][node2Index] = ancestor;
		lcaMatrix[node2Index][node1Index] = ancestor;
		
	}
	
	@SuppressWarnings("unchecked")
	protected void init()
	{
		mapNodeToIndex = new SimpleBidirectionalMap<Integer, S>();
		Set<S> setNodes = AbstractNodeUtils.treeToLinkedHashSet(root);
		int index = 0;
		for (S node : setNodes)
		{
			mapNodeToIndex.put(index, node);
			++index;
		}
		lcaMatrix = (S[][]) new AbstractNode[setNodes.size()][setNodes.size()];
		parentMap = AbstractNodeUtils.parentMap(root);
	}
	
	protected void releaseMemory()
	{
		mapToDescendants = null;
		parentMap = null;
	}
	
	
	
	
	
	protected S root;
	protected Map<S,Set<S>> mapToDescendants = new LinkedHashMap<S, Set<S>>();
	protected BidirectionalMap<Integer, S> mapNodeToIndex;
	protected S[][] lcaMatrix;
	protected Map<S,S> parentMap;
	protected boolean computed = false;
}
