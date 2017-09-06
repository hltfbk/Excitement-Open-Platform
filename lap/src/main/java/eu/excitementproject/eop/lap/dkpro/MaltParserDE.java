package eu.excitementproject.eop.lap.dkpro;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.maltparser.MaltParser;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.core.treetagger.TreeTaggerPosTagger;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBaseAE;

/**
 * 
 * German tokenizer + TreeTagger + MaltParser (wrapped in DKPro component). This
 * class extends <code>MaltParserEN</code> and changes the
 * <code>languageIdentifier</code>.
 * 
 * For German, we have only the default model. (Model variant, "default" only)
 * 
 * @author Tae-Gil Noh 
 * 
 */

public class MaltParserDE extends LAP_ImplBaseAE {

	/**
	 * the default, simple constructor. Will generate default pipeline with
	 * default views and models.
	 * 
	 * @throws LAPException
	 */
	public MaltParserDE() throws LAPException {
		this(null); // default model 
	}

	/**
	 * constructor with parameter for the classifier.
	 * 
	 * @param modelVariant "variant string" for the model. This component provides only one variant called "default".
	 * @throws LAPException
	 */
	public MaltParserDE(String modelVariant) throws LAPException {
		AnalysisEngineDescription[] descArr = new AnalysisEngineDescription[3];
		
		try {
			descArr[0] = createPrimitiveDescription(OpenNlpSegmenter.class);
			descArr[1] = createPrimitiveDescription(TreeTaggerPosTagger.class);
			descArr[2] = createPrimitiveDescription(MaltParser.class,
					MaltParser.PARAM_VARIANT, modelVariant,
					MaltParser.PARAM_PRINT_TAGSET, true);
		} catch (ResourceInitializationException e) {
			throw new LAPException("Unable to create AE descriptions", e);
		}
		
		// b) call initializeViews() 
		initializeViews(descArr); 
		
		// c) set lang ID 
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
