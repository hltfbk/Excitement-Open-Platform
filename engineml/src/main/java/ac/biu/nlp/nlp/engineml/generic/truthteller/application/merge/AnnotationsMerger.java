/**
 * 
 */
package ac.biu.nlp.nlp.engineml.generic.truthteller.application.merge;
import ac.biu.nlp.nlp.engineml.generic.truthteller.application.DefaultAnnotationRuleApplicationOperation;
import ac.biu.nlp.nlp.engineml.generic.truthteller.representation.BasicRuleAnnotations;
import ac.biu.nlp.nlp.engineml.representation.AdditionalNodeInformation;

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
