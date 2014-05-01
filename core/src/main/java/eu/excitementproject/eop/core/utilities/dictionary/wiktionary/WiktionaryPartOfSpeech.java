package eu.excitementproject.eop.core.utilities.dictionary.wiktionary;

import eu.excitementproject.eop.common.representation.partofspeech.BySimplerCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;

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
	
	
	public BySimplerCanonicalPartOfSpeech toPartOfSpeech() throws WiktionaryException
	{
		try {
			return new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.valueOf(this.name()));
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
		switch (SimplerPosTagConvertor.simplerPos(partOfSpeech.getCanonicalPosTag()))
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
