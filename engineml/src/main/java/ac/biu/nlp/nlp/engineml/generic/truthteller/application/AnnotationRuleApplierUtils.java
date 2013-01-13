/**
 * 
 */
package ac.biu.nlp.nlp.engineml.generic.truthteller.application;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ac.biu.nlp.nlp.engineml.datastructures.FlippedBidirectionalMap;
import ac.biu.nlp.nlp.engineml.generic.truthteller.AnnotatorException;
import ac.biu.nlp.nlp.engineml.generic.truthteller.application.merge.AnnotationsMerger;
import ac.biu.nlp.nlp.engineml.generic.truthteller.representation.AnnotationRule;
import ac.biu.nlp.nlp.engineml.generic.truthteller.representation.BasicRuleAnnotations;
import ac.biu.nlp.nlp.engineml.generic.truthteller.services.AnnotationsWithWildcardsMatchCriteria;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.representation.ExtendedConstructionNode;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.match.AllEmbeddedMatcher;
import eu.excitementproject.eop.common.representation.parse.tree.match.MatcherException;

/**
 * This class holds several static methods used by {@link DefaultAnnotationRuleApplier}
 * 
 * @author Amnon Lotan
 *
 * @since Jun 20, 2012
 */
public class AnnotationRuleApplierUtils {

	/**
	 * Returns true iff the rule application changed the tree
	 * @param textTreeAndParentMap
	 * @param matchOfLhsToTree
	 * @param rule
	 * @param map 
	 * @return
	 * @throws AnnotatorException 
	 * @throws OperationException 
	 */
	public static boolean applyRuleToMatchedTree(	TreeAndParentMap<ExtendedInfo, ExtendedConstructionNode> textTreeAndParentMap,
			BidirectionalMap<ExtendedNode, ExtendedConstructionNode> matchOfLhsToTree, 
			Map<ExtendedNode, BasicRuleAnnotations> map, AnnotationsMerger annotationsMerger) throws AnnotatorException, OperationException 
	{
		DefaultAnnotationRuleApplicationOperation operation;
		operation = new DefaultAnnotationRuleApplicationOperation( textTreeAndParentMap, matchOfLhsToTree, map, annotationsMerger);
		operation.annotateTheTree();
		boolean newTreeIsTheSame = !operation.treeWasAltered();
		return newTreeIsTheSame;
	}

	/**
	 * Return first match between the tree and the rule. Comparing nodes and edges with {@link AnnotationsWithWildcardsMatchCriteria}. Recall that with 
	 * this criteria, null annotation values are regarded as "" empty strings. 
	 * 
	 * @param tree
	 * @param rule
	 * @return
	 * @throws MatcherException 
	 */
	public static Set<BidirectionalMap<ExtendedNode, ExtendedConstructionNode>> getMatchesOfLhsToTree(ExtendedConstructionNode tree, 
			AnnotationRule<ExtendedNode, BasicRuleAnnotations> rule) 
		throws MatcherException
	{
		AllEmbeddedMatcher<ExtendedInfo, ExtendedInfo, ExtendedConstructionNode, ExtendedNode> matcher =
			new AllEmbeddedMatcher<ExtendedInfo, ExtendedInfo, ExtendedConstructionNode, ExtendedNode>(MATCH_CRITERIA);
		matcher.setTrees(tree, rule.getLeftHandSide());
		matcher.findMatches();
		Set<BidirectionalMap<ExtendedConstructionNode, ExtendedNode>> reversedMatches = matcher.getMatches();
		
		Set<BidirectionalMap<ExtendedNode, ExtendedConstructionNode>> matchesOfLhsToTree = 
				new HashSet<BidirectionalMap<ExtendedNode,ExtendedConstructionNode>>(reversedMatches.size());
		for (BidirectionalMap<ExtendedConstructionNode, ExtendedNode>  match : reversedMatches)
			matchesOfLhsToTree.add(new FlippedBidirectionalMap<ExtendedNode, ExtendedConstructionNode>(match));

		return matchesOfLhsToTree;
	}

	private static final AnnotationsWithWildcardsMatchCriteria<ExtendedInfo, ExtendedInfo, ExtendedConstructionNode, ExtendedNode> MATCH_CRITERIA = 
			new AnnotationsWithWildcardsMatchCriteria<ExtendedInfo, ExtendedInfo, ExtendedConstructionNode, ExtendedNode>();
}
