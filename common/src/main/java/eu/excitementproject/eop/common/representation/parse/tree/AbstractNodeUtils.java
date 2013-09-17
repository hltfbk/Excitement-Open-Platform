package eu.excitementproject.eop.common.representation.parse.tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.NodeString;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.SimpleNodeString;


/**
 * A collection of utilities for parse trees.
 * 
 * @author Asher Stern
 *
 */
public class AbstractNodeUtils
{
	
	//////////////////// PUBLIC PART ///////////////////////////
	
	
	
	/**
	 * Detects whether the given node is a root of a tree. I.e. there are no cycles in
	 * the sub-tree rooted by <code>root</code>
	 * @param root root of sub-tree.
	 * @return <code>true</code> if there are no cycles.
	 */
	public static boolean isTree(AbstractNode<?,?> root)
	{
		return isTree(root,new HashSet<AbstractNode<?,?>>());
		
	}
	
	/**
	 * Detects whether the node is a leaf, i.e. has no children.
	 * @param node
	 * @return <code>true</code> if the node is a leaf. 
	 */
	public static boolean isLeaf(AbstractNode<?,?> node)
	{
		boolean ret = false;
		if (null==node.getChildren())
			ret = true;
		else
		{
			if (node.getChildren().size()==0)
				ret = true;
		}
		return ret;
	}
	
	public static <T,S extends AbstractNode<T,S>> int treeSize(S tree)
	{
		int size = 0;
		Iterator<S> iterator = new TreeIterator<>(tree);
		while (iterator.hasNext())
		{
			iterator.next();
			++size;
		}
		return size;
	}
	
	/**
	 * Gets a parse tree, and returns a set that contains all the nodes in that tree.
	 * <BR>
	 * <B>It is strongly recommended not to use this method, since it returns
	 * a HashSet, which is non-deterministic.</B>
	 * <BR>
	 * However, this method must not be deleted or changed, since legacy binaries,
	 * which cannot be changed (perhaps) use it, so changing this method would result it
	 * a runtime error.
	 * <P>
	 * The difference between this function and {@link #treeToSet(AbstractNode)} is the
	 * return value.<BR>
	 * Use the function that fits your code.
	 * @param <T> the information type of a node.
	 * @param root the root of the subtree.
	 * @return a set that contains all the nodes in that tree.
	 */
	public static <T> HashSet<AbstractNode<T,?>> weakTreeToSet(AbstractNode<T,?> root)
	{
		return weakTreeToSet(root,new LinkedHashSet<AbstractNode<T,?>>());
	}

	/**
	 * Gets a parse tree, and returns a set that contains all the nodes in that tree.
	 * <BR>
	 * <B>It is strongly recommended not to use this method, since it returns
	 * a HashSet, which is non-deterministic. Use {@link #treeToLinkedHashSet(AbstractNode)} instead.</B>
	 * <BR>
	 * However, this method must not be deleted or changed, since legacy binaries,
	 * which cannot be changed use it, so changing this method would result it
	 * a runtime error.
	 * <P>
	 * The difference between this function and {@link #weakTreeToSet(AbstractNode)} is the
	 * return value.<BR>
	 * Use the function that fits your code.
	 * @param <T> the information type of a node.
	 * @param <S> the type of the node (and all its descendants) itself.
	 * @param root the root of the subtree.
	 * @return a set that contains all the nodes in that tree.
	 */
	public static <T,S extends AbstractNode<T,S>> HashSet<S> treeToSet(S root)
	{
		return treeToLinkedHashSet(root); // fortunately, LinkedHashSet extends HashSet.
		//return treeToSet(root,new HashSet<S>());
	}

	/**
	 * Gets a parse tree and returns its nodes as a {@link LinkedHashSet}.
	 * @param root
	 * @return
	 */
	public static <T,S extends AbstractNode<T,S>> LinkedHashSet<S> treeToLinkedHashSet(S root)
	{
		LinkedHashSet<S> set = new LinkedHashSet<S>();
		for (S node : TreeIterator.iterableTree(root))
		{
			set.add(node);
		}
		return set;
	}

	
	/**
	 * Gets a parse tree, and returns a topologically ordered list that contains all the nodes in that tree (where parents appears before their children)
	 * <P>
	 * @param <T> the information type of a node.
	 * @param <S> the type of the node (and all its descendants) itself.
	 * @param root the root of the subtree.
	 * @return a topologically ordered list that contains all the nodes in that tree.
	 */
	public static <T,S extends AbstractNode<T,S>> List<S> treeToList(S root)
	{
		return treeToList(root,new ArrayList<S>());
	}
	
