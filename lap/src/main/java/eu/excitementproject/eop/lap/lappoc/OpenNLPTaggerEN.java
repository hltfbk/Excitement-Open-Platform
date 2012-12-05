package eu.excitementproject.eop.lap.lappoc;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.factory.AggregateBuilder;

import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.ae.postagger.OpenNlpPosTaggerAE;
import eu.excitementproject.eop.lap.ae.tokenizer.MaxentTokenizerAE;
import eu.excitementproject.eop.lap.ae.tokenizer.OpenNLPTokenizerAE;

/**
 * 
 * English tokenizer + tagger, that relies on OpenNLP tagger (wrapped in DKPro component)  
 * This class is provides all LAPAccess methods simply by overriding addAnnotationTo() of LAP_ImplBase
 * 
 * @author Gil 
 *
 */
public class OpenNLPTaggerEN extends LAP_ImplBase implements LAPAccess {
	
	public OpenNLPTaggerEN() throws LAPException {
		super(); 		
		languageIdentifier = "EN"; // set languageIdentifer 
	}	

	@Override 
	public void addAnnotationOn(JCas aJCas, String viewName)
			throws LAPException 
	{
		// prepare DKPro components 
		AnalysisEngineDescription seg = null; 
		
		AnalysisEngineDescription tagger = null; 
		try {
			seg = createPrimitiveDescription(BreakIteratorSegmenter.class, BreakIteratorSegmenter.PARAM_SPLIT_AT_APOSTROPHE, true);
			//tagger = createPrimitiveDescription(MaxentTokenizerAE.class);
			//tagger = createPrimitiveDescription(OpenNLPTokenizerAE.class, OpenNLPTokenizerAE.PARAM_MODEL_FILE, "D:/Java/Jars/opennlp-tools-1.3.0/models/english/tokenize/EnglishTok.bin.gz");
			tagger = createPrimitiveDescription(OpenNlpPosTaggerAE.class,
					OpenNlpPosTaggerAE.PARAM_MODEL_FILE , "D:/Java/Jars/opennlp-tools-1.3.0/models/english/parser/tag.bin.gz",
					OpenNlpPosTaggerAE.PARAM_TAG_DICT, "D:/Java/Jars/opennlp-tools-1.3.0/models/english/parser/tagdict");
			//tagger = createPrimitiveDescription(OpenNlpPosTagger.class);
		}
		catch (ResourceInitializationException re)
		{
			throw new LAPException("Failed to initilize DKPro UIMA component" ,re ); 
		}	
		// Using AggregateBuilder to assign views 
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(seg, "_InitialView", viewName);
		builder.add(tagger, "_InitialView", viewName); 
		
		try {
			AnalysisEngine ae = builder.createAggregate(); 
			ae.process(aJCas); 
		}
		catch (ResourceInitializationException re)
		{
			throw new LAPException("Failed to initilize aggregate AE component" ,re ); 
		} 
		catch (AnalysisEngineProcessException e) 
		{
			throw new LAPException("An exception while running the aggregate AE", e); 
		}		
	}

}
