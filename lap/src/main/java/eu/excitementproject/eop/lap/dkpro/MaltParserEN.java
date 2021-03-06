/**
 * This package contains all the LAP components already existing in dkpro and they extend <code>LAP_ImplBaseAE</code>.
 */
package eu.excitementproject.eop.lap.dkpro;

import de.tudarmstadt.ukp.dkpro.core.maltparser.MaltParser;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.core.treetagger.TreeTaggerPosTagger;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBaseAE;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

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
	 * the default, simple constructor. Will generate default pipeline with default model. 
	 * 
	 * @throws LAPException
	 */
	public MaltParserEN() throws LAPException {
		this(null); // default model 
	}

	/**
	 * constructor with parameter for the classifier.
	 * 
	 * @param modelVariant 
	 *            This pipeline only has one argument PARSER_MODEL_VARIANT. 
	 *            know model variants are including "linear" and "poly" for English. 
	 *            Null means all default variable (default model) 
	 *            Note that passing unknown model will raise EXCEPTION from the UIMA AE. 
	 *            
	 * @throws LAPException
	 */
	public MaltParserEN(String modelVariant) throws LAPException {
		
		// a) prepare AEs 
//		AnalysisEngineDescription[] descArr = new AnalysisEngineDescription[3];
//		
//		try {
//			descArr[0] = createPrimitiveDescription(OpenNlpSegmenter.class);
//			descArr[1] = createPrimitiveDescription(TreeTaggerPosLemmaTT4J.class);
//			descArr[2] = createPrimitiveDescription(MaltParser.class,
//					MaltParser.PARAM_VARIANT, modelVariant,
//					MaltParser.PARAM_PRINT_TAGSET, true);
//		} catch (ResourceInitializationException e) {
//			throw new LAPException("Unable to create AE descriptions", e);
//		}

		// Gil: Temporary code until we can use PosMapper (of later DKPros (> 1.5))
		// TreeTagger first adds POS and Lemma, then POS overridden by OpenNLP. 
		// Note that, TreeTaggers POS annotations are still there in AnnotationIndex, 
		// but lost connection from Token annotations. Thus, parser only see OpenNLP POS. 
		AnalysisEngineDescription[] descArr = new AnalysisEngineDescription[4];
		
		try {
			descArr[0] = createEngineDescription(OpenNlpSegmenter.class);
			descArr[1] = createEngineDescription(TreeTaggerPosTagger.class);
			descArr[2] = createEngineDescription(OpenNlpPosTagger.class);
			descArr[3] = createEngineDescription(MaltParser.class,
					MaltParser.PARAM_VARIANT, modelVariant,
					MaltParser.PARAM_PRINT_TAGSET, true);
		} catch (ResourceInitializationException e) {
			throw new LAPException("Unable to create AE descriptions", e);
		}

		
		// b) call initializeViews() 
		initializeViews(descArr); 
		
		// c) set lang ID 
		languageIdentifier = "EN"; // set languageIdentifer
	}

//	/**
//	 * constructor with parameter for the classifier and view names.
//	 * 
//	 * @param listAEDescriptorsArgs
//	 *            the parameter for the underlying AEs. This pipeline only has one argument PARSER_MODEL_VARIANT. 
//	 *            know model variants are including "linear" and "poly" for English. Null means all default variable (default model) 
//	 *            Note that passing unknown model will raise EXCEPTION from the UIMA AE. 
//	 * @param views
//	 * @throws LAPException
//	 */
//	public MaltParserEN(String[] views, Map<String,String> listAEDescriptorsArgs) throws LAPException {
//		super(views, listAEDescriptorsArgs);
//		languageIdentifier = "EN";
//	}
//
//	@Override
//	public final AnalysisEngineDescription[] listAEDescriptors(Map<String,String> args)
//			throws LAPException {
//		final int NUM_OF_ARR = 3;
//		AnalysisEngineDescription[] descArr = new AnalysisEngineDescription[NUM_OF_ARR];
//		
//		String modelVariant=null; 
//		
//	    if ( null != args && "" != args.get("PARSER_MODEL_VARIANT"))
//	    { // parser model argument passed in from the constructor. 
//	    	modelVariant = args.get("PARSER_MODEL_VARIANT"); 
//	    }
//
//		try {
//			descArr[0] = createPrimitiveDescription(BreakIteratorSegmenter.class);
//			descArr[1] = createPrimitiveDescription(TreeTaggerPosLemmaTT4J.class);
//			descArr[2] = createPrimitiveDescription(MaltParser.class,
//					MaltParser.PARAM_VARIANT, modelVariant,
//					MaltParser.PARAM_PRINT_TAGSET, true);
//		} catch (ResourceInitializationException e) {
//			throw new LAPException("Unable to create AE descriptions", e);
//		}
//
//		return descArr;
//	}

}