	/**
	 * Returns a mapping from each node to its parent. Note that the root is not mapped.
	 * @param <T> the information type of a node.
	 * @param <S> the type of the node (and all its descendants) itself.
	 * @param root the root of the tree.
	 * @return a mapping from each node to its parent.
	 */
	public static <T,S extends AbstractNode<T,S>> Map<S,S> parentMap(S root)
	{
		LinkedHashMap<S,S> ret = new LinkedHashMap<S, S>();
		HashSet<S> alreadyVisited = new HashSet<S>();
		parentMap(root,ret,alreadyVisited);
		return ret;
		
	}
	
	
	/**
	 * Returns a mapping from each node to its parent. Note that the root is not mapped.
	 * The difference between this function and {@link #parentMap(AbstractNode)} is the
	 * return value. Use the function that fits your needs.
	 * @param <T> the information type of a node.
	 * @param root the root of the tree.
	 * @return a mapping from each node to its parent.
	 */
	public static <T> Map<AbstractNode<T,?>,AbstractNode<T,?>> weakParentMap(AbstractNode<T,?> root)
	{
		LinkedHashMap<AbstractNode<T,?>,AbstractNode<T,?>> ret = new LinkedHashMap<AbstractNode<T,?>, AbstractNode<T,?>>();
		HashSet<AbstractNode<T,?>> alreadyVisited = new HashSet<AbstractNode<T,?>>();
		weakParentMap(root,ret,alreadyVisited);
		return ret;
		
	}
	
	
	/**
	 * Returns a set with all the leaves in the tree.
	 * @param <T> the information type of a node.
	 * @param <S> the type of the node (and all its descendants) itself.
	 * @param root the root of the tree.
	 * @return a set with all the leaves in the tree.
	 */
	public static <T,S extends AbstractNode<T,S>> Set<S> getLeaves(S root)
	{
		Set<S> ret = new LinkedHashSet<S>();
		getLeaves(root, ret,new LinkedHashSet<S>() );
		return ret;
		
	}

	/**
	 * The difference between this method to {@link #getLeaves(AbstractNode)} is
	 * the return value.
	 * Use the method that fits your needs.
	 * @param <T>
	 * @param root
	 * @return
	 */
	public static <T> Set<AbstractNode<T,?>> weakGetLeaves(AbstractNode<T,?> root)
	{
		Set<AbstractNode<T,?>> ret = new HashSet<AbstractNode<T,?>>();
		weakGetLeaves(root, ret,new HashSet<AbstractNode<T,?>>() );
		return ret;
		
	}

	
	/**
	 * Returns the antecedent of the antecedent of the antecedent...
	 * until a node that has no antecedent has been reached.
	 * @param <T>
	 * @param <S>
	 * @param node a node in a tree.
	 * @return
	 */
	public static <T,S extends AbstractNode<T,S>> S getDeepAntecedentOf(S node)
	{
		S ret = node;
		while (ret.getAntecedent()!=null)
		{
			ret = ret.getAntecedent();
		}
		return ret;
	}

	/**
	 * Returns the antecedent of the antecedent of the antecedent...
	 * until a node that has no antecedent has been reached.
	 * @param <T>
	 * @param node a node in a tree.
	 * @return
	 */
	public static <T> AbstractNode<T,?> weakGetDeepAntecedentOf(AbstractNode<T,?> node)
	{
		AbstractNode<T,?> ret = node;
		while (ret.getAntecedent()!=null)
		{
			ret = ret.getAntecedent();
		}
		return ret;
	}
	
	/**
	 * Copies the entire tree. The copy may be of an other type
	 * than the original tree. But the information type
	 * must be equal.
	 * 
	 * @param <T>
	 * @param <S>
	 * @param root the root of the original tree.
	 * @param nodeConstructor an instance of {@link AbstractNodeConstructor} that will be
	 * used to create the nodes of the copied (new) tree.
	 * @return the root of the new tree, which is a copy
	 * of the original.
	 */
	public static <T,S extends AbstractNode<T, S>> S copyTree(AbstractNode<T,?> root, AbstractNodeConstructor<T, S> nodeConstructor)
	{
		TreeCopier<T,S> treeCopier = new TreeCopier<T, S>(root, nodeConstructor);
		treeCopier.copy();
		return treeCopier.getCopyTreeRoot();
	}

