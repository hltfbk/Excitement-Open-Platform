
package eu.excitementproject.eop.transformations.utilities.parsetreeutils;
import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;

import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.transformations.alignment.AlignmentCalculator;
import eu.excitementproject.eop.transformations.alignment.AlignmentCriteria;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedInfoGetFields;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.representation.annotations.PredTruth;
import eu.excitementproject.eop.transformations.utilities.Constants;


/**
 * 
 * A collection of static methods that calculate whether two nodes are equal,
 * and the difference between trees based on the equality of their nodes and edges.
 * The functions here treat two nodes as equal, not only based on their contents, but also
 * based on the "lowest content ancestor" of the node.
 * <P>
 * See {@link AlignmentCriteria} and {@link AlignmentCalculator}, which actually
 * use this class.
 * 
 * 
 * See also {@link Equalities} and {@link TreeUtilities}, which contain similar methods,
 * but do not take into account the "content ancestor" of a node.
 * 
 * @see Equalities
 * @see TreeUtilities
 * @see AlignmentCriteria
 * @see AlignmentCalculator
 * 
 * @author Asher Stern
 * @since May 23, 2011
 *
 */
public class AdvancedEqualities
{
	public static boolean USE_ADVANCED_EQUALITIES = Constants.USE_ADVANCED_EQUALITIES;
	
	public static boolean nodesSimilarIgnoreAnnotations(ExtendedInfo textNode, ExtendedInfo hypothesisNode)
	{
		boolean ret = false;

		String textLemma = InfoGetFields.getLemma(textNode);
		String hypothesisLemma = InfoGetFields.getLemma(hypothesisNode);

		SimplerCanonicalPosTag textPos = simplerPos(InfoGetFields.getPartOfSpeechObject(textNode).getCanonicalPosTag());
		SimplerCanonicalPosTag hypothesisPos = simplerPos(InfoGetFields.getPartOfSpeechObject(hypothesisNode).getCanonicalPosTag());

		if (lemmasEqual(textLemma, hypothesisLemma))
		{
			if (textPos.equals(hypothesisPos))
			{
				ret = true;
			}
		}
		return ret;
	}
	
	public static boolean nodesAnnotationMatch(ExtendedInfo textNode, ExtendedInfo hypothesisNode)
	{
		boolean ret = false;
		if (Constants.REQUIRE_PREDICATE_TRUTH_EQUALITY)
		{
			PredTruth textPredTruth = ExtendedInfoGetFields.getPredTruthObj(textNode, null);
			PredTruth hypothesisPredTruth = ExtendedInfoGetFields.getPredTruthObj(hypothesisNode, null);
			if ( (textPredTruth!=null) && (hypothesisPredTruth!=null) )
			{
				if (predTruthMatch(textPredTruth, hypothesisPredTruth))
				{
					ret = true;
				}
			}
			else
			{
				ret = true;
			}
		}
		else
		{
			ret = true;
		}
		return ret;
	}
	
	public static boolean nodesSimilarContents(ExtendedInfo textNode, ExtendedInfo hypothesisNode)
	{
		boolean ret = false;
		if (nodesSimilarIgnoreAnnotations(textNode,hypothesisNode))
		{
			if (Constants.REQUIRE_PREDICATE_TRUTH_EQUALITY)
			{
				if (nodesAnnotationMatch(textNode,hypothesisNode))
				{
					ret = true;
				}
			}
			else
			{
				ret = true;
			}
		}
		

		return ret;

	}
	

	
	
