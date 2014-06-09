/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.truthteller.application;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractConstructionNode;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.generic.truthteller.application.ct.ClauseTruthAnnotationRuleApplier;
import eu.excitementproject.eop.transformations.representation.AdditionalNodeInformation;

/**
 * This is a common interface for all classes that apply a single annotation rule on a tree, 
 * like DefaultAnnotationRuleApplier and {@link ClauseTruthAnnotationRuleApplier}.<br>
 * The rule is predetermined for each instance, supposedly through the Ctor.<br>
 * It is assumed that the {@link #annotateTreeWithOneRule()} method technically just changes/replaces the {@link AdditionalNodeInformation} or {@link Info} of 
 * the given {@link AbstractConstructionNode} tree, without replacing the containing nodes, so there is no need for a return value nor to return node mapping. 
 * 
 * @author Amnon Lotan
 *
 * @since 1 May 2012
 */
public interface AnnotationRuleApplier<N extends AbstractConstructionNode<? extends Info, N>> {

	/**
	 * Annotated a tree, according to the prerogative of the implementation
	 * @param tree
	 * @return
	 * @throws AnnotatorException
	 */
	public void annotateTreeWithOneRule(N tree) throws AnnotatorException;
}
