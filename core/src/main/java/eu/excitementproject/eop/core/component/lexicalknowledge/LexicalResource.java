package eu.excitementproject.eop.core.component.lexicalknowledge;

import java.util.List;

import eu.excitementproject.eop.common.Component;
import eu.excitementproject.eop.core.representation.parsetree.PartOfSpeech;

/**
 * [DELETEME_LATER: imported from BIUTEE infrastructure] 
 *
 * A lexical resource is a collection of lexical rules of a certain type 
 * (like WordNet, or VerbOcean) which can be queried. The interface provides 
 * three types of query methods. Queries are specified by lemma and POS pairs,
 * (and optionally, relation) and the results are returned as a list of 
 * LexicalRule objects. [Spec 1.1. Section 4.6.2] 
 * 
 * @author Gil  
 * @since 
 * @param <I> RuleInfo for LexicalRule implementation 
 */
public interface LexicalResource<I extends RuleInfo> extends Component {


	/**
	 * Returns a list of lexical rules whose left side (the head of the lexical relation) matches
	 * the given lemma and POS. An empty list means that no rules were matched. If the user 
	 * gives null POS, the interface will retrieve rules for all possible POSes.
	 * @param lemma Lemma to be matched on LHS. 
	 * @param pos POS to be matched on LHS. null means "don't care". 
	 * @return a list of rules that matches the given condition. Empty list if there's no match. 
	 */
	List<LexicalRule<? extends I>> getRulesForLeft(String lemma, PartOfSpeech pos) throws LexicalResourceException;
	
	/** Returns a list of lexical rules whose right side (the target of the lexical relation) matches 
	 * the given lemma and POS. An empty list means that no rules were matched.
	 * @param lemma Lemma to be matched on RHS. 
	 * @param pos POS to be matched on RHS. null means "don't care". 
	 * @return a list of rules that matches the given condition. Empty list if there's no match. 
	 */
	List<LexicalRule<? extends I>> getRulesForRight(String lemma, PartOfSpeech pos) throws LexicalResourceException;
	
	/** This method returns a list of lexical rules whose left and right sides match the two given pairs of lemma and POS.
	 * @param leftLemma Lemma to be matched on LHS
	 * @param leftPos POS to be matched on LHS. null means "don't care". 
	 * @param rightLemma Lemma to be matched on RHS. 
	 * @param rightPos POS to be matched on RHS. null means "don't care". 
	 * @return a list of rules that matches the given condition. Empty list if there's no match.
	 */
	List<LexicalRule<? extends I>> getRules(String leftLemma, PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos) throws LexicalResourceException;
	
}
