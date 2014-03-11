/**
 * This package contains all the LAP components already existing in dkpro and they extend <code>LAP_ImplBaseAE</code>.
 */
package eu.excitementproject.eop.lap.dkpro;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.maltparser.MaltParser;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.tudarmstadt.ukp.dkpro.core.treetagger.TreeTaggerPosLemmaTT4J;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBaseAE;

/**
 * 
 * English tokenizer + TreeTagger + MaltParser (wrapped in DKPro component).
 * This class is provides all LAPAccess methods simply by overriding
 * addAnnotationTo() of LAP_ImplBase.
 * 
 * @author Rui
 * 
 */

public class MaltParserEN extends LAP_ImplBaseAE {

	/**
	 * the default, simple constructor. Will generate default pipeline with default views and models. 
	 * 
	 * @throws LAPException
	 */
	public MaltParserEN() throws LAPException {
		super();
		languageIdentifier = "EN";
	}

	/**
	 * constructor with parameter for the classifier.
	 * 
	 * @param listAEDescriptorsArgs
	 *            the parameter for the underlying AEs. This pipeline only has one argument PARSER_MODEL_VARIANT. 
	 *            know model variants are including "linear" and "poly" for English. Null means all default variable (default model) 
	 *            Note that passing unknown model will raise EXCEPTION from the UIMA AE. 
	 *            
	 * @throws LAPException
	 */
	public MaltParserEN(Map<String,String> listAEDescriptorsArgs) throws LAPException {
		super(listAEDescriptorsArgs);
		languageIdentifier = "EN"; // set languageIdentifer
	}

	/**
	 * constructor with parameter for the classifier and view names.
	 * 
	 * @param listAEDescriptorsArgs
	 *            the parameter for the underlying AEs. This pipeline only has one argument PARSER_MODEL_VARIANT. 
	 *            know model variants are including "linear" and "poly" for English. Null means all default variable (default model) 
	 *            Note that passing unknown model will raise EXCEPTION from the UIMA AE. 
	 * @param views
	 * @throws LAPException
	 */
	public MaltParserEN(String[] views, Map<String,String> listAEDescriptorsArgs) throws LAPException {
		super(views, listAEDescriptorsArgs);
		languageIdentifier = "EN";
	}

	@Override
	public final AnalysisEngineDescription[] listAEDescriptors(Map<String,String> args)
			throws LAPException {
		final int NUM_OF_ARR = 3;
		AnalysisEngineDescription[] descArr = new AnalysisEngineDescription[NUM_OF_ARR];
		
		String modelVariant=null; 
		
	    if ( null != args && "" != args.get("PARSER_MODEL_VARIANT"))
	    { // parser model argument passed in from the constructor. 
	    	modelVariant = args.get("PARSER_MODEL_VARIANT"); 
	    }

		try {
			descArr[0] = createPrimitiveDescription(BreakIteratorSegmenter.class);
			descArr[1] = createPrimitiveDescription(TreeTaggerPosLemmaTT4J.class);
			descArr[2] = createPrimitiveDescription(MaltParser.class,
					MaltParser.PARAM_VARIANT, modelVariant,
					MaltParser.PARAM_PRINT_TAGSET, true);
		} catch (ResourceInitializationException e) {
			throw new LAPException("Unable to create AE descriptions", e);
		}

		return descArr;
	}

}
