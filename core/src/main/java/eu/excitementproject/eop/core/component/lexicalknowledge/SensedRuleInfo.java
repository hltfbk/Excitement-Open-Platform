/**
 * 
 */
package eu.excitementproject.eop.core.component.lexicalknowledge;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;


/**
 * A {@link RuleInfo} type that has senses (like wiktionary senses, of Wordnet synsets) for each side of the {@link LexicalRule}, 
 * and an enum representing the rule's relation.
 * @author Amnon Lotan
 * @since May 15, 2011
 * 
 * @param <R> an enum containing all the possible relation types connecting senses in the resource
 * @param <S> the type of the senses the implemented {@link LexicalResource} holds
 */
public interface SensedRuleInfo<S, R extends Enum<R>> extends RuleInfo 
{
	/**
	 * Get the left sense of the rule
	 * @return
	 */
	public S getLeftSense();
	/**
	 * Get the right sense of the rule
	 * @return
	 */
	public S getRightSense();
	/**
	 * Get the ordinal of this sense, with regards to the other senses of the left term of the {@link LexicalRule}
	 * @return
	 */
	public int getLeftSenseNo();
	/**
	 * Get the ordinal of this sense, with regards to the other senses of the right term of the {@link LexicalRule}
	 * @return
	 */
	public int getRightSenseNo();
	/**
	 * Get the relation type of the rule
	 * @return
	 */
	public R getTypedRelation();
}

