/**
 * 
 */
package ac.biu.nlp.nlp.engineml.generic.truthteller.representation;

import java.io.Serializable;
import java.util.List;

import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNode;

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
