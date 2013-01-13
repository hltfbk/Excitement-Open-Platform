/**
 * 
 */
package ac.biu.nlp.nlp.engineml.generic.rule_compiler.entailmentrules;
import ac.biu.nlp.nlp.engineml.generic.rule_compiler.RuleCompileServices;
import ac.biu.nlp.nlp.engineml.operations.rules.Rule;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractConstructionNode;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNode;

/**
 * A {@link RuleCompileServices} interface for entailment {@link Rule}s.
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
	 * Perform some last fixes on a {@link Rule}
	 * @param rule
	 * @throws EntailmentCompilationException
	 */
	public void doRuleLastFixes(Rule<I, N> rule) throws EntailmentCompilationException;
	
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
