package eu.excitementproject.eop.transformations.operations.rules;
import eu.excitementproject.eop.common.component.syntacticknowledge.RuleWithConfidenceAndDescription;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.transformations.datastructures.LemmaAndPos;

/**
 * Represents a lexical-syntactic rule base.<BR>
 * 
 * @deprecated No longer used.
 * 
 * Usage:
 * <OL>
 * <LI>Call as many times as you want
 * to {@link #getLeftHandSidesByLemmaAndPos(LemmaAndPos)}.
 * Store the results.</LI>
 * <LI>Call {@link #getRulesByLeftHandSide(AbstractNode)} as many times as you want, but
 * only with the stored results that were returned by the previous step.</LI>
 * <LI>Once you want to call again {@link #getLeftHandSidesByLemmaAndPos(LemmaAndPos)},
 * all the results returned earlier, in the first step, become irrelevant.</LI>
 * </OL>
 * <P>
 * <B>This rule base is not thread safe.</B>
 * 
 * 
 * <BR><B>NOT THREAD SAFE</B>
 * 
 * 
 * 
 * @author Asher Stern
 * @since Feb 14, 2011
 *
 * @param <I>
 * @param <S>
 */
@Deprecated
public abstract class DynamicRuleBase<I, S extends AbstractNode<I, S>> implements RuleBase<I, S>
{
	/**
	 * Given a lemma and part-of-speech, this method returns rules' left-hand-sides that one of
	 * their nodes contains that lemma+part-of-speech.
	 * 
	 * @param lemmaAndPos
	 * @return
	 * @throws RuleBaseException
	 */
	public abstract ImmutableSet<S> getLeftHandSidesByLemmaAndPos(LemmaAndPos lemmaAndPos) throws RuleBaseException;
	
	/**
	 * Given a rule's left-hand-side, this method returns rules with that left-hand-side.
	 * @param leftHandSide
	 * @return
	 * @throws RuleBaseException
	 */
	public abstract ImmutableSet<RuleWithConfidenceAndDescription<I, S>> getRulesByLeftHandSide(S leftHandSide) throws RuleBaseException; 

}
