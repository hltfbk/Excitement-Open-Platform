/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.truthteller.application.merge;
import eu.excitementproject.eop.transformations.generic.truthteller.application.DefaultAnnotationRuleApplicationOperation;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.BasicRuleAnnotations;
import eu.excitementproject.eop.transformations.representation.AdditionalNodeInformation;

/**
 * The AnnotationsMerger class is used by a {@link DefaultAnnotationRuleApplicationOperation} at RHS instantiation to merge between 
 * the annotations of an LHS node, and those of the RHS node.
 * 
 * @author Amnon Lotan
 *
 * @since Jun 20, 2012
 */
public interface AnnotationsMerger {
	
	AdditionalNodeInformation mergeAnnotations(	AdditionalNodeInformation origAnnotaions, BasicRuleAnnotations newAnnotations) throws AnnotationsMergerException;

}
