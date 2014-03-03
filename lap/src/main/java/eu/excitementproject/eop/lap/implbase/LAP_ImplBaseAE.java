package eu.excitementproject.eop.lap.implbase;

import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import eu.excitementproject.eop.lap.LAPException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.uimafit.factory.AggregateBuilder;

import java.util.HashMap; 
import java.util.Map; 

/**
 * 
 * <I>(Before reading / using this implementation base, first do read about LAP_ImplBase for the role of an LAP implementation base).</I> 
 * <P>
 * This is an extended, and a more efficient version of LAP Implementation Base ({@link LAP_ImplBase}), if the underlying annotation is done by AEs. 
 * Compared to the base class, this class is specifically designed to provide methods of LAPAccess ({@link eu.excitementproject.eop.lap.LAPAccess}) 
 * based on existing AEs (for example, DKPro modules).  
 * If you are adding a new linguistic analysis pipeline (LAP) with a set of existing AEs; this implementation base is better than the base LAP_ImplBase. 
 * </P>
 * 
 * <H2> Why this implementation base is needed? </H2> 
 * <P>
 * A generic (normal, non multi-view) UIMA AE only work on a single "View" of a CAS. 
 * We can "map" a generic AE to work on a specific view (like TextView or HypothesisView),
 * when the AE is initialized as a part of an aggregated analysis engine (AAE).  
 * 
 * <P>
 * The problem is that, once this mapping is done; the assigned AE cannot work on other views. View mapping is one-time thing, at the initialization time of the AE within aggregated pipeline. 
 * Since our LAPAccess needs to work on any given view, the base LAP_ImplBase simply re-map 
 * this "View mapping" each time it works on a new view. This means, the underlying AE gets re-initialized each time it works on a view. 
 * This is not a problem if the underlying AE initialization code is smart enough to init only once (like using singletons, etc), or initialization time is very short. 
 * Sadly, many AEs, do need long initialization time. (For example, model loading of a parser, etc). For such a case, using base LAP_ImplBase is not acceptible. 
 * 
 * <H2> How this class resolves the issue? </H2> 
 * 
 * <P>
 * Once mapped, UIMA AE works on this mapped view for its life cycle. We cannot change that. 
 * However, we can declare we will work only on a set of "previously defined" views. 
 * 
 * <P> 
 * This implementation base first gets an array of view names (as string), 
 * and prepare a pipeline for each of the view. Thus, when addAnnotationOn() method is called, 
 * it will use a specifically prepared pipeline for the request. If the request is on a view 
 * name that is unknown, it will simply give up and raise an exception. 
 * 
 * <H2> So What to override? </H2> 
 * <P> 
 * If you want to provide a LAPAccess module based on this implementation base, 
 * you have to override two methods. One is the constructor, and the other is 
 * listOfAEDescriptors(). In the constructor, you simply need to set language ID, & call super(). 
 * The method listOfAEDescriptors(), will be called in the constructor of ImplBaseAE code, and 
 * you have to return an ordered array of AnalysisEngineDescriptor. 
 * 
 * <P> 
 * Unlike {@link LAP_ImplBase}, you don't need to override addAnnotationOn(), since it is 
 * already provided by this class. However, you have to provide the list of AEs that will 
 * be used in the addAnnotationOn(), in the forms of ordered list of AE Descriptors.   
 * 
 * <P> Sometimes, an example is much easier to understand than the description. 
 * For a usage example of this implementation base, see {@link ExampleLAPAE} </P> 
 * @author Gil 
 */


public abstract class LAP_ImplBaseAE extends LAP_ImplBase {
	
