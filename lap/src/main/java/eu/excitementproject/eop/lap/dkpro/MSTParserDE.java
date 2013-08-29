package eu.excitementproject.eop.lap.dkpro;

//FROZEN
//WE will revive MSTParser after DKPRO 1.5.0 gets stable and uploaded to Maven Central 
//
//
//import java.util.Map;
//
//import eu.excitementproject.eop.lap.LAPException;
//
///**
// * <P> 
// * This LAPAccess implementation provides LAPAcess interface for German by using 
// * Sentence Splitter + Tree tagger + MSTParser. 
// * 
// * <P>
// * It will use default German Tagger model, but you can select Parser model by the "variant" string in the constructor. 
// * (variant, is a model ID, that is used by DKPro parser AE) 
// * 
// * @author Gil 
// *
// */
//
//
//public class MSTParserDE extends MSTParserEN {
//
//	/**
//	 * Without any argument, this will initiate the instance with German default 
//	 * model for MST Parser, with the capability of annotating the "three views" (Default, TextView & HypothesisView). 
//	 * If you need only need to annotate a specific view (e.g. Default only, not for T & H View, etc) 
//	 * call the full constructor with two arguments. (e.g. if you give one view only, its initialization is actually faster). 
//	 *  
//	 * @throws LAPException
//	 */
//	public MSTParserDE() throws LAPException
//	{
//		super(); 
//		this.languageIdentifier="DE"; // setting LangID. Other things will be handled accordingly in base class
//	}
//	
//	/**
//	 * <P> 
//	 * This constructor will initiate this MSTParser based LAP instance with the given PARSER_MODEL_VARIANT. 
//	 * (Thus: de.tudarmstadt.ukp.dkpro.core.mstparser-model-parser-[LANGID]-[modelVarient] ) 
//	 * 
//	 * <P> 
//	 * This will initiate the instance with the capability of annotating the "three views" (Default, TextView & HypothesisView). 
//	 * If you need only need to annotate a specific view (e.g. Default only, not for T & H View, etc) 
//	 * call the full constructor with two arguments. (e.g. if you give one view only, its initialization is faster).
//	 * 
//	 * @param listAEDescriptorsArgs (For German, "long" and "default" model variants are included in the first release.) This argument holds the arguments that will be passed directly to the listAEDescriptorsArgs() which will generate AE descriptors. The arguments are depending on the implementation of listAEDescriptors(). Only one argument is know to this pipeline. PARSER_MODEL_VARIANT: which will load corresponding model variant of MST parser. The value can be null, for default arguments. 
//	 * @throws LAPException
//	 */
//	public MSTParserDE(Map<String,String> listAEDescriptorsArgs) throws LAPException 
//	{
//		super(listAEDescriptorsArgs);
//		this.languageIdentifier="DE"; // setting LangID. Other things will be handled accordingly in base class
//	}
//	
//	/**
//	 * <P> 
//	 * Full constructor, that accepts model Varient and view names. If you use this LAP for EOP EDAs (e.g. where you need TextView & Hypothesis View), you can simply call the constructor without view names (since their default is Default, TView and HView). 
//	 * ModelVarient will load the desinated model-varient. 	(Thus: de.tudarmstadt.ukp.dkpro.core.mstparser-model-parser-[LANGID]-[modelVarient] ) 
//	 * 
//	 * @param views String array of "view names". Once initialized, the instance can only annotate this "previously given" Views only. (See LAP_ImplBaseAE for explanation). 
//	 * @param listAEDescriptorsArgs (For German, "long" and "default" model variants are included in the first release.) This argument holds the arguments that will be passed directly to the listAEDescriptorsArgs() which will generate AE descriptors. The arguments are depending on the implementation of listAEDescriptors(). Only one argument is know to this pipeline. PARSER_MODEL_VARIANT: which will load corresponding model variant of MST parser. 
//	 * @throws LAPException
//	 */
//	public MSTParserDE(String[] views, Map<String,String> listAEDescriptorsArgs ) throws LAPException
//	{
//		super(views, listAEDescriptorsArgs); 
//		this.languageIdentifier="DE"; // setting LangID. Other things will be handled accordingly in base class
//	}
//	
//}
