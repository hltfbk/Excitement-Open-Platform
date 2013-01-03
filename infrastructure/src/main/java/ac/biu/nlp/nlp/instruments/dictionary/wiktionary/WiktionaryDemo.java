/**
 * 
 */
package ac.biu.nlp.nlp.instruments.dictionary.wiktionary;

import java.util.List;

import ac.biu.nlp.nlp.general.immutable.ImmutableList;
import ac.biu.nlp.nlp.instruments.dictionary.wiktionary.jwktl.JwktlDictionary;
import ac.biu.nlp.nlp.representation.CanonicalPosTag;
import ac.biu.nlp.nlp.representation.UnspecifiedPartOfSpeech;
import ac.biu.nlp.nlp.representation.UnsupportedPosTagStringException;

/**
 * @author Amnon Lotan
 * @since 25/06/2011
 * 
 */
public class WiktionaryDemo {

	/**
	 * @param args
	 * @throws UnsupportedPosTagStringException 
	 * @throws SensedException 
	 */
	public static void main(String[] args) throws WiktionaryException, UnsupportedPosTagStringException {
		WiktionaryDictionary wiktionary = new JwktlDictionary("D:/data/RESOURCES/Wiktionary/parse", "b:/jars/stanford-postagger-2008-09-28/bidirectional-wsj-0-18.tagger" );
		
		String lemma = "dog";
		
		WiktionaryPartOfSpeech wktPos = WiktionaryPartOfSpeech.toWiktionaryPartOfspeech(new UnspecifiedPartOfSpeech( CanonicalPosTag.NOUN));
		List<WiktionarySense> senses = wiktionary.getSortedSensesOf(lemma, wktPos);
//		for (List<WktSense> senseList : senses.values())
		{
			for (WiktionarySense sense : senses)
//			WktSense sense = senses.get(7);
			{
				System.out.println("See Also: " + sense.getRelatedWords(WiktionaryRelation.SEE_ALSO).getMutableCollectionCopy());
				System.out.println("Synonyms: " + sense.getRelatedWords(WiktionaryRelation.SYNONYM).getMutableCollectionCopy());
				System.out.println("Gloss: " + sense.getGloss());
				System.out.println("Parsed gloss terms: " + sense.getRelatedWords(WiktionaryRelation.GLOSS_TERMS).getMutableCollectionCopy());
				ImmutableList<String> hypernyms = sense.getRelatedWords(WiktionaryRelation.HYPERNYM);
				System.out.println("Hypernyms: " + hypernyms.getMutableCollectionCopy());
				System.out.println("\n***********************************************************\n");
			}
			
		}
			 
		
			

	}

}