	/**
	 * This is the full constructor. 
	 * <P> 
	 * 
	 * The constructor of LAP_ImplBaseAE requires "view names" that this 
	 * LAP will work on. Once initialized addAnnotationOn() will only work 
	 * on this "known" views only. 
	 * 
	 * The constructor pre-generates one AAE for each of the view, and 
	 * use them to annotate data on that view. 
	 * 
	 * @param views Name of the views that this LAPAccess will process after init. 
	 * @param listAEDescriptorsArgs This argument holds the arguments that will be passed directly to the listAEDescriptorsArgs() which will generate AE descriptors. The arguments are depending on the implementation of listAEDescriptors(). The constructor does not use this value as itself. 
	 * @throws LAPException
	 */
	protected LAP_ImplBaseAE(String[] views, Map<String,String> listAEDescriptorsArgs) throws LAPException 
	{
		engineForView = new HashMap<String, AnalysisEngine>();  
		AnalysisEngineDescription[] descList = listAEDescriptors(listAEDescriptorsArgs); 
		for (String v : views)
		{
			AggregateBuilder builder = new AggregateBuilder();
			for (AnalysisEngineDescription d : descList)
			{
				builder.add(d, INITIALVIEW, v); // maps view name v, to AE's default view. 				
			}
			
			AnalysisEngine aae = null; 
			try {
				aae = builder.createAggregate(); 
			}
			catch (ResourceInitializationException e)
			{
				throw new LAPException("Unable to create the AAE from AE descriptions", e); 
			}
			engineForView.put(v, aae); 
		}
	}

	/**
	 * Simplified constructor for ImplBaseAE
	 * <P> 
	 * LAP_ImplBaseAE constructor without any argument will simply call 
	 * LAP_ImplBaseAE(String[] knownViews) with three default view names 
	 * TextView, HypothesisView and _InitialView 
	 * Also, this passes null arguments for listAEDescriptors 
	 * 
	 * @throws LAPException
	 */
	protected LAP_ImplBaseAE() throws LAPException
	{
		this(new String[]{INITIALVIEW, TEXTVIEW, HYPOTHESISVIEW}, null); 
	}
	
	/**
	 * Another simplified constructor for ImplBaseAE
	 * <P>
	 * This constructor uses the default three views as the target views, and passes the given argument map to listAEDescriptor(). 
	 * 
	 * @param listAEDescriptorsArgs This argument holds the arguments that will be passed directly to the listAEDescriptorsArgs() which will generate AE descriptors. The arguments are depending on the implementation of listAEDescriptors(). The constructor does not use this value as itself. 
	 * @throws LAPException
	 */
	protected LAP_ImplBaseAE(Map<String,String> listAEDescriptorsArgs) throws LAPException
	{
		this(new String[]{INITIALVIEW, TEXTVIEW, HYPOTHESISVIEW}, listAEDescriptorsArgs); 
	}
	
	/**
	 * <P>
	 * Override and provide this abstract method. This is one of the two methods that 
	 * needs to be provided for the implementation base to work on. The method will be 
	 * called in the constructor of the impl base. 
	 * 
	 * <P>
	 * The implementation base will initialize an AAE based on this ordered array, and 
	 * prepare single AAE for each of the given view name (view names are given in the 
	 * constructor argument).  
	 * 
	 * <P> 
	 * See {@link XX} for an implementation example. 
	 * 
	 * @return an ordered array of AnalysisEngineDescription 
	 */
	public abstract AnalysisEngineDescription[] listAEDescriptors(Map<String,String> arguments) throws LAPException;
	
	
	@Override
	public void addAnnotationOn(JCas aJCas, String viewName)
			throws LAPException 
	{
		AnalysisEngine aae = engineForView.get(viewName); 
		if (aae == null)
		{
			throw new LAPException("Unknown View Name \""+viewName +"\". The pipeline can only handle a viewName that is given to the constructor");			
		}
		
		try {
				aae.process(aJCas);
		}
		catch (AnalysisEngineProcessException e)
		{
			throw new LAPException("Underlying AE or AAE reported an exception", e);
			
		}	
	}
	protected HashMap<String, AnalysisEngine> engineForView; 
	
}
