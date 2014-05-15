package eu.excitementproject.eop.transformations.generic.truthteller.application.ct;
import java.util.List;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.codeannotations.LanguageDependent;
import eu.excitementproject.eop.common.representation.parse.representation.basic.StanfordDependencyRelation.StanfordDepedencyRelationType;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.generic.truthteller.TruthTellerConstants;
import eu.excitementproject.eop.transformations.generic.truthteller.application.AnnotationRuleApplier;
import eu.excitementproject.eop.transformations.generic.truthteller.application.AnnotationRuleApplierFactory;
import eu.excitementproject.eop.transformations.generic.truthteller.application.DefaultAnnotationRuleApplier;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.AnnotationRule;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.AnnotationRuleWithDescription;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.BasicRuleAnnotations;
import eu.excitementproject.eop.transformations.representation.AdditionalInformationServices;
import eu.excitementproject.eop.transformations.representation.ExtendedConstructionNode;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedInfoGetFields;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.representation.annotations.AnnotationValueException;
import eu.excitementproject.eop.transformations.representation.annotations.ClauseTruth;
import eu.excitementproject.eop.transformations.representation.annotations.PredTruth;
import eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature;
import eu.excitementproject.eop.transformations.utilities.view.ExtendedConstructionRulesViewer;

/**
 * Get a {@link ExtendedConstructionNode} tree and annotate it with {@link ClauseTruth} and {@link PredTruth}
 * <p>
 * Notice how the algo also copies CT to appositions and conjuncts.
 * <P>
 * <b>NOTE</b> the first line of {@link #annotateSubTree(ExtendedConstructionNode, ExtendedConstructionNode)}, applies a list of of the three relative clause annotation 
 * rules, and the hearst pattern rules, which depend on prior recursive CT annotation, i.e. they must be applied in tandem with this applier, each time the recursion 
 * visits a new node. Meaning: at the first line, all those dependent rules are applied (with {@link DefaultAnnotationRuleApplier} ), and then 
 * we calc the new recursive CT+PT. 
 * 
 * @author amnon
 * @since 5 Dec 2011
 */
@LanguageDependent("English")
public class ClauseTruthAnnotationRuleApplier implements AnnotationRuleApplier<ExtendedConstructionNode> {

	private static final Logger logger = Logger.getLogger(ClauseTruthAnnotationRuleApplier.class);
	private static final ExtendedConstructionRulesViewer CONSTRUCTION_TREE_VIEWER = new ExtendedConstructionRulesViewer(null);
	
	private final List<AnnotationRuleWithDescription<ExtendedNode, BasicRuleAnnotations>> interdependentCtRules;
	/**
	 * a rule applier factory to help apply the rules in {@link #interdependentCtRules}
	 */
	private final AnnotationRuleApplierFactory applierFactory;
	/**
	 * Ctor
	 * @param recursiveCtCalcAnnotationRules A list of annotation rules to be applied in tandem with this recursive applier. may be null.
	 */
	public ClauseTruthAnnotationRuleApplier(List<AnnotationRuleWithDescription<ExtendedNode, BasicRuleAnnotations>> recursiveCtCalcAnnotationRules) 
	{
		this.interdependentCtRules = recursiveCtCalcAnnotationRules;
		applierFactory = new AnnotationRuleApplierFactory(null);
		
	}

	//////////////////////////////////////// PUBLIC /////////////////////////////////////////////////////////////////////////

	/**
	 * Recursively compute the {@link ClauseTruth} and {@link PredTruth} of all nodes in tree
	 * @param tree
	 * @return
	 * @throws AnnotatorException
	 */
	public void annotateTreeWithOneRule(ExtendedConstructionNode tree) throws AnnotatorException {
		
		if (tree == null)
			throw new AnnotatorException("got null tree");
		
		ExtendedConstructionNode nodeToAnnotate = SpecialRuleApplicationUtils.skipRoot(tree);
		
		annotateSubTree(nodeToAnnotate, null, nodeToAnnotate);
	}
	
	//////////////////////////////////////// PRIVATE /////////////////////////////////////////////////////////////////////////
	
