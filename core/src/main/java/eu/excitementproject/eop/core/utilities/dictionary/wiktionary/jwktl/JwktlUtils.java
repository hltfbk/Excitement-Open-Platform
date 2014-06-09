/**
 * 
 */
package eu.excitementproject.eop.core.utilities.dictionary.wiktionary.jwktl;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import de.tudarmstadt.ukp.wiktionary.api.PartOfSpeech;
import de.tudarmstadt.ukp.wiktionary.api.RelationType;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionaryPartOfSpeech;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionaryRelation;


/**
 * A few static utils for the JWKTL wrapper package
 * @author Amnon Lotan
 * @since 21/06/2011
 * 
 */
public class JwktlUtils {

	private static final String ALSO = "also";
	private static final String SEE = "see";
	/**
	 * for identifying some technical prefixes like "Wikisaurus:" or "fr:"
	 */
	private static final char COLON = ':';
	
	/**
	 * covert a JWKTL part of speech to a {@link WiktionaryPartOfSpeech}
	 * @param partOfSpeech
	 * @return
	 */
	static WiktionaryPartOfSpeech toWiktionaryPartOfSpeech(de.tudarmstadt.ukp.wiktionary.api.PartOfSpeech partOfSpeech)
	{
		WiktionaryPartOfSpeech wktPOS;
		switch (partOfSpeech)
		{
		case ADJECTIVE:
		case NUMBER:
		case NUMERAL:
			wktPOS = WiktionaryPartOfSpeech.ADJECTIVE;
			break;
		case ADVERB:
		case INTERROGATIVE_ADVERB:
			wktPOS = WiktionaryPartOfSpeech.ADVERB;
			break;
		case AUXILIARY_VERB:
		case PARTICIPLE:
		case VERB:
			wktPOS = WiktionaryPartOfSpeech.VERB;
			break;
		case CONJUNCTION:
		case PREPOSITION:
			wktPOS = WiktionaryPartOfSpeech.PREPOSITION;
			break;
		case DETERMINER:
			wktPOS = WiktionaryPartOfSpeech.DETERMINER;
			break;
		case FIRST_NAME:
		case NOUN:
		case NOUN_PHRASE:
		case PROPER_NOUN:
			wktPOS = WiktionaryPartOfSpeech.NOUN;
			break;
		case INDEFINITE_PRONOUN:
		case PRONOUN:
		case INTERROGATIVE_PRONOUN:
		case PERSONAL_PRONOUN:
		case REFLEXIVE_PRONOUN:
		case RELATIVE_PRONOUN:
			wktPOS = WiktionaryPartOfSpeech.PRONOUN;
			break;
		case PUNCTUATION_MARK:
			wktPOS = WiktionaryPartOfSpeech.PUNCTUATION;
			break;
		default:
			wktPOS = WiktionaryPartOfSpeech.OTHER;
		}
		return wktPOS;
	}
	
	/**
	 * Translate from {@link WiktionaryPartOfSpeech} to its salient counterpart in {@link de.tudarmstadt.ukp.wiktionary.api.PartOfSpeech}.<br>
	 * <b>Note that since there are many {@link PartOfSpeech}s for each {@link WiktionaryPartOfSpeech}, you may get incorrect results using this.</b>
	 * @param partOfSpeech
	 * @return
	 */
	static de.tudarmstadt.ukp.wiktionary.api.PartOfSpeech toJwktlPartOfSpeech(WiktionaryPartOfSpeech partOfSpeech) {
		switch (partOfSpeech)
		{
			case ADJECTIVE:
				return PartOfSpeech.ADJECTIVE;
			case ADVERB:
				return PartOfSpeech.ADVERB;
			case DETERMINER:
				return PartOfSpeech.DETERMINER;
			case NOUN:
				return PartOfSpeech.NOUN;
			case PREPOSITION:
				return PartOfSpeech.PREPOSITION;
			case PRONOUN:
				return  PartOfSpeech.PRONOUN;
			case PUNCTUATION:
				return PartOfSpeech.PUNCTUATION_MARK;
			case VERB:
				return  PartOfSpeech.VERB;
			case OTHER:
			default:
				return PartOfSpeech.UNSPECIFIED;
		}
	}

