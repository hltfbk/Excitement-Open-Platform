package eu.excitementproject.eop.lap.biu.ae;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.factory.AggregateBuilder;

import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.biu.ae.ner.StanfordNamedEntityRecognizerAE;
import eu.excitementproject.eop.lap.biu.ae.parser.EasyFirstParserAE;
import eu.excitementproject.eop.lap.biu.ae.postagger.MaxentPosTaggerAE;
import eu.excitementproject.eop.lap.biu.ae.sentencesplitter.LingPipeSentenceSplitterAE;
import eu.excitementproject.eop.lap.biu.ae.tokenizer.MaxentTokenizerAE;
import eu.excitementproject.eop.lap.lappoc.LAP_ImplBase;

public class BIUFullLAP extends LAP_ImplBase implements LAPAccess {

	public BIUFullLAP() throws LAPException {
		super();
		languageIdentifier = "EN"; // set languageIdentifer 
	}	

	@Override
	public void addAnnotationOn(JCas aJCas, String viewName) throws LAPException {
		try {
			// Build anaysis engines
			AnalysisEngineDescription splitter =   createPrimitiveDescription(LingPipeSentenceSplitterAE.class);
			AnalysisEngineDescription tokenizer =  createPrimitiveDescription(MaxentTokenizerAE.class);
			AnalysisEngineDescription tagger =     createPrimitiveDescription(MaxentPosTaggerAE.class,
					MaxentPosTaggerAE.PARAM_MODEL_FILE , "D:\\java\\jars\\stanford-postagger-full-2008-09-28\\models\\left3words-wsj-0-18.tagger");
			AnalysisEngineDescription ner =        createPrimitiveDescription(StanfordNamedEntityRecognizerAE.class,
					MaxentPosTaggerAE.PARAM_MODEL_FILE , "D:\\java\\jars\\stanford-ner-2009-01-16\\classifiers\\ner-eng-ie.crf-3-all2008-distsim.ser.gz");
			AnalysisEngineDescription parser =     createPrimitiveDescription(EasyFirstParserAE.class
					//,
					//EasyFirstParserAE.PARAM_HOST , "132.70.6.156", //te-srv1
					//EasyFirstParserAE.PARAM_PORT , 8081
					);


			// Using AggregateBuilder to assign views 
			AggregateBuilder builder = new AggregateBuilder();
			builder.add(splitter,   "_InitialView", viewName);
			builder.add(tokenizer,  "_InitialView", viewName);
			builder.add(tagger,     "_InitialView", viewName); 
			builder.add(ner,        "_InitialView", viewName); 
			builder.add(parser,     "_InitialView", viewName); 
			
			// Create and run aggregate engine
			AnalysisEngine ae = builder.createAggregate(); 
			ae.process(aJCas); 
		}
		catch (ResourceInitializationException re)
		{
			throw new LAPException("Failed to initilize analysis engine" , re); 
		} 
		catch (AnalysisEngineProcessException e) 
		{
			throw new LAPException("An exception while running the aggregate AE", e); 
		}		
	}

}
