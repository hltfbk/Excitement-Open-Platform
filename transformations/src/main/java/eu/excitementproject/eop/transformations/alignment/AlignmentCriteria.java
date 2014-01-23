package eu.excitementproject.eop.transformations.alignment;

import eu.excitementproject.eop.common.codeannotations.ThreadSafe;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
//import eu.excitementproject.eop.transformations.rteflow.systems.TESystemEnvironment;

/**
 * A criteria by which it is said that a node or an edge in the text-parse-tree is equal
 * to a node or an edge in the hypothesis-parse-tree.
 * 
 * This criteria is used to determine whether the hypothesis is embedded in
 * the text. It is used during the proof construction to guide it to the goal that
 * all of the nodes and edges in the hypothesis parse-tree are embedded in the
 * text-parse-tree.
 * 
 * <P>
 * Note that currently the requirement is that all "triples" of the hypothesis
 * parse-tree will be embedded in the text-parse-tree (i.e. in the parse-tree that
 * is generated from the original text parse tree by applying the sequence of
 * transformations).
 * A "triple" is: parent, child and the relation between them.
 * <P>
 * {@link AlignmentCriteria} is thread safe and is part of {@link TESystemEnvironment}.
 * 
 * @author Asher Stern
 * @since May 28, 2012
 *
 * @param <T>
 * @param <S>
 */
@ThreadSafe
public interface AlignmentCriteria<T, S extends AbstractNode<T, S>>
{
	/**
	 * Tests whether the two given node-informations are similar. Similar means that if (at least theoretically)
	 * the nodes were at some locations at some parse trees, then they could be also aligned
	 * Practically, this means that (perhaps) moving the text node to another location might make
	 * it aligned.
	 * as in {@link #nodesAlign(TreeAndParentMap, TreeAndParentMap, AbstractNode, AbstractNode)}.
	 * @param textTree
	 * @param hypothesisTree
	 * @param textNode
	 * @param hypothesisNode
	 * @return <tt>true</tt> if similar.
	 */
	public boolean nodesSimilar(TreeAndParentMap<T, S> textTree, TreeAndParentMap<T, S> hypothesisTree, S textNode, S hypothesisNode);
	
	/**
	 * Tests whether the two given nodes are aligned, i.e., equal.
	 * @param textTree
	 * @param hypothesisTree
	 * @param textNode
	 * @param hypothesisNode
	 * @return
	 */
	public boolean nodesAligned(TreeAndParentMap<T, S> textTree, TreeAndParentMap<T, S> hypothesisTree, S textNode, S hypothesisNode);
	
	/**
	 * Tests whether the two given edges are aligned, i.e., equal.
	 * <code>textEdge</code> and <code>hypothesisEdge</code> are actually nodes, and the edges are the
	 * edges that connect them to their parents.
	 * @param textTree
	 * @param hypothesisTree
	 * @param textEdge
	 * @param hypothesisEdge
	 * @return
	 */
	public boolean edgesAligned(TreeAndParentMap<T, S> textTree, TreeAndParentMap<T, S> hypothesisTree, S textEdge, S hypothesisEdge);

	/**
	 * Tests whether the two given triples are aligned, i.e., equal.
	 * <code>textTriple</code> and <code>hypothesisTriple</code> are actually nodes, and the triples are these nodes,
	 * their parents, and the edges between.
	 * @param textTree
	 * @param hypothesisTree
	 * @param textTriple
	 * @param hypothesisTriple
	 * @return
	 */
	public boolean triplesAligned(TreeAndParentMap<T, S> textTree, TreeAndParentMap<T, S> hypothesisTree, S textTriple, S hypothesisTriple);
}
