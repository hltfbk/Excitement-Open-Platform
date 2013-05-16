package eu.excitementproject.eop.lap.dkpro;

//TODO: the implementation is not complete yet, due to the collision of different JARs.
//
//import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
//
//import java.util.Map;
//
//import org.apache.uima.analysis_engine.AnalysisEngineDescription;
//import org.apache.uima.resource.ResourceInitializationException;
//
//import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordNamedEntityRecognizer;
//import eu.excitementproject.eop.lap.LAPException;
//import eu.excitementproject.eop.lap.lappoc.LAP_ImplBaseAE;
//
///**
// * This LAPAccess implementation provides LAPAcess interface for English by using 
// * Stanford CoreNLP tools, including segmentation, POS tagging, lemmatization, and NE Recognition. 
// * 
// * @author Rui
// */
//public class StanfordNEREN extends LAP_ImplBaseAE {
//
//	/**
//	 * Without any argument, this will initiate the instance with English default 
//	 * model for the NE recognizer.
//	 * 
//	 * @throws LAPException
//	 */
//	public StanfordNEREN() throws LAPException 
//	{
//		this(null); // default model 		
//	}
//	
//	/** 
//	 * This constructor will initiate the Stanford NER based LAP instance with the given modelVarient. 
//	 * (Thus: de.tudarmstadt.ukp.dkpro.core.stanfordnlp-model-ner-[LANGID]-[modelVarient] ) 
//	 * 
//	 * @param modelVarient String of the model varient. If the given varient is not accessible in the class path, the underlying AE will try to use default. If default isn't there, it will raise an exception. 
//	 * @throws LAPException
//	 */
//	public StanfordNEREN(Map<String,String> listAEDescriptorsArgs) throws LAPException 
//	{
//		super(listAEDescriptorsArgs); 
//		languageIdentifier = "EN"; // set languageIdentifer
//	}
//	
//	/**
//	 * <P> 
//	 * Full constructor, that accepts model Varient and view names. If you use this LAP for EOP EDAs (e.g. where you need TextView & Hypothesis View), you can simply call the constructor without view names (since their default is Default, TView and HView). 
//	 * ModelVarient will load the desinated model-varient. 	(Thus: de.tudarmstadt.ukp.dkpro.core.stanfordnlp-model-ner-[LANGID]-[modelVarient] ) 
//	 * 
//	 * @param modelVarient String of the model varient. If the given varient is not accessible in the class path, the the underlying AE will try to use default. If default isn't there, it will raise an exception.
//	 * @param views String array of "view names". Once initialized, the instance can only annotate this "previously given" Views only. (See LAP_ImplBaseAE for explanation). 
//	 * @throws LAPException
//	 */
//	public StanfordNEREN(String[] views, Map<String,String> listAEDescriptorsArgs) throws LAPException 
//	{
//		super(views, listAEDescriptorsArgs);
//		languageIdentifier = "EN"; 
//	}
//	
//	@Override
//	public AnalysisEngineDescription[] listAEDescriptors(Map<String, String> args) throws LAPException {
//
//		AnalysisEngineDescription[] descArr = new AnalysisEngineDescription[1];
//		
//		String modelVariant=null; 
//
//	    if ( (args != null) && (args.get("PARSER_MODEL_VARIANT") != ""))
//	    { // parser model argument passed in from the constructor. 
//	    	modelVariant = args.get("PARSER_MODEL_VARIANT"); 
//	    }
//	    
//		try 
//		{
////			descArr[0] = createPrimitiveDescription(StanfordSegmenter.class);
////			descArr[1] = createPrimitiveDescription(StanfordPosTagger.class); 
////			descArr[0] = createPrimitiveDescription(StanfordLemmatizer.class); 
//			descArr[0] = createPrimitiveDescription(
//                    StanfordNamedEntityRecognizer.class,
//                    StanfordNamedEntityRecognizer.PARAM_PRINT_TAGSET, true,
//                    StanfordNamedEntityRecognizer.PARAM_VARIANT, modelVariant);
//		}
//		catch (ResourceInitializationException e)
//		{
//			throw new LAPException("Unable to create AE descriptions", e); 
//		}
//		
//		return descArr; 
//				
//	}
//	
//}

