package eu.excitementproject.eop.lap.dkpro;

import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;

/**
 * This class supports LAPAccess interface, with German tokenization and tagging
 * 
 * OpenNLPTagger support both English & German. 
 * Reuse EN code, but just set the langID as DE. 
 * 
 * @author Gil
 *
 */
public class OpenNLPTaggerDE extends OpenNLPTaggerEN implements LAPAccess {

	public OpenNLPTaggerDE() throws LAPException {
		super(); 
		languageIdentifier = "DE"; // setting language identifier 
	}

}
