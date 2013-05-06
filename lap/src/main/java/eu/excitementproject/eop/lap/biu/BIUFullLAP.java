package eu.excitementproject.eop.lap.biu;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.factory.AggregateBuilder;

import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.biu.ae.ner.StanfordNamedEntityRecognizerAE;
import eu.excitementproject.eop.lap.biu.ae.parser.EasyFirstParserAE;
import eu.excitementproject.eop.lap.biu.ae.postagger.MaxentPosTaggerAE;
import eu.excitementproject.eop.lap.biu.ae.sentencesplitter.LingPipeSentenceSplitterAE;
import eu.excitementproject.eop.lap.biu.ae.tokenizer.MaxentTokenizerAE;
import eu.excitementproject.eop.lap.lappoc.LAP_ImplBase;

/**
 * BIU's LAP (Linguistic Analysis Pipeline). It fits the requirements of
 * {@link eu.excitementproject.eop.biutee.rteflow.systems.excitement.BiuteeEDA}.
 * 
 * @author Ofer Bronstein
 * @since May 2013
 */
public class BIUFullLAP extends LAP_ImplBase implements LAPAccess {

	public BIUFullLAP(String taggerModelFile, String nerModelFile,
			String parserHost, Integer parserPort) throws LAPException {
		super();
		this.taggerModelFile = taggerModelFile;
		this.nerModelFile = nerModelFile;
		this.parserHost = parserHost;
		this.parserPort = parserPort;
		
		languageIdentifier = "EN"; // set languageIdentifer 
	}
	
	public BIUFullLAP(NameValueTable section) throws LAPException, ConfigurationException {
		this(
			section.getFile(DEFAULT_TAGGER_MODEL_FILE_PARAM).getAbsolutePath(),
			section.getFile(DEFAULT_NER_MODEL_FILE_PARAM).getAbsolutePath(),
			section.getString(DEFAULT_PARSER_HOST_NAME),
			section.getInteger(DEFAULT_PARSER_PORT_NAME)
			);
	}

	public BIUFullLAP(CommonConfig config) throws LAPException, ConfigurationException {
		this(config.getSection(DEFAULT_SECTION_NAME));
	}

	@Override
	public void addAnnotationOn(JCas aJCas, String viewName) throws LAPException {
		try {
			// Build analysis engines
			AnalysisEngineDescription splitter =   createPrimitiveDescription(LingPipeSentenceSplitterAE.class);
			AnalysisEngineDescription tokenizer =  createPrimitiveDescription(MaxentTokenizerAE.class);
			AnalysisEngineDescription tagger =     createPrimitiveDescription(MaxentPosTaggerAE.class,
					MaxentPosTaggerAE.PARAM_MODEL_FILE , taggerModelFile);
			AnalysisEngineDescription ner =        createPrimitiveDescription(StanfordNamedEntityRecognizerAE.class,
					MaxentPosTaggerAE.PARAM_MODEL_FILE , nerModelFile);
			AnalysisEngineDescription parser =     createPrimitiveDescription(EasyFirstParserAE.class,
					EasyFirstParserAE.PARAM_HOST , parserHost,
					EasyFirstParserAE.PARAM_PORT , parserPort
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

	private String taggerModelFile;
	private String nerModelFile;
	private String parserHost;
	private Integer parserPort;
	
	private static final String DEFAULT_SECTION_NAME = "rte_pairs_preprocess";
	private static final String DEFAULT_TAGGER_MODEL_FILE_PARAM = "easyfirst_stanford_pos_tagger";
	private static final String DEFAULT_NER_MODEL_FILE_PARAM = "stanford_ner_classifier_path";
	private static final String DEFAULT_PARSER_HOST_NAME = "easyfirst_host";
	private static final String DEFAULT_PARSER_PORT_NAME = "easyfirst_port";
}
