package eu.excitementproject.eop.transformations.operations.rules;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

/**
 *
 * A rule base is an object that can return {@link SyntacticRule}s (i.e. a "container"
 * of {@link SyntacticRule}s ).
 * <BR>
 * <B>THREAD SAFETY: THE SUBCLASSES SHOULD DECLARE WHETHER THEY ARE THREAD SAFE OR NOT<B>
 * <P>
 * A simple {@link RuleBase} can be just a container that has one method
 * (e.g. <code>getRules()</code>) that returns rules.
 * <BR>
 * A more advanced {@link RuleBase} can be a class that returns rules according to some criteria
 * (e.g. <code>getRulesByKey(Key key)</code>).
 * 
 * 
 * @author Asher Stern
 * @since Feb 5, 2011
 *
 * @param <I>
 * @param <S>
 */
public interface RuleBase<I, S extends AbstractNode<I, S>>
{

}
