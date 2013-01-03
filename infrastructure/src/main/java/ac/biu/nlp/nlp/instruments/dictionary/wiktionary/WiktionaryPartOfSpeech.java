/**
 * 
 */
package ac.biu.nlp.nlp.instruments.dictionary.wiktionary;

import ac.biu.nlp.nlp.representation.CanonicalPosTag;
import ac.biu.nlp.nlp.representation.PartOfSpeech;
import ac.biu.nlp.nlp.representation.UnspecifiedPartOfSpeech;
import ac.biu.nlp.nlp.representation.UnsupportedPosTagStringException;

/**
 * Represents all the parts of speech used in Wiktionary, and some conversion methods.
 * @author Amnon Lotan
 * @since Jun 22, 2011
 * 
 */
public enum WiktionaryPartOfSpeech 
{
	ADJECTIVE,
	ADVERB,
	NOUN,
	VERB,
	DETERMINER,
	PREPOSITION,
	PRONOUN,
	PUNCTUATION,
	OTHER;
	
	
	public UnspecifiedPartOfSpeech toPartOfSpeech() throws WiktionaryException
	{
		try {
			return new UnspecifiedPartOfSpeech(CanonicalPosTag.valueOf(this.name()));
		} catch (UnsupportedPosTagStringException e) {
			throw new WiktionaryException("Internal bug! this value WordNetPartOfSpeech."+this.name()+" isn't a CanonicalPosTag");
		}
	}
	
	/**
	 * Returns the wiktionary equivalent of the given PartOfSpeech. 
	 * 
	 * @param partOfSpeech
	 * @return
	 * @throws WiktionaryException 
	 */
	public static WiktionaryPartOfSpeech toWiktionaryPartOfspeech(PartOfSpeech partOfSpeech) throws WiktionaryException 
	{
		switch (partOfSpeech.getCanonicalPosTag())
		{
			case ADJECTIVE:
				return ADJECTIVE;
			case ADVERB:
				return ADVERB;
			case NOUN:
				return NOUN;
			case VERB:
				return VERB;
			case DETERMINER:
				return WiktionaryPartOfSpeech.DETERMINER;
			case PREPOSITION:
				return PREPOSITION;
			case PRONOUN:
				return PRONOUN;
			case PUNCTUATION:
				return PUNCTUATION;
			case OTHER:
				return OTHER;
			default:
				throw new WiktionaryException("Internal error! I do not recognize the CanonicalPosTag of this part of speech: " + partOfSpeech );
		}
	}
}
