package ac.biu.nlp.nlp.representation;

/**
 * Enumeration of a small set of part-of-speech tags.
 * <B>
 * This enumeration should not be used as is! The programmer should always use
 * the {@link PartOfSpeech} class to represent part-of-speech. The {@link CanonicalPosTag} is
 * used internally by the {@link PartOfSpeech}, but all tools implementations, and other code
 * that uses part-of-speech should not use explicitly the {@link CanonicalPosTag}.
 * </B> 
 * 
 * 
 * @author Asher Stern
 * @since Dec 26, 2010
 *
 */
public enum CanonicalPosTag
{
	NOUN,
	VERB,
	ADJECTIVE,
	ADVERB,
	PREPOSITION,
	DETERMINER,
	PRONOUN,
	PUNCTUATION,
	OTHER;
}


