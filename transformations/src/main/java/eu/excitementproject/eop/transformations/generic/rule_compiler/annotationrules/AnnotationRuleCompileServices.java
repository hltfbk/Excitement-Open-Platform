/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.rule_compiler.annotationrules;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.common.representation.parse.representation.basic.EdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractConstructionNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.transformations.generic.rule_compiler.RuleCompileServices;
import eu.excitementproject.eop.transformations.generic.rule_compiler.entailmentrules.CgxEntailmentRuleCompiler;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.RuleAnnotations;


/**
 * An interface for dealing with the implementation details of interpreting the String values in annotation rule CGX/XML files,
 * and creating {@link NodeInfo}s and {@link EdgeInfo}s and {@link RuleAnnotations} out of them.
 * 
 * @author Amnon Lotan
 *
 * @param <I>	node {@link Info} type
 * @param <N>	{@link AbstractNode} type 
 * @param <CN>	{@link AbstractConstructionNode} type. Should be like the N node type, but with a {@link AbstractConstructionNode}{@code .setInfo(Info)} method
 * @param <A>	A {@link RuleAnnotations} type representing the annotation values records on the right side of the rule
 * 
 * @since 3 Jul 2012
 */
public interface AnnotationRuleCompileServices<I extends Info, N extends AbstractNode<I, N>, CN extends AbstractConstructionNode<I, CN>, A extends RuleAnnotations>
		extends RuleCompileServices<I, N, CN>
	{

	/**
	 * 	perform any special textual expansions the  compilationServices implementation may require, in addition to the common expansion done in {@link CgxEntailmentRuleCompiler}
	 * @param ruleTexts
	 * @return
	 * @throws AnnotationCompilationException 
	 */
	public Set<String> doSpecialRuleTextExpantion(Collection<String> ruleTexts) throws AnnotationCompilationException;
	
	/**
	 * @return
	 */
	public Set<String> getPredicateList();


	/**
	 * Turn the given label of an annotations Concept into an annotations record
	 * @param string
	 * @return
	 * @throws AnnotationCompilationException 
	 */
	public A labelToAnnotations(String string) throws AnnotationCompilationException;
	
	/**
	 * for each partial mapping pair, replace only the attribute specified in the partial mapping type
	 * @param partialMappings
	 * @throws AnnotationCompilationException 
	 */
	public void  performPartialMappings( List<PartialAlignment<CN,A>> partialMappings) throws AnnotationCompilationException;
	
}
