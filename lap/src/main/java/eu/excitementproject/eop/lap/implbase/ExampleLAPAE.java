package eu.excitementproject.eop.lap.implbase;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

//import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
//import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
//import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
//import de.tudarmstadt.ukp.dkpro.core.treetagger.TreeTaggerPosLemmaTT4J;

import eu.excitementproject.eop.lap.LAPException;

/**
 * 
 * This is an example to show how to use LAP_ImplBaseAE to generate a new 
 * LAPAccess implementation. The implementation base is ideal for generating 
 * LAPAccess from a set of pre-existing AEs. For more documentation see {@link LAP_ImplBaseAE}. 
 * 
 * @author Gil 
 *
 */
public class ExampleLAPAE extends LAP_ImplBaseAE {

	public ExampleLAPAE() throws LAPException {
		
		// Basically, you have to do 3 things in your constructor(s). 
		
		// 1) make your AE descriptor list. If you want to pass some parameters to the AEs, 
		// you can get those argument values by extending your constructor (e.g. CommmonConfig, 
		// or argument passing on your constructor. -- note that LAP_ImplBaseAE has only the 
		// default (implicit) constructor, and you can add any constructor for your own class.) 
		
		// 2) Call "initializeViews()", with the prepared AE descriptor list. 
		
		// 3) set (override) languageIdentifier string value. Default is EN (English). 
		// (This ID is needed for generateTHPair from String, the resulting CAS will be set with the ID)   
		
		// The example uses openNLPPOSTagger. 
		
		// step 1) prepare AEs
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

		// step 2) call initializeViews() with those AEs. 
		// the method is provided by LAP_ImplBaseAE, and it will setup AAE for each 
		// view (Textview, HypothesisView) 
		initializeViews(descArr); 
		
		// step 3) set languageIdentifer, 
		languageIdentifier = "EN"; 		
	}

}
