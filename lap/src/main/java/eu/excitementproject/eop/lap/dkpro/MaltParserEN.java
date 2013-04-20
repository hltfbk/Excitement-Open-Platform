package eu.excitementproject.eop.lap.dkpro;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.maltparser.MaltParser;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.tudarmstadt.ukp.dkpro.core.treetagger.TreeTaggerPosLemmaTT4J;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.lappoc.LAP_ImplBaseAE;

/**
 *
 * English tokenizer + TreeTagger + MaltParser (wrapped in DKPro component)  
 * This class is provides all LAPAccess methods simply by overriding addAnnotationTo() of LAP_ImplBase
 * 
 * @author Rui
 *
 */

public class MaltParserEN extends LAP_ImplBaseAE {

	private String aVariant;
	
	public MaltParserEN() throws LAPException {
		super();
		languageIdentifier = "EN";
		this.aVariant = "";
	}

	public MaltParserEN(String aVariant) throws LAPException {
		super();
		languageIdentifier = "EN"; // set languageIdentifer
		this.aVariant = aVariant;
	}
	
	public MaltParserEN(String aVariant, String[] views) throws LAPException {
		super(views);
		languageIdentifier = "EN"; 
		this.aVariant = aVariant;
	}

	@Override
	public AnalysisEngineDescription[] listAEDescriptors() throws LAPException {
		// This example uses DKPro BreakIterSegmenter, TreeTagger, and MaltParser
		// simply return them in an array, with order. (sentence segmentation first, then tagging, then parsing) 
		AnalysisEngineDescription[] descArr = new AnalysisEngineDescription[3];
		try 
		{
			descArr[0] = createPrimitiveDescription(BreakIteratorSegmenter.class);
			descArr[1] = createPrimitiveDescription(TreeTaggerPosLemmaTT4J.class); 
			descArr[2] = createPrimitiveDescription(MaltParser.class,
					MaltParser.PARAM_VARIANT, aVariant,
					MaltParser.PARAM_PRINT_TAGSET, true);
		}
		catch (ResourceInitializationException e)
		{
			throw new LAPException("Unable to create AE descriptions", e); 
		}
		
		return descArr; 
	}

}