	/**
	 * Nodes are equal if:
	 * <OL>
	 * <LI>They have the same lemma and part-of-speech</LI>
	 * <LI>They have the same lemma of "content ancestor".</LI>
	 * </OL>
	 * "content ancestor" of a node <tt>n</tt> is a node <tt>m</tt> that:
	 * <OL>
	 * <LI>is an ancestor of <tt>n</tt></LI>
	 * <LI>and is a content-word</LI>
	 * <LI>and there is no other node <tt>m'</tt> that
	 * <OL>
	 * <LI>is ancestor of <tt>n</tt></LI>
	 * <LI>and descendant of <tt>m</tt></LI>
	 * <LI>and is a content word</LI>
	 * </OL>
	 * </LI>
	 * </OL>
	 * See also {@link ContentAncestorSetter}
	 * 
	 * @param textNode
	 * @param hypothesisNode
	 * @return
	 */
	public static boolean nodesEqual(ExtendedInfo textNode, ExtendedInfo hypothesisNode)
	{
		boolean ret = false;
		if (nodesSimilarContents(textNode, hypothesisNode))
		{
			ExtendedInfo hypothesisContentAncestor = ExtendedInfoGetFields.getContentAncestor(hypothesisNode);
			ExtendedInfo textContentAncestor = ExtendedInfoGetFields.getContentAncestor(textNode);
			if ( (null==hypothesisContentAncestor) && (null==textContentAncestor) )
			{
				ret = true;
			}
			else if ( (null==hypothesisContentAncestor) || (null==textContentAncestor) )
			{
				ret = false;
			}
			else
			{
				String textAncestorLemma = InfoGetFields.getLemma(textContentAncestor);
				String hypothesisAncestorLemma = InfoGetFields.getLemma(hypothesisContentAncestor);
				if (lemmasEqual(textAncestorLemma, hypothesisAncestorLemma))
				{
					if (Constants.REQUIRE_PREDICATE_TRUTH_EQUALITY)
					{
						if (nodesAnnotationMatch(textContentAncestor,hypothesisContentAncestor))
						{
							ret = true;
						}
					}
					else
					{
						ret = true;
					}
				}
			}
			
		}
		return ret;
	}
	
	/**
	 * Returns a subset of the nodes of the hypothesis-parse-tree that have no
	 * corresponding node in the text-parse-tree.
	 * <P>
	 * A node in the hypothesis-parse-tree has a corresponding node in the
	 * text-parse-tree if there exist a node in the text-parse-tree for which
	 * the function {@link #nodesEqual(ExtendedNode, ExtendedNode)} returns true.
	 *  
	 * @param text the text-parse-tree
	 * @param hypothesis the hypothesis-parse-tree
	 * @return
	 */
	public static Set<ExtendedNode> findMissingNodes(TreeAndParentMap<ExtendedInfo, ExtendedNode> text,TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis)
	{
		Set<ExtendedNode> ret = new LinkedHashSet<ExtendedNode>();
		Set<ExtendedNode> textNodes = AbstractNodeUtils.treeToLinkedHashSet(text.getTree());
		Set<ExtendedNode> hypothesisNodes = AbstractNodeUtils.treeToLinkedHashSet(hypothesis.getTree());
		
		for (ExtendedNode hypothesisNode : hypothesisNodes)
		{
			boolean found = false;
			for (ExtendedNode textNode : textNodes)
			{
				if (nodesEqual(textNode.getInfo(),hypothesisNode.getInfo()))
				{
					found = true;
					break;
				}
			}
			if (!found)
			{
				ret.add(hypothesisNode);
			}
		}
		return ret;
	}
	
	
	/**
	 * Returns a subset of the hypothesis-parse-tree nodes that have no matching
	 * relation in the text, as explained hereafter.
	 * <BR>
	 * Let <tt>n</tt> be a node. Let <tt>p(n)</tt> be its parent. We denote by
	 * <tt>l(n)</tt> the relation (the edge-label) between <tt>p(n)</tt>
	 * and <tt>n</tt>.
	 * We denote by <tt>r(n)<tt> the triple <tt>(n,p(n),l(n))</tt>.
	 * The function returns a subset of nodes as follows:
	 * A node <tt>n</tt> in the hypothesis-parse-tree is returned by this
	 * function if there does not exist a node <tt>m</tt> in the text-parse-tree
	 * such that <tt>r(n)</tt> matches <tt>r(m)</tt>.
	 * <BR>
	 * By "match" we mean that
	 * <OL>
	 * <LI>{@link #nodesEqual(ExtendedNode, ExtendedNode)} returns true for <tt>n</tt> and <tt>m</tt></LI>
	 * <LI>{@link #nodesEqual(ExtendedNode, ExtendedNode)} returns true for <tt>P(n)</tt> and <tt>P(m)</tt></LI>
	 * <LI>{@link #edgesEqual(ExtendedNode, ExtendedNode)} returns true for <tt>l(n)</tt> and <tt>l(m)</tt></LI>
	 * </OL>
	 * 
	 * 
	 * @param text the text-parse-tree (or an intermediate parse tree derived from the text)
	 * @param hypothesis the hypothesis-parse-tree
	 * @return subset of the hypothesis-parse-tree nodes that have no matching "relation" as
	 * defined above.
	 */
	// TODO: Code duplicate with method findMatchingRelations()
	public static Set<ExtendedNode> findMissingRelations(TreeAndParentMap<ExtendedInfo, ExtendedNode> text,TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis)
	{
		Set<ExtendedNode> ret = new LinkedHashSet<ExtendedNode>();
		Set<ExtendedNode> textNodes = AbstractNodeUtils.treeToLinkedHashSet(text.getTree());
		Set<ExtendedNode> hypothesisNodes = AbstractNodeUtils.treeToLinkedHashSet(hypothesis.getTree());
		
		for (ExtendedNode hypothesisNode : hypothesisNodes)
		{
			boolean found = false;
			for (ExtendedNode textNode : textNodes)
			{
				if (nodesEqual(textNode.getInfo(),hypothesisNode.getInfo()))
				{
					ExtendedNode textParent = text.getParentMap().get(textNode);
					ExtendedNode hypothesisParent = hypothesis.getParentMap().get(hypothesisNode);
					if ( (textParent==null) && (hypothesisParent==null) )
					{
						found = true;
					}
					else if ( (textParent==null) || (hypothesisParent==null) )
					{
						// do nothing. found is false.
					}
					else
					{
						// both have a parent
						if (edgesEqual(textNode, hypothesisNode))
						{
							if (nodesEqual(textParent.getInfo(), hypothesisParent.getInfo()))
							{
								found = true;
							}
						}
					}
				}
				if (found)
					break;
			}
			if (!found)
			{
				ret.add(hypothesisNode);
			}
		}
		return ret;
	}
	
