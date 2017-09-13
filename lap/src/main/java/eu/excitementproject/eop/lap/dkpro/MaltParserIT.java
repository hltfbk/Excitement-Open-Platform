package eu.excitementproject.eop.lap.dkpro;

import de.tudarmstadt.ukp.dkpro.core.maltparser.MaltParser;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.core.treetagger.TreeTaggerPosTagger;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBaseAE;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

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
			descArr[0] = createEngineDescription(OpenNlpSegmenter.class);
			descArr[1] = createEngineDescription(TreeTaggerPosTagger.class);
			descArr[2] = createEngineDescription(MaltParser.class,
					MaltParser.PARAM_VARIANT, modelVariant,
					MaltParser.PARAM_PRINT_TAGSET, true,
					MaltParser.PARAM_IGNORE_MISSING_FEATURES, true);
		} catch (ResourceInitializationException e) {
			throw new LAPException("Unable to create AE descriptions", e);
		}
		
		// b) call initializeViews() 
		initializeViews(descArr); 
		
		// c) set lang ID 
		languageIdentifier = "IT";
	}
	
}
