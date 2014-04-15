package eu.excitementproject.eop.lap.dkpro;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import java.util.Map;

//import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
//import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
//import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
//import org.uimafit.factory.AggregateBuilder;
//import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.tudarmstadt.ukp.dkpro.core.treetagger.TreeTaggerPosLemmaTT4J;

import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
//import eu.excitementproject.eop.lap.lappoc.LAP_ImplBase;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBaseAE;


/**
 * 
 * English tokenizer + tagger + lemmatizer, that relies on TreeTagger 
 * (wrapped in DKPro component)  
 * This class is provides all LAPAccess methods simply by overriding addAnnotationTo() of LAP_ImplBase
 * 
 * @author Gil 
 *
 */

public class TreeTaggerEN extends LAP_ImplBaseAE implements LAPAccess {

	public TreeTaggerEN() throws LAPException {
		super(); 
		languageIdentifier = "EN"; // set languageIdentifer, this ID is needed for generateTHPair from String  		
	}

	public TreeTaggerEN(String[] views) throws LAPException {
		super(views, null);
		languageIdentifier = "EN"; 
	}

	@Override
	public AnalysisEngineDescription[] listAEDescriptors(Map<String,String> args) throws LAPException{
		// This example uses DKPro BreakIterSegmenter and TreeTagger. 
		// simply return them in an array, with order. (sentence segmentation first, then tagging) 
		// also, this example does not use any arguments (e.g. no model selection, etc) 
		AnalysisEngineDescription[] descArr = new AnalysisEngineDescription[2];
		try 
		{
			descArr[0] = createPrimitiveDescription(OpenNlpSegmenter.class);
			//descArr[0] = createPrimitiveDescription(BreakIteratorSegmenter.class);
			descArr[1] = createPrimitiveDescription(TreeTaggerPosLemmaTT4J.class); 
		}
		catch (ResourceInitializationException e)
		{
			throw new LAPException("Unable to create AE descriptions", e); 
		}
		
		return descArr; 
	}
}


// // 
// // OLD, slower code. 
//{
//
//	/**
//	 * The constructor sets the language flag.
//	 * 
//	 * @throws LAPException
//	 */
//	public TreeTaggerEN() throws LAPException {
//		super(); 		
//		languageIdentifier = "EN";
//	}
//
//	@Override
//	public void addAnnotationOn(JCas aJCas, String viewName)
//			throws LAPException {
//		// prepare DKPro components 
//		AnalysisEngineDescription seg = null; 
//		AnalysisEngineDescription tagger = null; 
//		try {
//			seg = createPrimitiveDescription(BreakIteratorSegmenter.class);
//			tagger = createPrimitiveDescription(TreeTaggerPosLemmaTT4J.class);
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
//
//	}
//
//}
