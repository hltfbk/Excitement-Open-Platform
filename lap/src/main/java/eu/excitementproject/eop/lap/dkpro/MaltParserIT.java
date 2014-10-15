package eu.excitementproject.eop.lap.dkpro;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.maltparser.MaltParser;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.core.treetagger.TreeTaggerPosLemmaTT4J;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBaseAE;

/**
 * 
 * Italian tokenizer + TreeTagger + MaltParser (wrapped in DKPro component). 
 * 
 * For Italian we have only the default model. (Model variant, "default" only)
 * 
 * @author Tae-Gil Noh 
 * 
 */

public class MaltParserIT extends LAP_ImplBaseAE {

	/**
	 * the default, simple constructor. Will generate default pipeline with
	 * default views and models.
	 * 
	 * @throws LAPException
	 */
	public MaltParserIT() throws LAPException {
		this(null); // default model 
	}

	/**
	 * constructor with parameter for the classifier.
	 * 
	 * @param modelVariant "variant string" for the model. 
	 * @throws LAPException
	 */
	public MaltParserIT(String modelVariant) throws LAPException {
		AnalysisEngineDescription[] descArr = new AnalysisEngineDescription[3];
		
		try {
			descArr[0] = createPrimitiveDescription(OpenNlpSegmenter.class);
			descArr[1] = createPrimitiveDescription(TreeTaggerPosLemmaTT4J.class);
			descArr[2] = createPrimitiveDescription(MaltParser.class,
					MaltParser.PARAM_VARIANT, modelVariant,
					MaltParser.PARAM_PRINT_TAGSET, true);
		} catch (ResourceInitializationException e) {
			throw new LAPException("Unable to create AE descriptions", e);
		}
		
		// b) call initializeViews() 
		initializeViews(descArr); 
		
		// c) set lang ID 
		languageIdentifier = "IT";
	}
	
}