	/**
	 * Copies entire tree.
	 * It is equal to {@link #copyTree(AbstractNode, AbstractNodeConstructor)}, but it
	 * returns the mapping from the original tree's nodes to the current one.
	 * @param <T>
	 * @param <S>
	 * @param root The root of the original tree.
	 * @param nodeConstructor an instance of {@link AbstractNodeConstructor} that will be
	 * used to create the nodes of the copied (new) tree.
	 * @return A map from the original tree to the new tree.
	 */
	public static <T,S extends AbstractNode<T, S>> BidirectionalMap<AbstractNode<T,?>, S> copyTreeReturnMap(AbstractNode<T,?> root, AbstractNodeConstructor<T, S> nodeConstructor)
	{
		TreeCopier<T,S> treeCopier = new TreeCopier<T, S>(root, nodeConstructor);
		treeCopier.copy();
		return treeCopier.getMapOrigToCopy();
	}
	
	
	/**
	 * Copies entire tree. Use this function when the two trees (the original
	 * and the copied) have the same type (S)).
	 * 
	 * 
	 * @param <T> the information type on a node (e.g. {@link Info})
	 * @param <S> the type of the original tree's nodes and the copied trees nodes (e.g. {@link BasicNode})
	 * @param originalTree the original tree
	 * @param nodeConstructor an object that constructs new nodes.
	 * @return A {@link BidirectionalMap} that maps the original tree's nodes to the copied
	 * tree's nodes.
	 */
	public static <T,S extends AbstractNode<T, S>> BidirectionalMap<S, S> strictTypeCopyTree(S originalTree, AbstractNodeConstructor<T, S> nodeConstructor)
	{
		StrictTreeCopier<T, S> copier = new StrictTreeCopier<T, S>(originalTree, nodeConstructor);
		copier.copy();
		return copier.getMapOrigToCopy();
	}
	
	
	

	/**
	 * Returns a depth map, that maps for each node its depth in the parse tree.
	 * The root has depth 0. The root's direct children have depth 1. Every node has
	 * depth of <tt>[its parent's depth]+1</tt>
	 * @param <T>
	 * @param <S>
	 * @param root
	 * @return
	 */
	public static <T,S extends AbstractNode<T, S>> Map<S, Integer> depthMap(S root)
	{
		
		Map<S, Integer> ret = new LinkedHashMap<S, Integer>();
		if (root != null)
		{
			depthMap(root,ret,0);
		}
		return ret;
	}
	
	/**
	 * @return true if node1 is a strict descendant of node2, in the given parent-map 
	 */
	public static <T,S extends AbstractNode<T, S>> boolean isDescendant(S node1, S node2, Map<S,S> parentMap)
	{
		for (;;) {
			node1 = parentMap.get(node1);
			if (node1==node2)
				return true;    // found node2 in the ancestry line of node1
			if (node1==null) 
				return false;    // did not find node2 in the ancestry line of node1
		}
	}
	

	/**
	 * Returns an multi-line indented string representation of the tree.
	 * @param node
	 * @param str used for printing each node
	 * @param indent indentation string for each level
	 * @return
	 */
	public static <I extends Info> String getIndentedString(AbstractNode<I,?> node, NodeString<I> str, String indent) {
		return getIndentedStringSubtree(node, str, indent, "").toString().trim();
	}
	
	/**
	 * Returns an multi-line indented string representation of the tree.
	 * Uses {@link SimpleNodeString) for printing each node.
	 * @param node
	 * @param indent indentation string for each level
	 * @param prefix indentation string for top level
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <I extends Info> String getIndentedString(AbstractNode<I,?> node, String indent, String prefix) {
		return getIndentedStringSubtree(node, (NodeString<I>) new SimpleNodeString(), indent, prefix).toString().trim();
	}
	
	/**
	 * Returns an multi-line indented string representation of the tree.
	 * Uses "  " for indentation.
	 * @param node
	 * @param str used for printing each node
	 * @return
	 */
	public static <I extends Info> String getIndentedString(AbstractNode<I,?> node, NodeString<I> str) {
		return getIndentedString(node, str, "  ");
	}
	
