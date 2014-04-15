package eu.excitementproject.eop.lap.dkpro;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

//import java.util.Map;

//import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
//import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
//import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
//import org.uimafit.factory.AggregateBuilder;

import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
//import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
//import de.tudarmstadt.ukp.dkpro.core.treetagger.TreeTaggerPosLemmaTT4J;
//import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
//import eu.excitementproject.eop.lap.lappoc.LAP_ImplBase;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBaseAE;

/**
 * 
 * English tokenizer + tagger, that relies on OpenNLP sent + tokenizer + tagger (wrapped in DKPro component)  
 * This class provides all LAPAccess methods based on LAP_ImplBase. The class only provides the UIMA AEs 
 * that would be used by the pipeline, and actual capabilities are all provided by ImplBaseAE. See the super class for more detail.  
 * 
 * @author Tae-Gil Noh 
 * @since Aug 2012  
 *
 */
public class OpenNLPTaggerEN extends LAP_ImplBaseAE {

	public OpenNLPTaggerEN() throws LAPException {
		
		// 1) prepare AEs 
		// This LAPAccess instance uses OpenNLP segmenter (sentence breaker + tokenizer) and OpenNLP Tagger;
		// via DKPro AEs. 
		// This LAP does not use any arguments (e.g. no model selection, etc) 
		AnalysisEngineDescription[] descArr = new AnalysisEngineDescription[2];
		try 
		{
			descArr[0] = createPrimitiveDescription(OpenNlpSegmenter.class);
			descArr[1] = createPrimitiveDescription(OpenNlpPosTagger.class); 
		}
		catch (ResourceInitializationException e)
		{
			throw new LAPException("Unable to create AE descriptions", e); 
		}

		// 2) call initializeViews() 
		initializeViews(descArr); 
		
		// 3) set lang ID 		
		languageIdentifier = "EN"; 
		
	}

//	@Override
//	public AnalysisEngineDescription[] listAEDescriptors(Map<String,String> args) throws LAPException{
//		// This LAPAccess instance uses OpenNLP segmenter (sentence breaker + tokenizer) and OpenNLP Tagger;
//		// via DKPro AEs. 
//		// Here it simply return them in an array, with order. (sentence segmentation first, then tagging) 
//		// also, this LAP does not use any arguments (e.g. no model selection, etc) 
//		AnalysisEngineDescription[] descArr = new AnalysisEngineDescription[2];
//		try 
//		{
//			descArr[0] = createPrimitiveDescription(OpenNlpSegmenter.class);
//			descArr[1] = createPrimitiveDescription(OpenNlpPosTagger.class); 
//		}
//		catch (ResourceInitializationException e)
//		{
//			throw new LAPException("Unable to create AE descriptions", e); 
//		}
//		
//		return descArr; 
//	}
}

//public class OpenNLPTaggerEN extends LAP_ImplBase implements LAPAccess {
//	
//	/**
//	 * The constructor sets the language flag.
//	 * 
//	 * @throws LAPException
//	 */
//	public OpenNLPTaggerEN() throws LAPException {
//		super(); 		
//		languageIdentifier = "EN"; 
//	}	
//
//	@Override 
//	public void addAnnotationOn(JCas aJCas, String viewName)
//			throws LAPException 
//	{
//		// prepare DKPro components 
//		AnalysisEngineDescription seg = null; 
//		AnalysisEngineDescription tagger = null; 
//		try {
//			seg = createPrimitiveDescription(BreakIteratorSegmenter.class);
//			tagger = createPrimitiveDescription(OpenNlpPosTagger.class);
//		}
//		catch (ResourceInitializationException re)
//		{
//			throw new LAPException("Failed to initilize DKPro UIMA component" ,re ); 
//		}	
//		// Using AggregateBuilder to assign views 
//		AggregateBuilder builder = new AggregateBuilder();
//		builder.add(seg, "_InitialView", viewName);
//		builder.add(tagger, "_InitialView", viewName); 
//		
//		try {
//			AnalysisEngine ae = builder.createAggregate(); 
//			ae.process(aJCas); 
//		}
//		catch (ResourceInitializationException re)
//		{
//			throw new LAPException("Failed to initilize aggregate AE component" ,re ); 
//		} 
//		catch (AnalysisEngineProcessException e) 
//		{
//			throw new LAPException("An exception while running the aggregate AE", e); 
//		}		
//	}
//
//}
