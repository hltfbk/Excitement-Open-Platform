/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.truthteller.application.merge;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.AnnotationValueException;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.BasicRuleAnnotations;
import eu.excitementproject.eop.transformations.representation.AdditionalInformationServices;
import eu.excitementproject.eop.transformations.representation.AdditionalNodeInformation;
import eu.excitementproject.eop.transformations.representation.annotations.ClauseTruth;
import eu.excitementproject.eop.transformations.representation.annotations.NegationAndUncertainty;
import eu.excitementproject.eop.transformations.representation.annotations.PredTruth;

/**
 * compute the new merged node annotations, which are the product of superimposing the new annotations from the rule, on the annotations
 * from the tree node.
 * <p>
 * Annotation value flipping is supported!
 *  
 * @author Amnon Lotan
 *
 * @since Jun 20, 2012
 */
public class DefaultAnnotationsMerger implements AnnotationsMerger {

	/**
	 * compute the new merged node annotations, which are the product of superimposing the new annotations from the rule, on the annotations 
	 * from the tree node. 
	 *   
	 * @param origAnnotaions
	 * @param ruleAnnotations
	 * @return
	 * @throws AnnotationsMergerException 
	 * 
	 * */
	public AdditionalNodeInformation mergeAnnotations(	AdditionalNodeInformation origAnnotaions, BasicRuleAnnotations ruleAnnotations) throws AnnotationsMergerException {
		if (ruleAnnotations == null)
			return  origAnnotaions;
		else
		{
			AdditionalNodeInformation mergedAnnotations = origAnnotaions;
			if (ruleAnnotations.getPredicateSignature() != null)
				mergedAnnotations = AdditionalInformationServices.setPredicateSignature(mergedAnnotations, ruleAnnotations.getPredicateSignature());
			try {
				if (ruleAnnotations.getNegationAndUncertainty() != null)
				{
					NegationAndUncertainty newNU = ruleAnnotations.getNegationAndUncertainty().mergeAnnotation(mergedAnnotations.getNegationAndUncertainty());
					mergedAnnotations = AdditionalInformationServices.setNegationAndUncertainty(mergedAnnotations, newNU);
				}
				if (ruleAnnotations.getClauseTruth() != null)
				{
					ClauseTruth newCt = ruleAnnotations.getClauseTruth().mergeAnnotation(mergedAnnotations.getClauseTruth());
					mergedAnnotations = AdditionalInformationServices.setClauseTruth(mergedAnnotations, newCt);
				}
				if (ruleAnnotations.getPredTruth() != null)
				{
					PredTruth newPt = ruleAnnotations.getPredTruth().mergeAnnotation(mergedAnnotations.getPredTruth());
					mergedAnnotations = AdditionalInformationServices.setPredTruth(mergedAnnotations, newPt);
				}
			} catch (AnnotationValueException e) {
				throw new AnnotationsMergerException("There was an error merging one of the rule's annotations:\n" + ruleAnnotations +"\nwith the text node annotations:\n"
						+ origAnnotaions);
			}
			if (ruleAnnotations.getMonotonicity() != null)
				mergedAnnotations = AdditionalInformationServices.setMonotonicity(mergedAnnotations, ruleAnnotations.getMonotonicity());
			return mergedAnnotations;
		}
	}

}