	/**
	 * Returns an multi-line indented string representation of the tree.
	 * Uses "  " for indentation. Uses {@link SimpleNodeString) for printing each node.
	 * @param node
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <I extends Info> String getIndentedString(AbstractNode<I,?> node) {
		return getIndentedString(node, (NodeString<I>) new SimpleNodeString());
	}
	
	
	/////////////////////// PROTECTED & PRIVATE PART ///////////////////////////////

	
	/**
	 * Copies an entire tree.
	 * The tricky part here is to copy the antecedent information as well.
	 * @author Asher Stern
	 * 
	 *
	 * @param <T>
	 * @param <S>
	 */
	private static class TreeCopier<T,S extends AbstractNode<T, S>>
	{
		public TreeCopier(AbstractNode<T, ?> originalRoot,
				AbstractNodeConstructor<T, S> nodeConstructor)
		{
			super();
			this.originalRoot = originalRoot;
			this.nodeConstructor = nodeConstructor;
		}

		public void copy()
		{
			copyRoot = copy(originalRoot);
			copyAntecedentInformation();
		}
		
		private S copy(AbstractNode<T,?> root)
		{
			if (null==root)
				return null;
			if (mapOrigToCopy.leftContains(root))
				return null;
			
			S ret = nodeConstructor.newNode(root.getInfo());
			if (root.getChildren()!=null)
			{
				for (AbstractNode<T,?> child : root.getChildren())
				{
					S copyChild = copy(child);
					if (copyChild!=null)
						ret.addChild(copyChild);
				}
			}
			mapOrigToCopy.put(root, ret);
			return ret;
		}
		
		private void copyAntecedentInformation()
		{
			for (AbstractNode<T,?> node : mapOrigToCopy.leftSet())
			{
				if (node.getAntecedent()!=null)
				{
					S copyNode = mapOrigToCopy.leftGet(node);
					AbstractNode<T, ?> antecedent = node.getAntecedent();
					S copyAntecedent = mapOrigToCopy.leftGet(antecedent);
					copyNode.setAntecedent(copyAntecedent);
				}
			}
		}
		
		public S getCopyTreeRoot()
		{
			return this.copyRoot;
		}
		
		public BidirectionalMap<AbstractNode<T, ?>, S> getMapOrigToCopy()
		{
			return mapOrigToCopy;
		}





		private AbstractNode<T,?> originalRoot;
		private S copyRoot = null;
		private AbstractNodeConstructor<T, S> nodeConstructor;
		private BidirectionalMap<AbstractNode<T,?>, S> mapOrigToCopy = new SimpleBidirectionalMap<AbstractNode<T,?>, S>();
	}
	// end of class TreeCopier
	
	
	private static class StrictTreeCopier<T,S extends AbstractNode<T,S>>
	{
		public StrictTreeCopier(S originalTree, AbstractNodeConstructor<T, S> nodeConstructor)
		{
			this.originalTree = originalTree;
			this.nodeConstructor = nodeConstructor;
		}
		
		public void copy()
		{
			mapOrigToCopy = new SimpleBidirectionalMap<S, S>();
			copySubTree(originalTree);
			updateAntecedent();
		}
		
		public BidirectionalMap<S, S> getMapOrigToCopy()
		{
			return this.mapOrigToCopy;
		}
		
		private S copySubTree(S subTree)
		{
			S ret = null;
			if (subTree.getInfo()!=null)
				ret = nodeConstructor.newNode(subTree.getInfo());
			else
				ret = nodeConstructor.newEmptyNode();
			
			if (subTree.getChildren()!=null)
			{
				for (S child : subTree.getChildren())
				{
					ret.addChild(copySubTree(child));
				}
			}
			mapOrigToCopy.put(subTree, ret);
			return ret;
		}
		
		private void updateAntecedent()
		{
			for (S originalNode : mapOrigToCopy.leftSet())
			{
				if (originalNode.getAntecedent()!=null)
				{
					S copiedNode = mapOrigToCopy.leftGet(originalNode);
					copiedNode.setAntecedent(mapOrigToCopy.leftGet(originalNode.getAntecedent()));
				}
			}
		}
		
		
		private S originalTree;
		private BidirectionalMap<S, S> mapOrigToCopy;
		private AbstractNodeConstructor<T, S> nodeConstructor;
	}
	// end of class StrictTreeCopier

	
	protected static <T,S extends AbstractNode<T,S>> void getLeaves(S root,Set<S> setLeaves, Set<S> alreadyVisited)
	{
		if (alreadyVisited.contains(root)) ;
		else
		{
			alreadyVisited.add(root);
			if (isLeaf(root))
				setLeaves.add(root);
			else
			{
				for (S child : root.getChildren())
				{
					getLeaves(child, setLeaves, alreadyVisited);
				}
			}
		}
		
	}

	
	protected static <T> void weakGetLeaves(AbstractNode<T,?> root,Set<AbstractNode<T,?>> setLeaves, Set<AbstractNode<T,?>> alreadyVisited)
	{
		if (alreadyVisited.contains(root)) ;
		else
		{
			alreadyVisited.add(root);
			if (isLeaf(root))
				setLeaves.add(root);
			else
			{
				for (AbstractNode<T,?> child : root.getChildren())
				{
					weakGetLeaves(child, setLeaves, alreadyVisited);
				}
			}
		}
		
	}

	
	protected static <T,S extends AbstractNode<T,S>> void parentMap(S root,Map<S,S> map,HashSet<S> alreadyVisited)
	{
		if (alreadyVisited.contains(root)) ;
		else
		{
			alreadyVisited.add(root);
			if (isLeaf(root)) ;
			else
			{
				for (S child : root.getChildren())
				{
					map.put(child, root);
					parentMap(child, map, alreadyVisited);
				}
			}
		}

		
	}

