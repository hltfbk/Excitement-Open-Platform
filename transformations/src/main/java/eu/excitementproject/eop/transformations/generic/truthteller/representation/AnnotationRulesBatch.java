/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.truthteller.representation;
import java.io.Serializable;
import java.util.List;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;


/**
 * @author Amnon Lotan
 *
 * @since Jul 10, 2012
 */
public class AnnotationRulesBatch<N extends AbstractNode<? extends Info, N>, A> implements Serializable
{
	private static final long serialVersionUID = 7571562312284252773L;
	
	private final List<AnnotationRuleWithDescription<N, A>> annotationRules;
	private final List<AnnotationRuleWithDescription<N, A>> recursiveCtCalcAnnotationRules;
	
	/**
	 * Ctor
	 * @param annotationRules
	 * @param recursiveCtCalcAnnotationRules
	 */
	public AnnotationRulesBatch(
			List<AnnotationRuleWithDescription<N, A>> annotationRules,
			List<AnnotationRuleWithDescription<N, A>> recursiveCtCalcAnnotationRules) {
		super();
		this.annotationRules = annotationRules;
		this.recursiveCtCalcAnnotationRules = recursiveCtCalcAnnotationRules;
	}
	
	/**
	 * @return the recursiveCtCalcAnnotationRules
	 */
	public List<AnnotationRuleWithDescription<N, A>> getRecursiveCtCalcAnnotationRules() {
		return recursiveCtCalcAnnotationRules;
	}
	/**
	 * @return the annotationRules
	 */
	public List<AnnotationRuleWithDescription<N, A>> getAnnotationRules() {
		return annotationRules;
	}
	

}