	/**
	 * @param node
	 * @param treeRoot 
	 * @return
	 * @throws AnnotatorException 
	 */
	private ExtendedConstructionNode annotateSubTree(ExtendedConstructionNode node, ExtendedConstructionNode parent, ExtendedConstructionNode treeRoot)
			throws AnnotatorException {

		ClauseTruth existingClauseTruth = ExtendedInfoGetFields.getClauseTruthObj(node.getInfo());
		// Some nodes were not identified as predicates, and thus have null CT, thus we skip them
		if (existingClauseTruth != null)
		{
			applyInterdependentCTAnnotationRules(treeRoot, interdependentCtRules);
			
			ClauseTruth newClauseTruth = lookupCT(node, parent);	
			// assign new CT
			node.setInfo(new ExtendedInfo(node.getInfo(),AdditionalInformationServices.setClauseTruth(node.getInfo().getAdditionalNodeInformation(), newClauseTruth)));
			
			// assign new PT
			PredTruthAnnotationRuleApplier.getAndSetPT(node);
		}
		
		// recurse to children, making sure to skip over prepositions
		if (node.getChildren() != null)
			for (ExtendedConstructionNode child : node.getChildren())
			{
				child = SpecialRuleApplicationUtils.skipPrepositionalNodes(child);
				annotateSubTree(child, node, treeRoot);
			}
		return node;
	}

	/**
	 * @param subTree
	 * @param interdependentCtRules
	 * @throws AnnotatorException 
	 */
	private void applyInterdependentCTAnnotationRules(ExtendedConstructionNode subTree, 
			List<AnnotationRuleWithDescription<ExtendedNode, BasicRuleAnnotations>> interdependentCtRules) throws AnnotatorException 
	{
		if (interdependentCtRules != null)
		{
			/* 
			 * all annotation rules are matched and applied to the text like substitution rules that delete the matched LHSs.
			 */
			String lastDescription = "";
			for (AnnotationRuleWithDescription<ExtendedNode, BasicRuleAnnotations>  ruleWithDesc : interdependentCtRules)
			{
				if (!lastDescription.equals(ruleWithDesc.getDescription()))
				{
					lastDescription = ruleWithDesc.getDescription();
					logger.debug("Applying rule: " + lastDescription);
				}

				AnnotationRule<ExtendedNode, BasicRuleAnnotations> rule = ruleWithDesc.getRule();
				AnnotationRuleApplier<ExtendedConstructionNode> applier = applierFactory.getAnnotationRuleApplier(rule);
				if (applier instanceof ClauseTruthAnnotationRuleApplier )
					throw new AnnotatorException("It is forbidden to place a ref to the ClauseTruthAnnotationRuleApplier within the list of annotation rule applied by " +
							"the ClauseTruthAnnotationRuleApplier itself" );

				if (logger.isDebugEnabled())
				{
					try {	CONSTRUCTION_TREE_VIEWER.printTree(subTree, true);		}	
					catch (TreeStringGeneratorException e) { throw new AnnotatorException("See nested",e );	}
				}

				// apply normal annotation rule
				try {
					applier.annotateTreeWithOneRule(subTree);
				}
				catch (AnnotatorException e) { throw new AnnotatorException("Error applying rule: " +ruleWithDesc.getDescription(), e); }
			}
		}
		
	}

