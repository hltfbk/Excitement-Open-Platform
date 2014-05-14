/**
 * 
 */
package eu.excitementproject.eop.core.utilities.dictionary.wordnet;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.jwi.JwiSensedWord;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.jwi.JwiSynset;


/**
 * An enum of all Wordnet relation types (relations from Synsets to words)
 * @author Amnon Lotan
 *
 * @since Nov 27, 2011
 * @see http://wordnet.princeton.edu/wordnet/man/wninput.5WN.html
 */
public enum WordNetRelation {

	/**
	 * Cousin relations do NOT have a real wordnet counterpart, but we do use it as a relation, to get the cousins of synsets.
	 * <p>
	 * Currently supported only {@link JwiSynset}s and {@link JwiSensedWord}s 
	 */
	STRICT_2ND_DEGREE_COUSIN(false, false),

	
	// Nouns, Verbs, Adjectives and Adverbs

	/**
	 * Synonym does NOT have a counterpart in wordnet, cos a word's synonyms are just the other words in the same Synset. lexical.
	 */
	SYNONYM(true, false),
	/**
	 * the opposite of something. lexical
	 */
	ANTONYM(true, false)	,
	REGION(false, false),
	USAGE(false, false) 	,

	// Nouns and Verbs

	/**
	 * The generic term used to designate a whole class of specific instances. Y is a hypernym of X if X is a (kind of) Y .
	 */
	HYPERNYM(false, true) ,
	/**
	 * The specific term used to designate a member of a class. X is a hyponym of Y if X is a (kind of) Y 
	 */
	HYPONYM (false, true)	,
	/**
	 * 
	 * Terms in different syntactic categories that have the same root form and are semantically related.
	 * AKA "NOMINALIZATION" or "Derivationally related form" in other implementations. used for nouns and verbs. lexical.
	 */
	DERIVATIONALLY_RELATED (true, false),
	/**
	 * A proper noun that refers to a particular, unique referent (as distinguished from nouns that refer to classes). This is a specific form of hypernym. 
	 */
	INSTANCE_HYPERNYM (false, true),
	/**
	 * A proper noun that refers to a particular, unique referent (as distinguished from nouns that refer to classes). This is a specific form of hyponym.
	 */
	INSTANCE_HYPONYM (false, true)	,

	// Nouns and Adjectives

	ATTRIBUTE (false, false)	,
	
	// Verbs and Adjectives
	
	/**
	 * lexical 
	 */
	SEE_ALSO (true, false)	,

	// Nouns

	/**
	 * The name of the whole of which the meronym names a part. Y is a holonym of X if X is a part of Y .
	 */
	MEMBER_HOLONYM (false, true),
	/**
	 * The name of the whole of which the meronym names a part. Y is a holonym of X if X is a part of Y .
	 */
	SUBSTANCE_HOLONYM(false, true),
	/**
	 * The name of the whole of which the meronym names a part. Y is a holonym of X if X is a part of Y .
	 */
	PART_HOLONYM(false, true) 	,
	/**
	 * The name of a constituent part of, the substance of, or a member of something. X is a meronym of Y if X is a part of Y .
	 */
	MEMBER_MERONYM 	(false, true),
	/**
	 * The name of a constituent part of, the substance of, or a member of something. X is a meronym of Y if X is a part of Y .
	 */
	SUBSTANCE_MERONYM(false, true),
	/**
	 * The name of a constituent part of, the substance of, or a member of something. X is a meronym of Y if X is a part of Y .
	 */
	PART_MERONYM 	(false, true),
	CATEGORY_MEMBER	(false, false),
	REGION_MEMBER	(false, false), 
	USAGE_MEMBER(false, false)	,

	// Verbs

	/**
	 * A verb X entails Y if X cannot be done unless Y is, or has been, done. Semantic.
	 */
	ENTAILMENT(false, true),
	CAUSE(false, true), 
	VERB_GROUP	(false, false),
	/**
	 * the verb Y is a troponym of the verb X if the activity Y is doing X in some manner (to lisp is a troponym of to talk)
	 */
	TROPONYM(false, true),
	
	// Adjectives
	
	SIMILAR_TO	(false, false), 
	/**
	 * An adjective that is derived from a verb. lexical.
	 */
	PARTICIPLE_OF(true, false),
	/**
	 * pertains to noun. lexical
	 */
	PERTAINYM(true, false)	,

	// Adverbs

	/**
	 * Adverbs generally point to the adjectives from which they are derived. lexical.
	 */
	DERIVED	(true, false), 

	;
	


	private final boolean isLexical;
	/**
	 * All lexical relations should be non transitive!
	 */
	private final boolean isTransitive;
	private WordNetRelation(boolean isLexical, boolean isTransitive)
	{
		this.isLexical = isLexical;
		this.isTransitive = isTransitive;
	}
	
	/**
	 * @return the isLexical
	 */
	public boolean isLexical() {
		return isLexical;
	}
	
	/**
	 * Indicates whether this relation can participate in chaining. See {@link Synset#getRelatedSynsets(WordNetRelation, int)}
	 * @return the isTransitive
	 */
	public boolean isTransitive() {
		return isTransitive;
	}
	
	// code for accessing a JwnlRelation's PointerType counterpart
	private WordNetRelation symmetricRelation;
	static {
		setSymmetric(ANTONYM, ANTONYM);
		setSymmetric(HYPERNYM, HYPONYM);
		setSymmetric(INSTANCE_HYPERNYM, INSTANCE_HYPONYM);
		setSymmetric(MEMBER_MERONYM, MEMBER_HOLONYM);
		setSymmetric(SUBSTANCE_MERONYM, SUBSTANCE_HOLONYM);
		setSymmetric(PART_MERONYM, PART_HOLONYM);
		setSymmetric(SIMILAR_TO, SIMILAR_TO);
		setSymmetric(ATTRIBUTE, ATTRIBUTE);
		setSymmetric(VERB_GROUP, VERB_GROUP);
		setSymmetric(REGION, REGION_MEMBER);
		setSymmetric(USAGE,  USAGE_MEMBER);
		setSymmetric(DERIVATIONALLY_RELATED, DERIVATIONALLY_RELATED);
		setSymmetric(DERIVED, DERIVED);
		setSymmetric(SYNONYM, SYNONYM);
		setSymmetric(STRICT_2ND_DEGREE_COUSIN, STRICT_2ND_DEGREE_COUSIN);
	}

	/** Set <var>a</var> as <var>b</var>'s symmetric type, and vice versa. */
	private static void setSymmetric(WordNetRelation a, WordNetRelation b) {
		a.symmetricRelation = b;
		b.symmetricRelation = a;
	}
	
	public WordNetRelation getSymmetricRelation()
	{
		return this.symmetricRelation;
	}
}
