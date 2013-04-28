package eu.excitementproject.eop.lap.dkpro;

import eu.excitementproject.eop.lap.LAPException;

/**
 * <P> 
 * This LAPAccess implementation provides LAPAcess interface for German by using 
 * Sentence Splitter + Tree tagger + MSTParser. 
 * 
 * <P>
 * It will use default German Tagger model, but you can select Parser model by the "variant" string in the constructor. 
 * (variant, is a model ID, that is used by DKPro parser AE) 
 * 
 * // TODO: "Long" model, yet to be provided. (for the moment, only "default" variant is in the repository) 
 * // TODO: test "Variant" actually works for long, or any other models. 
 * @author Gil 
 *
 */


public class MSTParserDE extends MSTParserEN {

	
	/**
	 * Without any argument, this will initiate the instance with German default 
	 * model for MST Parser, with the capability of annotating the "three views" (Default, TextView & HypothesisView). 
	 * If you need only need to annotate a specific view (e.g. Default only, not for T & H View, etc) 
	 * call the full constructor with two arguments. (e.g. if you give one view only, its initialization is actually faster). 
	 *  
	 * @throws LAPException
	 */
	public MSTParserDE() throws LAPException
	{
		super(); 
		this.languageIdentifier="DE"; // setting LangID. Other things will be handled accordingly in base class
	}
	
	/**
	 * <P> 
	 * This constructor will initiate this MSTParser based LAP instance with the given modelVarient. 
	 * (Thus: de.tudarmstadt.ukp.dkpro.core.mstparser-model-parser-[LANGID]-[modelVarient] ) 
	 * 
	 * <P> 
	 * This will initiate the instance with the capability of annotating the "three views" (Default, TextView & HypothesisView). 
	 * If you need only need to annotate a specific view (e.g. Default only, not for T & H View, etc) 
	 * call the full constructor with two arguments. (e.g. if you give one view only, its initialization is faster).
	 * 
	 * @param modelVarient String of the model varient. If the given varient is not accessible in the class path, the underlying AE will try to use default. If default isn't there, it will raise an exception.
	 * @throws LAPException
	 */
	public MSTParserDE(String modelVariant) throws LAPException 
	{
		super(modelVariant);
		this.languageIdentifier="DE"; // setting LangID. Other things will be handled accordingly in base class
		
	}
	
	/**
	 * <P> 
	 * Full constructor, that accepts model Varient and view names. If you use this LAP for EOP EDAs (e.g. where you need TextView & Hypothesis View), you can simply call the constructor without view names (since their default is Default, TView and HView). 
	 * ModelVarient will load the desinated model-varient. 	(Thus: de.tudarmstadt.ukp.dkpro.core.mstparser-model-parser-[LANGID]-[modelVarient] ) 
	 * 
	 * @param modelVarient String of the model varient. If the given varient is not accessible in the class path, the underlying AE will try to use default. If default isn't there, it will raise an exception.
	 * @param views String array of "view names". Once initialized, the instance can only annotate this "previously given" Views only. (See LAP_ImplBaseAE for explanation). 
	 * @throws LAPException
	 */
	public MSTParserDE(String modelVariant, String[] views) throws LAPException
	{
		super(modelVariant, views); 
		this.languageIdentifier="DE"; // setting LangID. Other things will be handled accordingly in base class
	}
	
}
