package eu.excitementproject.eop.lap.lappoc;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;

//import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.tudarmstadt.ukp.dkpro.core.treetagger.TreeTaggerPosLemmaTT4J;

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
		super(); 
		languageIdentifier = "EN"; // set languageIdentifer, this ID is needed for generateTHPair from String  		
	}

	public ExampleLAPAE(String[] views) throws LAPException {
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
			descArr[0] = createPrimitiveDescription(BreakIteratorSegmenter.class);
			descArr[1] = createPrimitiveDescription(TreeTaggerPosLemmaTT4J.class); 
		}
		catch (ResourceInitializationException e)
		{
			throw new LAPException("Unable to create AE descriptions", e); 
		}
		
		return descArr; 
	}
}