	public static ValueSetMap<ExtendedNode, ExtendedNode> findMatchingNodes(ExtendedNode textTree, ExtendedNode hypothesisTree)
	{
		ValueSetMap<ExtendedNode, ExtendedNode> ret = new SimpleValueSetMap<ExtendedNode, ExtendedNode>();
		Set<ExtendedNode> hypothesisNodes = AbstractNodeUtils.treeToLinkedHashSet(hypothesisTree);
		Set<ExtendedNode> textNodes = AbstractNodeUtils.treeToLinkedHashSet(textTree);
		
		for (ExtendedNode hypothesisNode : hypothesisNodes)
		{
			for (ExtendedNode textNode : textNodes)
			{
				if (nodesEqual(textNode.getInfo(),hypothesisNode.getInfo()))
				{
					ret.put(hypothesisNode,textNode);
				}
			}
		}
		return ret;
	}

	public static ValueSetMap<ExtendedNode, ExtendedNode> findSimilarContentNodes(ExtendedNode textTree, ExtendedNode hypothesisTree)
	{
		ValueSetMap<ExtendedNode, ExtendedNode> ret = new SimpleValueSetMap<ExtendedNode, ExtendedNode>();
		Set<ExtendedNode> hypothesisNodes = AbstractNodeUtils.treeToLinkedHashSet(hypothesisTree);
		Set<ExtendedNode> textNodes = AbstractNodeUtils.treeToLinkedHashSet(textTree);
		
		for (ExtendedNode hypothesisNode : hypothesisNodes)
		{
			for (ExtendedNode textNode : textNodes)
			{
				if (nodesSimilarContents(textNode.getInfo(),hypothesisNode.getInfo()))
				{
					ret.put(hypothesisNode,textNode);
				}
			}
		}
		return ret;
	}

	
	public static boolean treesMatch(TreeAndParentMap<ExtendedInfo, ExtendedNode> text, TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis)
	{
		return (findMissingRelations(text,hypothesis).size()==0);
	}