	protected static <T> void weakParentMap(AbstractNode<T,?> root,Map<AbstractNode<T,?>,AbstractNode<T,?>> map,HashSet<AbstractNode<T,?>> alreadyVisited)
	{
		if (alreadyVisited.contains(root)) ;
		else
		{
			alreadyVisited.add(root);
			if (isLeaf(root)) ;
			else
			{
				for (AbstractNode<T,?> child : root.getChildren())
				{
					map.put(child, root);
					weakParentMap(child, map, alreadyVisited);
				}
			}
		}

		
	}

	
	protected static <T,S extends AbstractNode<T,S>> HashSet<S> treeToSet(S root,HashSet<S> set)
	{
		if (set.contains(root)) ;
		else
		{
			set.add(root);
			if (isLeaf(root)) ;
			else
			{
				for (S child : root.getChildren())
				{
					treeToSet(child,set);
				}
			}
		}
		return set;
	}

	
	protected static <T,S extends AbstractNode<T,S>> List<S> treeToList(S root,ArrayList<S> arrayList)
	{
		arrayList.add(root);
		if (isLeaf(root)) ;
		else
		{
			for (S child : root.getChildren())
			{
				treeToList(child,arrayList);
			}
		}
		return arrayList;
	}

	protected static <T> HashSet<AbstractNode<T,?>> weakTreeToSet(AbstractNode<T,?> root,HashSet<AbstractNode<T,?>> set)
	{
		if (set.contains(root)) ;
		else
		{
			set.add(root);
			if (isLeaf(root)) ;
			else
			{
				for (AbstractNode<T,?> child : root.getChildren())
				{
					weakTreeToSet(child,set);
				}
			}
		}
		return set;
	}

	
	protected static boolean isTree(AbstractNode<?,?> root,HashSet<AbstractNode<?,?>> alreadyVisitedNodes)
	{
		if (null==root)
			return true;
		else
		{
			if (alreadyVisitedNodes.contains(root))
				return false;
			else
			{
				alreadyVisitedNodes.add(root);
				boolean ret = true;
				if (root.getChildren()!=null)
				{
					for (AbstractNode<?,?> child : root.getChildren())
					{
						if (!isTree(child,alreadyVisitedNodes))
							ret = false;
					}
				}
				return ret;
			}
		}
		
	}
	
	
	protected static <T,S extends AbstractNode<T, S>> Map<S,Integer> depthMap(S node,Map<S,Integer> map,int currentDepth)
	{
		if (!map.containsKey(node))
		{
			map.put(node, currentDepth);
			if (node.getChildren()!=null)
			{
				for (S child : node.getChildren())
				{
					depthMap(child,map,currentDepth+1);
				}
			}
		}
		return map;
	}
	
	protected static <I extends Info> StringBuffer getIndentedStringSubtree(AbstractNode<I,?> subtree, NodeString<I> str, String indent, String prefix) {
		final String NULL_TREE_STR = "(null)";
		StringBuffer result = new StringBuffer();
		
		if (subtree == null) {
			result.append(NULL_TREE_STR);
		}
		else {
			str.set(subtree);
			result.append(prefix);
			result.append(str.getStringRepresentation());
			result.append("\r\n");
			
			if (subtree.getChildren() != null) {
				for (AbstractNode<I,?> child : subtree.getChildren()) {
					result.append(getIndentedStringSubtree(child, str, indent, prefix+indent));
				}
			}
		}
		
		return result;

	}
	
}
