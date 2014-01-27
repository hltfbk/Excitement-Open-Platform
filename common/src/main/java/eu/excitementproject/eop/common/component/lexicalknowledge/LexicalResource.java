/**
 * 
 */
package eu.excitementproject.eop.common.component.lexicalknowledge;
import java.util.List;

import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;



/**
 * This is the top level interface for wrappers of lexical resources. Its methods accept tuples of {@code <lemma, POS>} and 
 * retrieve {@link LexicalRule}s that match the criteria (each rule contains a resource-specific {@link RuleInfo} record).<br> 
 * In case the user gives <code>null</code> POS, implementations must retrieve rules for all possible POSs.<br>
 * In case the user gives a POS that is not supported by the implemented lexical resource, then the implementation must return an empty list (not null). 
 * For instance, Wikipedia supports only nouns, and WordNet supports only nouns, verbs, adjectives and adverbs.     
 * <p>
 * If, in a particular implementation, rules can be filtered by some criteria, then that filtering should be configurable in the {@link LexicalResource}'s
 * constructor, and the criteria should also appear (not as a filter) in the respective implementation of {@link RuleInfo}.
 * <p>
 * In addition, each implementation must accept a constant <code>limitOnRetrievedRules</code> that defines the max number of rules to be returned for each 
 * query. The returned rules should always be the best available. This const should be accepted via Ctor.
 * <p>
 * <b>Note</b> The {@link LexicalResource} is oblivious of the context of the {@code lemma+pos}s it gets. E.g. it would retrieve the same rules for 
 * {@code <Windows, NOUN>} whether the context is OS or interior design. So <b>it is the user's responsibility to disambiguate</b> such terms.  
 * 
 * @author Amnon Lotan
 * @since 06/05/2011
 * 
 * @param <I> type of the implemented additional information a rule contains (besides the fields of {@link LexicalRule} )
 */
public interface LexicalResource<I extends RuleInfo> 
{
	/**
	 * Return a list of lexical rules whose right side (the target of the lexical relation) matches the given lemma+pos. An empty list means that
	 * no rules were matched. If applicable, the
	 * list is sorted in descending order of confidence.<br>
	 * In case the user gives <code>null</code> POS, retrieve rules for all possible POSs.<br>
	 * In case the user gives a POS that is not supported by the implemented lexical resource, then the implementation must return an empty list (not null).
	 * 
	 * @param lemma
	 * @param pos can be <code>null</code>
	 * @return A list of lexical rules whose right side (the target of the lexical relation) matches the given lemma+pos. An empty list means that
	 * no rules were matched. If applicable, the
	 * list is sorted in descending order of confidence.
	 * @throws LexicalResourceException 
	 */
	List<LexicalRule<? extends I>> getRulesForRight(String lemma, PartOfSpeech pos) throws LexicalResourceException;
	/**
	 * Return a list of lexical rules whose left side (the head of the lexical relation) matches the given lemma+pos. An empty list means that
	 * no rules were matched. If applicable, the
	 * list is sorted in descending order of confidence.<br>
	 * In case the user gives <code>null</code> POS, retrieve rules for all possible POSs.<br>
	 * In case the user gives a POS that is not supported by the implemented lexical resource, then the implementation must return an empty list (not null).
	 * 
	 * @param lemma
	 * @param pos can be <code>null</code>
	 * @return A list of lexical rules whose left side (the head of the lexical relation) matches the given lemma+pos. An empty list means that
	 * no rules were matched. If applicable, the list is sorted in descending order of confidence.
	 */
	List<LexicalRule<? extends I>> getRulesForLeft(String lemma, PartOfSpeech pos)  throws LexicalResourceException;
	/**
	 * Return a list of lexical rules whose left and right sides match the two given pairs of lemma+pos.<br> 
	 * If no rules are matched, an empty list is returned. If applicable, the list is sorted in descending order of confidence.<br>
	 * In case the user gives <code>null</code> POS, retrieve rules for all possible POSs.<br>
	 * In case the user gives a POS that is not supported by the implemented lexical resource, then the implementation must return an empty list (not null).
	 * <P>
	 * <b>CONSULT</b> the main comment of the implementation you use, in order to find out which combinations of LHS and RHS POSs are supported.
	 * 
	 * @param leftLemma
	 * @param leftPos can be <code>null</code>
	 * @param rightLemma
	 * @param rightPos can be <code>null</code>
	 * @return A list of lexical rules whose left and right sides match the two given pairs of lemma+pos. 
	 * An empty list means that no rules were matched. If applicable, the list is sorted in descending order of confidence.
	 */
	List<LexicalRule<? extends I>> getRules(String leftLemma, PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos)  throws LexicalResourceException;
	
	/**
	 * Cleans up any resources used by the resource.
	 */
	void close() throws LexicalResourceCloseException;
}
