package eu.excitementproject.eop.common.component.lexicalknowledge;

import java.util.List;

import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.component.lexicalknowledge.RelationSpecifier;

/**
 * 
 * <P> The interface LexicalResource defines the common method of querying and getting lexical 
 * entailment rules of the given words. This abstraction is useful in the sense that it 
 * abstracts various underlying lexical resources with the entailment relation. This enables 
 * the caller of LexicalResource implementations to query various knowledge resources in the 
 * same fashion. However, this comes with a price: the LexicalResource interface cannot provide 
 * querying capability of relations others than entailment. For example, user of that interface 
 * cannot query NonEntailment words of the given term (terms that you are sure that is not 
 * entailment), nor resource-specific relations (like synonym or hypernym of WordNet, or 
 * Stronger-than or happens-before of VerbOcean, etc). These resource-specific relations are 
 * reported back as part of the query result (RuleInfo within LexicalRule), but cannot be 
 * directly asked for.
 * 
 * <P> LexicalResourceWithRelation is defined to recover this capability. It permits 
 * lexical resource implementers to define additional query methods with relations. It enables 
 * not only the canonical relation queries but also queries with resource-specific relations.
 * The interface extends LexicalResource, and adds three methods. Essentially, each of the method 
 * has one additional argument that represents the relation to be queried.
 * 
 * <P> 
 * Each of the method gets one additional parameter R relation that specifies the relation 
 * to be fetched. For example, if relation is "NonEntailment", the resource will return rules 
 * that specifies "NonEntailment" (where LHS->RHS is confidently cannot be entailment). If the 
 * relation holds "Synonym", it means the query should return Lexical rules where LHS to RHS is 
 * synonymy. Note that the relation is represented by a generic parameter R. Each R is an 
 * extension of RelationSpecifier, where the R can be tailored for each resource. We adopt a 
 * simple hierarchy for this R. See interface RelationSpecifier and its extensions. 
 * 
 * @author Gil 
 *
 * @param <I>
 * @param <R>
 */
public interface LexicalResourceWithRelation<I extends RuleInfo, R extends RelationSpecifier> extends LexicalResource<I> {
	
	/**
	 * Returns a list of lexical rules whose left side matches the given lemma, POS and relation. 
	 * An empty list means that no rules were matched. null POS is permitted, just as LexicalResource.
	 * @param lemma
	 * @param pos
	 * @param relation
	 * @return
	 * @throws LexicalResourceException
	 */
	List<LexicalRule<? extends I>> getRulesForLeft(String lemma, PartOfSpeech pos, R relation) throws LexicalResourceException;
	
	/**
	 * Returns a list of lexical rules where right side matches the given lemma, POS and relation. 
	 * An empty list means that no rules were matched. null POS is permitted, just as LexicalResource.
	 * @param lemma
	 * @param pos
	 * @param relation
	 * @return
	 * @throws LexicalResourceException
	 */
	List<LexicalRule<? extends I>> getRulesForRight(String lemma, PartOfSpeech pos, R relation) throws LexicalResourceException;

	/**
	 *  This method returns a list of lexical rules whose left and right sides 
	 *  match the given conditions. 
	 * @param leftLemma
	 * @param leftPos
	 * @param rightLemma
	 * @param rightPos
	 * @param relation 
	 * @return
	 * @throws LexicalResourceException
	 */
	List<LexicalRule<? extends I>> getRules(String leftLemma, PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos, R relation) throws LexicalResourceException;	

}
