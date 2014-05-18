/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.truthteller.application.ct;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.generic.truthteller.application.AnnotationRuleApplier;
import eu.excitementproject.eop.transformations.representation.AdditionalInformationServices;
import eu.excitementproject.eop.transformations.representation.ExtendedConstructionNode;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedInfoGetFields;
import eu.excitementproject.eop.transformations.representation.annotations.ClauseTruth;
import eu.excitementproject.eop.transformations.representation.annotations.NegationAndUncertainty;
import eu.excitementproject.eop.transformations.representation.annotations.PredTruth;

/**
 * computes PT in every node as the product of CT and NU.<br>
 * For each node, set the Predicate Truth value, as the product of NU and CT:
	 * PT = NU x CT. <br>
	 * If NU if null, the CT is returned. If CT is null, null is returned.
 * <p>
 * 
 * <b>NOTE</b> this applier is hard coded to run last after all other annotation rules, to make sure everyone has her PT.
 * 
 * @author Amnon Lotan
 *
 * @since Jul 10, 2012
 */
public class PredTruthAnnotationRuleApplier implements AnnotationRuleApplier<ExtendedConstructionNode> {

	/////////////////////////////////////// Singleton Ctor	////////////////////////////////////////////////////////////
	
	/**
	 * Ctor
	 */
	private PredTruthAnnotationRuleApplier() {}
	private static PredTruthAnnotationRuleApplier instance = new PredTruthAnnotationRuleApplier();
	public static PredTruthAnnotationRuleApplier getInstance()
	{
		return instance;
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
		
		annotateSubTree(nodeToAnnotate, null);
	}
	
	///////////////////////////////////////// PACKAGE	////////////////////////////////////////////////////////////////////
	
	/**
	 * read the node's PT. if null, compute and assign it.
	 * @param node
	 * @return
	 * @throws AnnotatorException 
	 */
	static PredTruth getAndSetPT(ExtendedConstructionNode node) throws AnnotatorException {
		if (node == null)
			throw new AnnotatorException("got null node");
		PredTruth pt = ExtendedInfoGetFields.getPredTruthObj(node.getInfo());
		if (pt == null | PredTruth.U.equals(pt))
		{
			computeAndSetPT(node);
			pt = ExtendedInfoGetFields.getPredTruthObj(node.getInfo());
		}
		return pt;
	}
	
	//////////////////////////////////////// PRIVATE /////////////////////////////////////////////////////////////////////////

	/**
	 * @param node
	 * @return
	 * @throws AnnotatorException 
	 */
	private ExtendedConstructionNode annotateSubTree(ExtendedConstructionNode node, ExtendedConstructionNode parent) throws AnnotatorException {
		
		// assign new PT
		getAndSetPT(node);
		// recurse to children, making sure to skip over prepositions
		if (node.getChildren() != null)
			for (ExtendedConstructionNode child : node.getChildren())
			{
				child = SpecialRuleApplicationUtils.skipPrepositionalNodes(child);
				annotateSubTree(child, node);
			}
		return node;
	}

	/**
	 * Set the node's Predicate Truth value, as the product of NU and CT:
	 * PT = NU x CT. <br>
	 * If NU if null, the CT is returned. If CT is null, null is returned.
	 * @param node
	 * @throws AnnotatorException
	 */
	private static void computeAndSetPT(ExtendedConstructionNode node) throws AnnotatorException {
		ClauseTruth ct = ExtendedInfoGetFields.getClauseTruthObj(node.getInfo());
		if (ct != null)	
		{
			NegationAndUncertainty nu = ExtendedInfoGetFields.getNegationAndUncertaintyObj(node.getInfo());	// null is permitted

			PredTruth pt;
			try {	pt = computePT(nu, ct);	}
			catch (AnnotatorException e) {	throw new AnnotatorException("Error computing the PredicateTruth of " + node, e);	}

			// update node's PT
			node.setInfo(new ExtendedInfo(node.getInfo(), AdditionalInformationServices.setPredTruth( node.getInfo().getAdditionalNodeInformation(), pt)));
		}
	}


	/**
	 * PT table:
	 * 
	 * PT = NU x CT:	<br>
	 * NU\CT		ct+	ct-	ct?	ct0	<br>
	 * nu+	pt+	pt-	pt?	pt0	<br>
	 * nu-	pt-	pt+	pt?	pt0	<br>
	 * nu?	pt?	pt?	pt?	pt?	<br>
	 * 
	 * @param nu
	 * @param ct
	 * @return
	 * @throws AnnotatorException 
	 */
	private static PredTruth computePT(NegationAndUncertainty nu, ClauseTruth ct) throws AnnotatorException 
	{
		if (nu == null)		
			switch (ct)
			{
			case P:
				return PredTruth.P;
			case N:
				return PredTruth.N;
			case U:
				return PredTruth.U;
			case O:
				return PredTruth.O;
			default:
				throw new AnnotatorException("Unidentified CT: " + ct);
			}
		else
			switch (nu)
			{
			case P:
				switch (ct)
				{
				case P:
					return PredTruth.P;
				case N:
					return PredTruth.N;
				case U:
					return PredTruth.U;
				case O:
					return PredTruth.O;
				default:
					throw new AnnotatorException("Unidentified CT: " + ct);
				}
			case N:	// swap positive and negative
				switch (ct)
				{
				case P:
					return PredTruth.N;
				case N:
					return PredTruth.P;
				case U:
					return PredTruth.U;
				case O:
					return PredTruth.O;
				default:
					throw new AnnotatorException("Unidentified CT: " + ct);
				}
			case U:
				return PredTruth.U;
			default:
				throw new AnnotatorException("computePT() internal bug: unidentified NU: " + nu );
			}
	}
}