	/**
	 * clean the words: remove technical prefixes that end with ':' (like "wikisaurus:")
	 * @param wordsList
	 * @return
	 */
	public static List<String> cleanRelatedWords(List<String> wordsList) {
		// screen for all the "see also"s you see in word lists
		if (wordsList.contains(SEE) && wordsList.contains(ALSO))
		{
			wordsList.remove(SEE);
			wordsList.remove(ALSO);
		}
		for( ListIterator<String> iter = wordsList.listIterator(); iter.hasNext(); )
		{
			String word = iter.next();
			int index = -1;
			if (0 <= (index = word.lastIndexOf(COLON)))
			{
				iter.remove();
				iter.add(word.substring(index+1));
			}
		}
		return new Vector<String>(new HashSet<String>(wordsList));	// use a set to eliminate duplicates
	}
	
	/**
	 * Convert a {@link WiktionaryRelation} to a  JWKTL {@link RelationType}. Throws JwktlException if the given relation isn't supported. All should be, though. 
	 * @param wktRelation
	 * @return
	 * @throws JwktlException if the given relation isn't supported. 
	 */
	static de.tudarmstadt.ukp.wiktionary.api.RelationType wktRelationToJwktlRelaton( WiktionaryRelation wktRelation) throws JwktlException
	{
		if (wktRelation == null)
			throw new JwktlException("got null wiktionary relation");
		
		switch (wktRelation)
		{
		/**
		 * Each listed antonym denotes the opposite of this entry. 
		 */
		case ANTONYM:
			return (RelationType.ANTONYM);    
		case CHARACTERISTIC_WORD_COMBINATION:
			return (RelationType.CHARACTERISTIC_WORD_COMBINATION);
			
		/**
		 * Each listed coordinate term shares a hypernym with this entry.	
		 */
		case COORDINATE_TERM :
			return (RelationType.COORDINATE_TERM);	           
		/**
		 * Terms derived from this entry
		 */
		case DERIVED_TERM :
			return (RelationType.DERIVED_TERM);	           
		/**
		 * List terms in other languages that have borrowed or inherited the word. The etymology of these terms should then link back to the page. 
		 */
		case DESCENDANT :
			return (RelationType.DESCENDANT);	           
		case ETYMOLOGICALLY_RELATED_TERM:
			return (RelationType.ETYMOLOGICALLY_RELATED_TERM);	           
		/**
		 * Each listed holomym has this entrys referent as a part of itself; this entrys referent is part of that of each listed holonym.	
		 */
		case HOLONYM :
			return (RelationType.HOLONYM);	           
		/**
		 * Each listed hypernym is superordinate to this entry; This entrys referent is a kind of that denoted by listed hypernym.	
		 */
		case HYPERNYM :
			return (RelationType.HYPERNYM);	           
		/**
		 * Each listed hyponym is subordinate to this entry; Each listed hyponyms referent is a kind of that denoted by this entry.	 
		 */
		case HYPONYM :
			return (RelationType.HYPONYM);	           
		/**
		 * Each listed meronym denotes part of this entrys referent.	
		 */
		case MERONYM :
			return (RelationType.MERONYM);	           
		/**
		 * Each listed otherwise related term semantically relates to this entry.	
		 */
		case SEE_ALSO :
			return (RelationType.SEE_ALSO);	           
		/**
		 * Each listed synonym denotes the same as this entry.
		 */
		case SYNONYM :
			return (RelationType.SYNONYM);	           
		/**
		 * Each listed troponym denotes a particular way to do this entrys referent. Like a verb's hyponym. 
		 * A word that denotes a manner of doing something "`march' is a troponym of `walk'"	
		 */
		case TROPONYM :
			return (RelationType.TROPONYM); 
		
		/**
		 * words entailed by the sense's gloss
		 */
		case GLOSS_TERMS:
			return (null);
			
			default:
				throw new JwktlException("unhandled WiktionaryRelation: " + wktRelation);
		}
		
	}

	/**
	 * Convert a JWKTL {@link RelationType} to a {@link WiktionaryRelation}. Throws IllegalArgumentException - 
	 * if the specified relationType has no constant with the specified name, or the specified class object does not represent an enum type

	 * @param relationType
	 * @return
	 */
	static WiktionaryRelation jwktlToWiktionaryRelation(RelationType relationType)
	{
		return WiktionaryRelation.valueOf(relationType.name());
	}
}
