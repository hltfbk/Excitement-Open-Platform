package eu.excitementproject.eop.core.component.lexicalknowledge;

import java.util.List;
import eu.excitementproject.eop.core.representation.parsetree.PartOfSpeech;

/**
 * 
 * <P>
 * LexicalResource defines a canonical method of querying and getting lexical relations 
 * between words. The interface allows users to retrieve lexical terms that are entailed 
 * by (or contradictory to) the given term. 
 * 
 * However, this comes with a price: the LexicalResource interface cannot provide 
 * querying capability of finer, resource-dependent relations, since the implementations 
 * for individual resources map the resource-dependent relations onto entailment and 
 * non-entailment. </P>
 * 
 * <P>
 * LexicalResourceWithOwnRelations is defined to recover this capability. It permits 
 * lexical resource implementers to define query methods that use fine-grained relationships 
 * that are specific to a resource. The interface extends LexicalResource, and adds three 
 * methods. Essentially, they replace the argument of canonical relation (TERuleRelation) 
 * to resource-specific local relation.
 * </P>
 *
 * @author tailblues
 *
 * @param <I> an extension of RuleInfo @see RuleInfo, LexicalRule 
 * @param <R> the enum type that represents this resources fine relations. 
 * 
 * <P>
 * Note that R (R fineGrainedRelation in the method argument) is an enum. Java Enum does not permit 
 * inheritance, but all enums are implicit extension of java.lang.Enum. Any normal enum 
 * can be parametrize the R of this interface. Each resource implementation needs to provide 
 * this enum class. For example, a lexical resource based on WordNet might be parameterized 
 * with two classes like the following: class WordNetResource implements 
 * LexicalResourceWithOwnRelation <WordNetInfo, WordNetRelation> , where WordNetInfo is an 
 * extension of RuleInfo, and WordNetRelation is an enum that holds WordNet relations.
 * </P>
 */

@SuppressWarnings("rawtypes") // needed, since we use top Enum (java.lang.Enum) as argument
public interface LexicalResourceWithOwnRelation<I extends RuleInfo , R extends java.lang.Enum> extends LexicalResource<I> {

	/**
	 * Returns a list of lexical rules whose left side matches the given lemma, POS and fine-grained relation. 
	 * An empty list means that no rules were matched. null POS is permitted, just as LexicalResource.
	 * @param lemma
	 * @param pos
	 * @param fineGrainedRelation
	 * @return
	 * @throws LexicalResourceException
	 */
	List<LexicalRule<? extends I>> getRulesForLeft(String lemma, PartOfSpeech pos, R fineGrainedRelation) throws LexicalResourceException;
	
	/**
	 * Returns a list of lexical rules where right side matches the given lemma, POS and fine-grained relation. 
	 * An empty list means that no rules were matched. null POS is permitted, just as LexicalResource.
	 * @param lemma
	 * @param pos
	 * @param fineGrainedRelation
	 * @return
	 * @throws LexicalResourceException
	 */
	List<LexicalRule<? extends I>> getRulesForRight(String lemma, PartOfSpeech pos, R fineGrainedRelation) throws LexicalResourceException;

	/**
	 *  This method returns a list of lexical rules whose left and right sides 
	 *  match the given conditions. 
	 * @param leftLemma
	 * @param leftPos
	 * @param rightLemma
	 * @param rightPos
	 * @param fineGrainedRelation
	 * @return
	 * @throws LexicalResourceException
	 */
	List<LexicalRule<? extends I>> getRules(String leftLemma, PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos, R fineGrainedRelation) throws LexicalResourceException;	

}
