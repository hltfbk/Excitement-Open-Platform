package eu.excitementproject.eop.lap.dkpro;

import eu.excitementproject.eop.lap.LAPException;

/**
 * 
 * Italian tokenizer + tagger + lemmatizer, that relies on TreeTagger 
 * (wrapped in DKPro component)  
 * 
 * TreeTagger DKPro component also supports Italian, with Italian model. 
 * The class reuses EN code, but just set the langID as IT. 
 *  
 * @author Gil 
 *
 */

public class TreeTaggerIT extends TreeTaggerEN {

	/**
	 * The constructor sets the language flag.
	 * 
	 * @throws LAPException
	 */
	public TreeTaggerIT() throws LAPException {
		super(); 
		languageIdentifier = "IT";
	}

}
