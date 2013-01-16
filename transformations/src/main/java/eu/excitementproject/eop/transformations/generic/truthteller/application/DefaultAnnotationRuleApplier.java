/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.truthteller.application;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.common.representation.parse.tree.match.AllEmbeddedMatcher;
import eu.excitementproject.eop.common.representation.parse.tree.match.MatcherException;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.generic.truthteller.application.merge.AnnotationsMerger;
import eu.excitementproject.eop.transformations.generic.truthteller.application.merge.DefaultAnnotationsMerger;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.AnnotationRule;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.BasicRuleAnnotations;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.representation.ExtendedConstructionNode;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.view.AnnotationRulesViewer;
import eu.excitementproject.eop.transformations.utilities.view.ExtendedConstructionRulesViewer;

/**
 * Apply a normal annotation rule to all possible matches in the tree, and return the tree after all applications were performed on it.
	 * This means that 
	 * <li> 1) {@link AllEmbeddedMatcher} finds a list of all matches of the rule in the tree
	 * <li> 2)  the rules are applied iteratively to the tree according to the matches 
	 *<p>
	 * Annotation value flipping is also supported!
	 * <p>
	 * This is different than the way the engine applies a rule on a tree to produce a new tree for each application (and its corresponding match).
	 * <p>
 * 
 * @author Amnon Lotan
 * @since 13/06/2011
 *
 */
public class DefaultAnnotationRuleApplier implements AnnotationRuleApplier<ExtendedConstructionNode> 
{
	
	/**
	 * Ctor
	 * @param rule
	 * @throws AnnotatorException 
	 */
	public DefaultAnnotationRuleApplier(AnnotationRule<ExtendedNode, BasicRuleAnnotations> rule) throws AnnotatorException {
		if (rule == null)
			throw new AnnotatorException("null annotation rule");
		this.rule = rule;
	}
	
	/**
	 * Apply a normal annotation rule to all possible matches in the tree, and return the tree after all matches were performed on it.
	 * This means that 
	 * <li>1) {@link AllEmbeddedMatcher} finds a list of all matches of the rule in the tree
	 * <li>2)  the rule is applied iteratively to the tree according to the matches
	 * <p>
	 * annotation flipping is supported here, in the sense that matches are sought once, and then applied in a batch. This way a flip may occur
	 * only once per match.
	 * <p>
	 * This is different than the way the engine applies a rule on a tree to produce a new tree for each application (and its corresponding match).
	 *  
	 * @param tree
	 * @return
	 * @throws AnnotatorException
	 */
	public void annotateTreeWithOneRule(ExtendedConstructionNode tree) throws AnnotatorException
	{
		// get all matches between the tree and the rule's LHS
		Set<BidirectionalMap<ExtendedNode, ExtendedConstructionNode>> matchesOfLhsToTree;
		try {
			matchesOfLhsToTree = AnnotationRuleApplierUtils.getMatchesOfLhsToTree(tree, rule);
		} catch (MatcherException e) {
			logger.error("Error matching the following tree to the following rule");
			try {	TREE_VIEWER.printTree(tree, false);	} catch (TreeStringGeneratorException e1) {	}
			try {	RULE_VIEWER.viewRule(rule);	} catch (TreeStringGeneratorException e1) {	}
			throw new AnnotatorException("see nested", e );
		}
		
		
		// iteratively apply the rule on the tree, according to the the set of matches 
		if (matchesOfLhsToTree != null & !matchesOfLhsToTree.isEmpty())
		{
			int applications = 0;	
			TreeAndParentMap<ExtendedInfo, ExtendedConstructionNode> textTreeAndParentMap;
			try {	textTreeAndParentMap = new TreeAndParentMap<ExtendedInfo, ExtendedConstructionNode>(tree);	}
			catch (	TreeAndParentMapException e) { throw new AnnotatorException("see nested", e); 	}
			
			for (BidirectionalMap<ExtendedNode, ExtendedConstructionNode> matchOfLhsToTree : matchesOfLhsToTree )
			{
				boolean applicationChangedTree;
				try {
					applicationChangedTree = 
						AnnotationRuleApplierUtils.applyRuleToMatchedTree(textTreeAndParentMap, matchOfLhsToTree, rule.getMapLhsToAnnotations(), annotationsMerger);
				} catch (OperationException e) {
					logger.error("Error applying the following rule to the following tree");
					try {	TREE_VIEWER.printTree(textTreeAndParentMap.getTree(), false);	} catch (TreeStringGeneratorException e1) {	}
					try {	RULE_VIEWER.viewRule(rule);	} catch (TreeStringGeneratorException e1) {	}
					throw new AnnotatorException("see nested", e );
				}
				if (applicationChangedTree)
					applications++;
			}
			if(applications>0)
			{
				if (logger.isDebugEnabled())
				{
					logger.debug("Rule applied "+applications+" times.");
					try {	
						TREE_VIEWER.printTree(tree, true);	
						RULE_VIEWER.viewRule(rule);
					}
					catch (TreeStringGeneratorException e) { throw new AnnotatorException("See nested",e );	}
				}
			}
		}
	}
	
	//////////////////////////////////////////////////// PRIVATE	//////////////////////////////////////////////////////
	
	private static final AnnotationRulesViewer RULE_VIEWER = new AnnotationRulesViewer(null);
	private static final ExtendedConstructionRulesViewer TREE_VIEWER = new ExtendedConstructionRulesViewer (null);
	private static final Logger logger = Logger.getLogger(DefaultAnnotationRuleApplier.class);
	private static final AnnotationsMerger annotationsMerger = new DefaultAnnotationsMerger();	//annotatiorMerger;
	
	private final AnnotationRule<ExtendedNode, BasicRuleAnnotations> rule;
}
