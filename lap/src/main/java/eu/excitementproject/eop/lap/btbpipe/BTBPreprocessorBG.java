package eu.excitementproject.eop.lap.btbpipe;

import java.io.IOException;

import eu.excitementproject.eop.lap.LAPException;

/**
 * BTB pipeline preprocessor (tokenizer, sentence splitter, 
 * pos-tagger, lemmatizer)
 * 
 * @author Iliana Simova (BulTreeBank team)
 *
 */
public class BTBPreprocessorBG extends LAP_BTBPipe {

	/**
	 * Initialize the preprocessor
	 * 
	 * @throws LAPException
	 * @throws IOException 
	 */
	public BTBPreprocessorBG() throws LAPException, IOException {
		super();
	}
}