	/**
	 * Compute the {@link ClauseTruth} value of the given node. If the CT was already set (by a local rule) return that CT value. If node is the sentence's natural 
	 * root (containing the root word, not the artificial root node above that), 
	 * return POSITIVE. Otherwise, if node is a modifier of its parent, use the known recursive formula to compute the CT. If the node is a conjunction 
	 * or apposition, copy its parent's CT. If none of the above cases applies, return the default UNSPECIFIED.
	 * <p>
	 * Note how {@link #skipPrepositionalNodes(ExtendedConstructionNode)} smoothes cases of complex prepositional argument relations, where one or more prepositional
	 * nodes separate the parent from its actual argument node. 
	 * @param node
	 * @param parent 
	 * @return
	 * @throws AnnotatorException 
	 */
	private ClauseTruth lookupCT(ExtendedConstructionNode node, ExtendedConstructionNode parent) throws AnnotatorException {
		
		// get existing CT, (default is U)
		ClauseTruth existingClauseTruth = ExtendedInfoGetFields.getClauseTruthObj(node.getInfo(), TruthTellerConstants.DEFAULT_CT);
		
		ClauseTruth newClauseTruth;
		switch (existingClauseTruth)
		{
		case P:
			// the node was already marked POSITIVE by a previous rule application
			newClauseTruth = ClauseTruth.P;
			break;
		case N:
			// the node was already marked NEGATIVE by a previous rule application
			newClauseTruth = ClauseTruth.N;
			break;
		case U:
		case O:
			if (parent == null)		// node is the tree's root
				newClauseTruth = ClauseTruth.P;		// every root predicate has ct+ by default
			else
			{
				// compute by the relation to ancestor
				StanfordDepedencyRelationType relation = SpecialRuleApplicationUtils.getRelation(node);

				relation = SpecialRuleApplicationUtils.ammendRelationInExplativeVPs(node, parent, relation);
				
				if (ComplementRelations.COMPLEMENT_RELATIONS.contains(relation))
				{
					// compute by calculating antcestor's CT and NU and signature
					PredicateSignature parentSignature = ExtendedInfoGetFields.getPredicateSignatureObj(parent.getInfo());
					PredTruth parentPT = ExtendedInfoGetFields.getPredTruthObj(parent.getInfo());
					try {	newClauseTruth = computeCT(parentSignature, parentPT, relation);	}
					catch (AnnotatorException e) {	throw new AnnotatorException("Error computing CT of " + node, e);	}
				}
				// an apposition/conjunct/cop  simply gets its parent's CT. 
				// TODO does it matter if the conj was CT? ??
				else if (ComplementRelations.RELATIONS_THAT_COPY_CT_FROM_PARENT.contains(relation))
				{
					newClauseTruth = ExtendedInfoGetFields.getClauseTruthObj(parent.getInfo());
				}
				else // not an argument (but a subj, modifier or something), so leave it with the default value. Maybe some other local annotation rule will change it.
				{
					newClauseTruth = existingClauseTruth;	// U or O
				}
			}
			break;
		default:
			throw new AnnotatorException("Internal bug. This CT is not accounted for: "+ existingClauseTruth);
		}
		return newClauseTruth;
	}

	/**
	 * compute the result of this table (table 4 from my seminarion).<br>
	 * Assume this relation is argumental, PredicateType.ARGUMENT_RELATIONS.contains(relation).<br>
	 * 
	 * <p>
	 *	sig(par(p))		pt(par(p))<br>
	 * 			 	pt+	pt-	pt?	pt0	<br>
	 * 		
			+ / -	ct+	ct-	ct?	ct0	<br>
			+ / ?	ct+	ct?	ct?	ct0	<br>
			? / -	ct?	ct-	ct?	ct0	<br>	
			- / +	ct-	ct+	ct?	ct0	<br>
			- / ?	ct-	ct?	ct?	ct0	<br>
			? / +	ct?	ct+	ct?	ct0	<br>
			+ / +	ct+	ct+	ct+	ct+	<br>
			- / -	ct-	ct-	ct-	ct-	<br>
			? / ?	ct?	ct?	ct?	ct?	<br>
			O / O	ct0	ct0	ct0 ct0	<br>

	 * @param parentSignature
	 * @param parentPT
	 * @param relation
	 * @return
	 * @throws AnnotatorException
	 */
	private static ClauseTruth computeCT(PredicateSignature parentSignature,	PredTruth parentPT, StanfordDepedencyRelationType relation) throws AnnotatorException {
		if (parentSignature == null)
			parentSignature = PredicateSignature.U_U;
		if (parentPT == null)
			parentPT = PredTruth.U;	// set the parent with the default PT
		if (relation == null)
			throw new AnnotatorException("internal bug, got null relation");
		
		if (parentSignature.isFactiveRelation(relation))				// row 7 in the table
			return ClauseTruth.P;
		if (parentSignature.isNegativeFactiveRelation(relation))		// row 8
			return ClauseTruth.N;
		if (parentSignature.isUncertainRelation(relation))				// row 9
			return ClauseTruth.U;
		if (parentSignature.isUnspecifiedRelation(relation))			// row 10
			return ClauseTruth.O;
		if (parentPT.equals(PredTruth.U))						// the rest of col 3, rows 1-6
			return ClauseTruth.U;
		if (parentPT.equals(PredTruth.O))						// the rest of col 4, rows 1-6
			return ClauseTruth.O;
		try {
			if (parentPT.equals(PredTruth.P))				// the rest of col 1, rows 1-6
				return parentSignature.getPositiveSide(relation);
			if (parentPT.equals(PredTruth.N))				// the rest of col 2, rows 1-6
				return parentSignature.getNegativeSide(relation);
		} catch (AnnotationValueException e) {
			throw new AnnotatorException("Internal bug", e);
		}

		// default
		throw new AnnotatorException("Internal bug: no CT was found for: " + parentSignature+", "+parentPT+", "+relation);
	}
}
