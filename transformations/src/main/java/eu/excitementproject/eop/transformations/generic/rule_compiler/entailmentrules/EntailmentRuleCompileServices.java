/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.rule_compiler.entailmentrules;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractConstructionNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.transformations.generic.rule_compiler.RuleCompileServices;

/**
 * A {@link RuleCompileServices} interface for entailment {@link SyntacticRule}s.
 * @author Amnon Lotan
 *
 * @since Jul 4, 2012
 *
 * @param <I>	{@link Info} type
 * @param <N>	{@link AbstractNode} type 
 * @param <CN>	{@link AbstractConstructionNode} type. Should be like the N node type, but with a {@link AbstractConstructionNode}{@code .setInfo(Info)} method
 */
public interface EntailmentRuleCompileServices<I extends Info, N extends AbstractNode<I, N>, CN extends AbstractConstructionNode<I, CN>>
	extends	RuleCompileServices<I, N, CN> {
	
	/**
	 * Perform some last fixes on a {@link SyntacticRule}
	 * @param rule
	 * @throws EntailmentCompilationException
	 */
	public void doRuleLastFixes(SyntacticRule<I, N> rule) throws EntailmentCompilationException;
	
	/**
	 * supplement the missing rhs details with lhs details 
	 * @param leftInfo
	 * @param rightInfo
	 * @return
	 * @throws EntailmentCompilationException 
	 */
	I supplementRightInfoWithLeftInfo(I leftInfo, I rightInfo) throws EntailmentCompilationException;
	
	/**
	 * Replace only the specified attribute in the right node with the attribute value from the left node
	 * 
	 * @param leftInfo
	 * @param rightInfo
	 * @param alignmentType
	 * @return
	 * @throws EntailmentCompilationException 
	 */
	I copyLeftParamToRight(I leftInfo, I rightInfo, String alignmentType) throws EntailmentCompilationException;
}
