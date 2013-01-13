package eu.excitementproject.eop.core.utilities.dictionary.wordnet.jwi;
import edu.mit.jwi.item.Pointer;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetPartOfSpeech;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;


/**
 * A set of utilities for converting between generic wordnet relation types and parts of speech and their JWI counterparts.
 * @author Amnon Lotan
 *
 */
public class JwiUtils
{
	
	static edu.mit.jwi.item.POS getJwiPartOfSpeec(WordNetPartOfSpeech partOfSpeech)
	{
		switch(partOfSpeech)
		{
		case ADJECTIVE:
			return edu.mit.jwi.item.POS.ADJECTIVE;
		case ADVERB:
			return edu.mit.jwi.item.POS.ADVERB;
		case NOUN:
			return edu.mit.jwi.item.POS.NOUN;
		case VERB:
			return edu.mit.jwi.item.POS.VERB;
		default:
			return null;
		}
	}
	
	static WordNetPartOfSpeech getWordNetPartOfSpeech(edu.mit.jwi.item.POS pos)
	{
		if (null==pos)
			return null;
		if (edu.mit.jwi.item.POS.ADJECTIVE == pos)
			return WordNetPartOfSpeech.ADJECTIVE;
		else if (edu.mit.jwi.item.POS.ADVERB == pos)
			return WordNetPartOfSpeech.ADVERB;
		else if (edu.mit.jwi.item.POS.NOUN == pos)
			return WordNetPartOfSpeech.NOUN;
		else if (edu.mit.jwi.item.POS.VERB == pos)
			return WordNetPartOfSpeech.VERB;
		
		return null;
	}

	static Pointer wordNetRelationToPointer( WordNetRelation relation) throws WordNetException
	{
		switch( relation)
		{
		case SYNONYM:
			return null;	// Synonyms are the only relation that returns null
		case ANTONYM:
			return Pointer.ANTONYM;
		case REGION:	
			return (Pointer.REGION); 
		case USAGE :
			return 	(Pointer.USAGE); 

		// Nouns and Verbs

		case HYPERNYM:
			return  	(Pointer.HYPERNYM); 
		case HYPONYM :
			return 	(Pointer.HYPONYM); 
		/**
		 * AKA "DERIVED" or "Derived forms" in other implementations. used for nouns and verbs.
		 */
		case DERIVATIONALLY_RELATED :	
			return (Pointer.DERIVATIONALLY_RELATED); 
		case INSTANCE_HYPERNYM:
			return  	(Pointer.HYPERNYM_INSTANCE); 
		case INSTANCE_HYPONYM :
			return 	(Pointer.HYPONYM_INSTANCE); 

		// Nouns and Adjectives

		case ATTRIBUTE :
			return 	(Pointer.ATTRIBUTE); 
		case SEE_ALSO: 	
			return (Pointer.ALSO_SEE); 

		// Nouns

		case MEMBER_HOLONYM :
			return 	(Pointer.HOLONYM_MEMBER); 
		case SUBSTANCE_HOLONYM: 	
			return (Pointer.HOLONYM_SUBSTANCE); 
		case PART_HOLONYM :	
			return (Pointer.HOLONYM_PART); 
		case MEMBER_MERONYM:
			return  	(Pointer.MERONYM_MEMBER); 
		case SUBSTANCE_MERONYM: 	
			return (Pointer.MERONYM_SUBSTANCE); 
		case PART_MERONYM: 	
			return (Pointer.MERONYM_PART); 
		case CATEGORY_MEMBER:
			return  null;	// not implemented 
		case REGION_MEMBER:
			return 	(Pointer.REGION_MEMBER);  
		case USAGE_MEMBER:	
			return (Pointer.USAGE_MEMBER); 

		// Verbs

		case ENTAILMENT	:
			return (Pointer.ENTAILMENT); 
		case CAUSE:	
			return (Pointer.CAUSE);  
		case VERB_GROUP:	
			return (Pointer.VERB_GROUP);
		case TROPONYM:
			throw new WordNetException("TROPONYM is not implemented.");
			// return null;	// not implemented

		// Adjectives
			
		case SIMILAR_TO	:
			return (Pointer.SIMILAR_TO);  
		case PARTICIPLE_OF:
			return 	(Pointer.PARTICIPLE); 
		case PERTAINYM:
			return 	(Pointer.PERTAINYM); 

		// Adverbs
			
		case DERIVED:
			return (Pointer.DERIVED_FROM_ADJ);
			
		default:
			return null;	// for COUSIN and other relations that have no real JWI counterpart
//			throw new WordNetException("Not a JWNL Pointer: " + relation);
		}
	}
}
