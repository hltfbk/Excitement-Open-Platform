package eu.excitementproject.eop.lap.dkpro;

import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;

/**
 * This class supports LAPAccess interface, with German tokenization, lemmatization 
 * and POS tagging
 * 
 * TreeTagger DKPro component supports both English & German. 
 * Reuse EN code, but just set the langID as DE. The Default Lang ID is needed when 
 * LAPAccess is used to generate a new pair of TE pairs from string inputs. 
 * 
 * @author Gil
 *
 */


public class TreeTaggerDE extends TreeTaggerEN implements LAPAccess {

	/**
	 * The constructor sets the language flag.
	 * 
	 * @throws LAPException
	 */
	public TreeTaggerDE() throws LAPException {
		super(); 
		languageIdentifier = "DE";
	}

}
