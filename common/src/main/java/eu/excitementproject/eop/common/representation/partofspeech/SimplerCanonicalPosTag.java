package eu.excitementproject.eop.common.representation.partofspeech;

/**
 * A simple list of canonical part-of-speech tags.
 * This list is the original list used in BIU infrastructure.
 * 
 * See also {@link SimplerPosTagConvertor}
 * 
 * @see CanonicalPosTag
 * @see SimplerPosTagConvertor
 * 
 * @author Asher Stern
 * @since Jan 13, 2013
 *
 */
public enum SimplerCanonicalPosTag
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
