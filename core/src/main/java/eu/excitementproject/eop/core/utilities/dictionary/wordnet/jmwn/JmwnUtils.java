package eu.excitementproject.eop.core.utilities.dictionary.wordnet.jmwn;

import org.itc.mwn.POS;
import org.itc.mwn.PointerType;

import eu.excitementproject.eop.common.representation.partofspeech.ByCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetPartOfSpeech;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;


/**
 * 
 * parallel to JwnlUtils.java
 * 
 * @author nastase
 *
 */

public class JmwnUtils {
	
	public static POS getJmwnPartOfSpeech(PartOfSpeech abstractPos) {
		switch (abstractPos.getCanonicalPosTag()) {
		case ADJ:
			return POS.ADJ;
		case ADV:
			return POS.ADV;
		case N:
			return POS.NOUN;
		case V:
			return POS.VERB;
		default:
			return null;
		}
	}

	public static POS getJmwnPartOfSpeech(WordNetPartOfSpeech wnPos) {
		switch (wnPos) {
		case ADJECTIVE:
			return POS.ADJ;
		case ADVERB:
			return POS.ADV;
		case NOUN:
			return POS.NOUN;
		case VERB:
			return POS.VERB;
		default:
			return null;
		}
	}

	
	public static ByCanonicalPartOfSpeech getUnspecifiedPartOfSpeech(POS jmwnPos) throws WordNetException{
	 try {
		if (jmwnPos.equals(POS.ADJ)) {
			return new ByCanonicalPartOfSpeech(CanonicalPosTag.ADJ.name());
		} else if (jmwnPos.equals(POS.ADV)) {
			return new ByCanonicalPartOfSpeech(CanonicalPosTag.ADV.name());
		} else if (jmwnPos.equals(POS.NOUN)) {
			return new ByCanonicalPartOfSpeech(CanonicalPosTag.N.name());
		} else if (jmwnPos.equals(POS.VERB)) {
			return new ByCanonicalPartOfSpeech(CanonicalPosTag.V.name());
		} else {
			return null;
		}
	 } catch (UnsupportedPosTagStringException e) {
			throw new WordNetException("Internal bug! this value WordNetPartOfSpeech." + jmwnPos.getLabel() +" isn't a CanonicalPosTag");
		}
	}

	public static WordNetPartOfSpeech getWordNetPartOfSpeech(POS jmwnPos) throws WordNetException{
		 try {
			if (jmwnPos.equals(POS.ADJ)) {
				return WordNetPartOfSpeech.ADJECTIVE;
			} else if (jmwnPos.equals(POS.ADV)) {
				return WordNetPartOfSpeech.ADVERB;
			} else if (jmwnPos.equals(POS.NOUN)) {
				return WordNetPartOfSpeech.NOUN;
			} else if (jmwnPos.equals(POS.VERB)) {
				return WordNetPartOfSpeech.VERB;
			} else {
				return null;
			}
		 } catch (Exception e) {
				throw new WordNetException("Internal bug! this value WordNetPartOfSpeech." + jmwnPos.getLabel() +" isn't a CanonicalPosTag");
			}
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
			return null; //(PointerType.REGION); 
		case USAGE :
			return 	null; //(PointerType.USAGE); 
	
		// Nouns and Verbs
	
		case HYPERNYM:
			return  	(PointerType.HYPERNYM); 
		case HYPONYM :
			return 	(PointerType.HYPONYM); 
		/**
		 * AKA "DERIVED" or "Derived forms" in other implementations. used for nouns and verbs.
		 */
		case DERIVATIONALLY_RELATED :	
			return  PointerType.DERIVED;	//(PointerType.NOMINALIZATION); 
		case INSTANCE_HYPERNYM:
			return  PointerType.HYPERNYM;	//(PointerType.INSTANCE_HYPERNYM); 
		case INSTANCE_HYPONYM :
			return  PointerType.HYPONYM;	//(PointerType.INSTANCES_HYPONYM); 
	
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
			return  null;	// not implemented 
		case REGION_MEMBER:
			return null;	//(PointerType.REGION_MEMBER);  
		case USAGE_MEMBER:	
			return null;	//(PointerType.USAGE_MEMBER); 
	
		// Verbs
	
		case ENTAILMENT	:
			return (PointerType.ENTAILMENT); 
		case CAUSE:	
			return (PointerType.CAUSE);  
		case VERB_GROUP:	
			return (PointerType.VERB_GROUP);
		case TROPONYM:
			return null;	// not implemented
	
		// Adjectives
		
		case SIMILAR_TO	:
			return (PointerType.SIMILAR_TO);  
		case PARTICIPLE_OF:
			return 	(PointerType.PARTICIPLE_OF); 
		case PERTAINYM:
			return 	(PointerType.PERTAINYM); 
		
		// Adverbs
			
		case DERIVED:
			return null;	// not implemented

		default:
			throw new WordNetException("Internal bug: this method lacks a clause for this WordNetRelation: " + relation);
		}
	}
	
	
}
