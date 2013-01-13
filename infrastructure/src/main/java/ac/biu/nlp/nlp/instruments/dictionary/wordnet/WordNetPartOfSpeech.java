package ac.biu.nlp.nlp.instruments.dictionary.wordnet;

import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnspecifiedPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;

public enum WordNetPartOfSpeech
{
	ADJECTIVE,
	ADVERB,
	NOUN,
	VERB;
	
	
	/**
	 * Returns the wordnet equivalent of the given PartOfSpeech. If it isn't adjective, adverb, noun or verb, it returns <code>null</code>. 
	 * 
	 * @param abstractPos
	 * @return
	 * @throws WordNetException
	 */
	public static WordNetPartOfSpeech toWordNetPartOfspeech(PartOfSpeech abstractPos)
	{
		switch (abstractPos.getCanonicalPosTag())
		{
			case ADJECTIVE:
				return ADJECTIVE;
			case ADVERB:
				return ADVERB;
			case NOUN:
				return NOUN;
			case VERB:
				return VERB;
			default:
				return null;
		}
	}
	
	public UnspecifiedPartOfSpeech toPartOfSpeech() throws WordNetException
	{
		try {
			return new UnspecifiedPartOfSpeech(CanonicalPosTag.valueOf(this.name()));
		} catch (UnsupportedPosTagStringException e) {
			throw new WordNetException("Internal bug! this value WordNetPartOfSpeech."+this.name()+" isn't a CanonicalPosTag");
		}
	}
	
}