	/**
	 * Finds matching from the hypothesis to the text. The matching includes the parents
	 * and relation to parents.
	 * <P>
	 * TODO: code duplicate with method {@link #findMissingRelations(TreeAndParentMap, TreeAndParentMap)}
	 * 
	 * @param text
	 * @param hypothesis
	 * @return Matching from the hypothesis to the text
	 */
	public static ValueSetMap<ExtendedNode, ExtendedNode> findMatchingRelations(TreeAndParentMap<ExtendedInfo, ExtendedNode> text, TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis)
	{
		ValueSetMap<ExtendedNode, ExtendedNode> matching = new SimpleValueSetMap<ExtendedNode, ExtendedNode>();
		Set<ExtendedNode> textNodes = AbstractNodeUtils.treeToLinkedHashSet(text.getTree());
		Set<ExtendedNode> hypothesisNodes = AbstractNodeUtils.treeToLinkedHashSet(hypothesis.getTree());
		
		for (ExtendedNode hypothesisNode : hypothesisNodes)
		{
			for (ExtendedNode textNode : textNodes)
			{
				if (nodesEqual(textNode.getInfo(),hypothesisNode.getInfo()))
				{
					ExtendedNode textParent = text.getParentMap().get(textNode);
					ExtendedNode hypothesisParent = hypothesis.getParentMap().get(hypothesisNode);
					if ( (textParent==null) && (hypothesisParent==null) )
					{
						matching.put(hypothesisNode,textNode);
					}
					else if ( (textParent==null) || (hypothesisParent==null) )
					{
						// do nothing. They are not matching.
					}
					else
					{
						// both have a parent
						if (edgesEqual(textNode, hypothesisNode))
						{
							if (nodesEqual(textParent.getInfo(), hypothesisParent.getInfo()))
							{
								matching.put(hypothesisNode,textNode);
							}
						}
					}
				}
			}
		}
		return matching;
	}
	
	

	

	public static final boolean lemmasEqual(String textLemma, String hypothesisLemma)
	{
		return Equalities.lemmasEqual(textLemma, hypothesisLemma);
	}
	
	public static final boolean posEqual(PartOfSpeech textPos, PartOfSpeech hypothesisPos)
	{
		return Equalities.posEqual(textPos, hypothesisPos);
	}

	
	
	public static boolean edgesEqual(ExtendedNode textNode, ExtendedNode hypothesisNode)
	{
		String textRelation = InfoGetFields.getRelation(textNode.getInfo());
		String hypothesisRelation = InfoGetFields.getRelation(hypothesisNode.getInfo());
		return textRelation.equals(hypothesisRelation);
	}
	
	private static boolean predTruthMatch(PredTruth textPredTruth, PredTruth hypothesisPredTruth)
	{
		boolean ret = true;
		if (!Constants.REQUIRE_PREDICATE_TRUTH_MATCH_FOR_UNKNOWN)
		{
			if (
					(textPredTruth.equals(PredTruth.N) && hypothesisPredTruth.equals(PredTruth.P))
					||
					(textPredTruth.equals(PredTruth.P) && hypothesisPredTruth.equals(PredTruth.N))
					)
				ret = false;
			else
				ret = true;
		}
		else
		{
			if ( (MEANINGFUL_PRED_TRUTH_VALUES.contains(textPredTruth)) && (MEANINGFUL_PRED_TRUTH_VALUES.contains(hypothesisPredTruth)) )
			{
				if (!textPredTruth.equals(hypothesisPredTruth) )
				{
					ret = false;
				}
				else
				{
					ret = true;
				}
			}
			else
			{
				ret = true;
			}
		}
		
		return ret;
	}

	private static Set<PredTruth> MEANINGFUL_PRED_TRUTH_VALUES = Utils.arrayToCollection(new PredTruth[]{PredTruth.N,PredTruth.P,PredTruth.U}, new LinkedHashSet<PredTruth>());
}
