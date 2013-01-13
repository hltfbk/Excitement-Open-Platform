package eu.excitementproject.eop.core.utilities.dictionary.wordnet.jwnl;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetPartOfSpeech;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.PointerType;

/**
 * 
 * @author Asher Stern
 *
 */
public class JwnlUtils
{
	
	static POS getJwnlPartOfSpeec(WordNetPartOfSpeech partOfSpeech)
	{
		switch(partOfSpeech)
		{
		case ADJECTIVE:
			return POS.ADJECTIVE;
		case ADVERB:
			return POS.ADVERB;
		case NOUN:
			return POS.NOUN;
		case VERB:
			return POS.VERB;
		default:
			return null;
		}
	}
	
	static WordNetPartOfSpeech getWordNetPartOfSpeech(POS pos)
	{
		if (null==pos)
			return null;
		if (POS.ADJECTIVE == pos)
			return WordNetPartOfSpeech.ADJECTIVE;
		else if (POS.ADVERB == pos)
			return WordNetPartOfSpeech.ADVERB;
		else if (POS.NOUN == pos)
			return WordNetPartOfSpeech.NOUN;
		else if (POS.VERB == pos)
			return WordNetPartOfSpeech.VERB;
		
		return null;
	}

	/**
	 * Return the Jwnl {@link PointerType} matching the given {@link WordNetRelation}. Notice that some {@link WordNetRelation}s have no implementation in Jwnl, and return 
	 * null.
	 * 
	 * @param relation
	 * @return
	 * @throws WordNetException
	 */
	static PointerType wordNetRelationToPointerType( WordNetRelation relation) throws WordNetException
	{
		switch( relation)
		{
		case SYNONYM:
			return null;	// Synonyms are the only relation that returns null
		case ANTONYM:
			return PointerType.ANTONYM;
		case REGION:	
			return (PointerType.REGION); 
		case USAGE :
			return 	(PointerType.USAGE); 
	
		// Nouns and Verbs
	
		case HYPERNYM:
			return  	(PointerType.HYPERNYM); 
		case HYPONYM :
			return 	(PointerType.HYPONYM); 
		/**
		 * AKA "DERIVED" or "Derived forms" in other implementations. used for nouns and verbs.
		 */
		case DERIVATIONALLY_RELATED :	
			return (PointerType.NOMINALIZATION); 
		case INSTANCE_HYPERNYM:
			return  	(PointerType.INSTANCE_HYPERNYM); 
		case INSTANCE_HYPONYM :
			return 	(PointerType.INSTANCES_HYPONYM); 
	
		// Nouns and Adjectives
	
		case ATTRIBUTE :
			return 	(PointerType.ATTRIBUTE); 
		case SEE_ALSO: 	
			return (PointerType.SEE_ALSO); 
	
		// Nouns
	
		case MEMBER_HOLONYM :
			return 	(PointerType.MEMBER_HOLONYM); 
		case SUBSTANCE_HOLONYM: 	
			return (PointerType.SUBSTANCE_HOLONYM); 
		case PART_HOLONYM :	
			return (PointerType.PART_HOLONYM); 
		case MEMBER_MERONYM:
			return  	(PointerType.MEMBER_MERONYM); 
		case SUBSTANCE_MERONYM: 	
			return (PointerType.SUBSTANCE_MERONYM); 
		case PART_MERONYM: 	
			return (PointerType.PART_MERONYM); 
		case CATEGORY_MEMBER:
			throw new WordNetException("CATEGORY_MEMBER is not implemented.");
			// return  null;	// not implemented 
		case REGION_MEMBER:
			return 	(PointerType.REGION_MEMBER);  
		case USAGE_MEMBER:	
			return (PointerType.USAGE_MEMBER); 
	
		// Verbs
	
		case ENTAILMENT	:
			return (PointerType.ENTAILMENT); 
		case CAUSE:	
			return (PointerType.CAUSE);  
		case VERB_GROUP:	
			return (PointerType.VERB_GROUP);
		case TROPONYM:
			throw new WordNetException("TROPONYM is not implemented.");
			// return null;	// not implemented
	
		// Adjectives
		
		case SIMILAR_TO	:
			return (PointerType.SIMILAR_TO);  
		case PARTICIPLE_OF:
			return 	(PointerType.PARTICIPLE_OF); 
		case PERTAINYM:
			return 	(PointerType.PERTAINYM); 
		
		// Adverbs
			
		case DERIVED:
			throw new WordNetException("DERIVED is not implemented.");
			// return null;	// not implemented

		default:
			throw new WordNetException("Internal bug: this method lacks a clause for this WordNetRelation: " + relation);
		}
	}

}
