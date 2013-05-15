package eu.excitementproject.eop.lap.dkpro;

// FROZEN
// WE will revive MSTParser after DKPRO 1.5.0 gets stable and uploaded to Maven Central 
// 
//
//import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
//
//import java.util.Map;
//
//import org.apache.uima.analysis_engine.AnalysisEngineDescription;
//import org.apache.uima.resource.ResourceInitializationException;
//
//import de.tudarmstadt.ukp.dkpro.core.mstparser.MSTParser;
//import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
//import de.tudarmstadt.ukp.dkpro.core.treetagger.TreeTaggerPosLemmaTT4J;
//
//import eu.excitementproject.eop.lap.LAPException;
//import eu.excitementproject.eop.lap.lappoc.LAP_ImplBaseAE;
//
///**
// * This LAPAccess implementation provides LAPAcess interface for English by using 
// * Sentence Splitter + Tree tagger + MSTParser. 
// * 
// * <P>
// * It will use default English Tree Tagger model, but you can select Parser model by the "variant" string in the constructor. 
// * (variant, is a model ID, that is used by DKPro parser AE) 
// * 
// * 
// * // TODO BUG: For the moment, super() is called before modelVariant is set. Thus it fails to update model variant. Need to think a best way to do this. 
// * // TODO (update exception comment on modelVariant, after a good checking). 
// * @author Gil
// */
//public class MSTParserEN extends LAP_ImplBaseAE {
//
//	/**
//	 * Without any argument, this will initiate the instance with English default 
//	 * model for MST Parser, with the capability of annotating the "three views" (Default, TextView & HypothesisView). 
//	 * If you need only need to annotate a specific view (e.g. Default only, not for T & H View, etc) 
//	 * call the full constructor with two arguments. (e.g. if you give one view only, its initialization is actually faster). 
//	 * 
//	 * @throws LAPException
//	 */
//	public MSTParserEN() throws LAPException 
//	{
//		this(null); // no arguments passed to listAEDescriptors -- means default model
//	}
//	
//	/**
//	 * <P> 
//	 * This constructor will initiate this MSTParser based LAP instance with the given modelVarient. 
//	 * (Thus: de.tudarmstadt.ukp.dkpro.core.mstparser-model-parser-[LANGID]-[modelVarient] ) 
//	 * 
//	 * <P> 
//	 * This will initiate the instance with the capability of annotating the "three views" (Default, TextView & HypothesisView). 
//	 * If you need only need to annotate a specific view (e.g. Default only, not for T & H View, etc) 
//	 * call the full constructor with two arguments. (e.g. if you give one view only, its initialization is faster).
//	 * 
//	 * @param listAEDescriptorsArgs This argument holds the arguments that will be passed directly to the listAEDescriptorsArgs() which will generate AE descriptors. The arguments are depending on the implementation of listAEDescriptors(). Only one argument is know to this pipeline. PARSER_MODEL_VARIANT: which will load corresponding model variant of MST parser. 
//	 * @throws LAPException
//	 */
//	public MSTParserEN(Map<String,String> listAEDescriptorsArgs) throws LAPException 
//	{
//		super(listAEDescriptorsArgs); 
//		languageIdentifier = "EN"; // set languageIdentifer
//		// REMOVED: use listAEDescriptorArgs instead.	//this.modelVariant = modelVarient; // this will determine model-"variant" for model loading 
//	}
//	
//	/**
//	 * <P> 
//	 * Full constructor, that accepts model Varient and view names. If you use this LAP for EOP EDAs (e.g. where you need TextView & Hypothesis View), you can simply call the constructor without view names (since their default is Default, TView and HView). 
//	 * ModelVarient will load the desinated model-varient. 	(Thus: de.tudarmstadt.ukp.dkpro.core.mstparser-model-parser-[LANGID]-[modelVarient] ) 
//	 * 
//	 * @param views String array of "view names". Once initialized, the instance can only annotate this "previously given" Views only. (See LAP_ImplBaseAE for explanation). 
//	 * @param listAEDescriptorsArgs This argument holds the arguments that will be passed directly to the listAEDescriptorsArgs() which will generate AE descriptors. The arguments are depending on the implementation of listAEDescriptors(). Only one argument is know to this pipeline. PARSER_MODEL_VARIANT: which will load corresponding model variant of MST parser. 
//	 * @throws LAPException
//	 */
//	public MSTParserEN(String[] views, Map<String,String> listAEDescriptorsArgs) throws LAPException 
//	{
//		super(views, listAEDescriptorsArgs);
//		languageIdentifier = "EN"; 
//		// REMOVED: use listAEDescriptorArgs instead // this.modelVariant = modelVarient; 
//	}
//	
//	@Override
//	public AnalysisEngineDescription[] listAEDescriptors(Map<String, String> args) throws LAPException {
//
//		// This example uses DKPro BreakIterSegmenter, TreeTagger, and MSTParser 
//		// simply return them in an array, with order. (sentence segmentation first, then tagging, then parsing) 
//		AnalysisEngineDescription[] descArr = new AnalysisEngineDescription[3];
//		String modelVariant=null; 
//
//	    if ( (args != null) && (args.get("PARSER_MODEL_VARIANT") != ""))
//	    { // parser model argument passed in from the constructor. 
//	    	modelVariant = args.get("PARSER_MODEL_VARIANT"); 
//	    }
//	    
//		try 
//		{
//			descArr[0] = createPrimitiveDescription(BreakIteratorSegmenter.class);
//			descArr[1] = createPrimitiveDescription(TreeTaggerPosLemmaTT4J.class); 
//			descArr[2] = createPrimitiveDescription(MSTParser.class,
//					MSTParser.PARAM_VARIANT, modelVariant); 
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
//	//private String modelVariant; 
//	
//}
//
