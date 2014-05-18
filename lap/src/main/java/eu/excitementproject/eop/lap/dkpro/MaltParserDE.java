package eu.excitementproject.eop.lap.dkpro;

import eu.excitementproject.eop.lap.LAPException;

/**
 * 
 * German tokenizer + TreeTagger + MaltParser (wrapped in DKPro component). This
 * class extends <code>MaltParserEN</code> and changes the
 * <code>languageIdentifier</code>.
 * 
 * TODO: train more models for parsing German; for the moment, only the default model is available.
 * 
 * @author Rui
 * 
 */

public class MaltParserDE extends MaltParserEN {

	/**
	 * the default, simple constructor. Will generate default pipeline with
	 * default views and models.
	 * 
	 * @throws LAPException
	 */
	public MaltParserDE() throws LAPException {
		super();
		languageIdentifier = "DE";
	}

	/**
	 * constructor with parameter for the classifier.
	 * 
	 * @param listAEDescriptorsArgs
	 *            the parameter for the underlying AEs. This pipeline only has
	 *            one argument PARSER_MODEL_VARIANT. Null means all default
	 *            variable (default model) Note that passing unknown model will
	 *            raise EXCEPTION from the UIMA AE. Note that there is no other
	 *            models than the default one available for German for the
	 *            moment!
	 * @throws LAPException
	 */
	public MaltParserDE(String modelVariant) throws LAPException {
		super(modelVariant);
		languageIdentifier = "DE";
	}

//	/**
//	 * constructor with parameter for the classifier and view names.
//	 * 
//	 * @param listAEDescriptorsArgs
//	 *            the parameter for the underlying AEs. This pipeline only has
//	 *            one argument PARSER_MODEL_VARIANT. Null means all default
//	 *            variable (default model) Note that passing unknown model will
//	 *            raise EXCEPTION from the UIMA AE. Note that there is no other
//	 *            models than the default one available for German for the
//	 *            moment!
//	 * @param views
//	 * @throws LAPException
//	 */
//	public MaltParserDE(String[] views,
//			Map<String, String> listAEDescriptorsArgs) throws LAPException {
//		super(views, listAEDescriptorsArgs);
//		languageIdentifier = "DE";
//	}
}
