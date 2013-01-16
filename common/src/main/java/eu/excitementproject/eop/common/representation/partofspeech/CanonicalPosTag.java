package eu.excitementproject.eop.common.representation.partofspeech;


/**
 * <P> [DELETEME_LATER: Imported from BIUTEE with a modification - types are changed to adopted UIMA POS Type] </P> 
 * 
 * Enumeration of a small set of part-of-speech tags.
 * <B>
 * This enumeration should not be used as is! The programmer should always use
 * the {@link PartOfSpeech} class to represent part-of-speech. The {@link CanonicalPosTag} is
 * used internally by the {@link PartOfSpeech}, but all tools implementations, and other code
 * that uses part-of-speech should not use explicitly the {@link CanonicalPosTag}.
 * </B> 
 * 
 * @author Gil  
 * @since 
 */
public enum CanonicalPosTag
{
	ADJ,  // Adjective
	ADV,  // Adverb 
	ART,  // Article
	CARD, // Cardinal number 
	CONJ, // Conjunction 
	N,    // Noun
	NN,   // Common Noun (is-a N) 
	NP,   // Proper Noun (is-a N)  
	O,    // Exclamation
	PP,   // Preposition
	PR,   // Pronoun
	PUNC, // Punctuation 
	V,    // Verb and verb phrase	
	OTHER; 	// this OTHER, is not from UIMA type, inserted here, since PartOfSpeech interface uses this "OTHER" value.  
}


