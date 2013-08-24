/**
 * 
 */
package eu.excitementproject.eop.lap.dkpro;

import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;

/**
 * This class supports LAPAccess interface, via openNLP tokenizer and pos tagger. 
 * 
 * The class simply changes language ID (which will be added to CAS), and the underlying 
 * AE properly loads Italian model. (Italian model also added in LAP POM) 
 * 
 * @author Tae-Gil Noh 
 * @since August 23, 2013 
 *
 */
public class OpenNLPTaggerIT extends OpenNLPTaggerEN implements LAPAccess {

	/**
	 * The constructor only sets/overrides the language flag.
	 * 
	 * @throws LAPException
	 */
	public OpenNLPTaggerIT() throws LAPException {
		super(); 
		languageIdentifier = "IT"; 
	}

}
