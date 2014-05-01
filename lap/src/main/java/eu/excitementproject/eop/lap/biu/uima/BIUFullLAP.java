package eu.excitementproject.eop.lap.biu.uima;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;

import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.biu.uima.ae.ner.StanfordNamedEntityRecognizerAE;
import eu.excitementproject.eop.lap.biu.uima.ae.parser.EasyFirstParserAE;
import eu.excitementproject.eop.lap.biu.uima.ae.postagger.MaxentPosTaggerAE;
import eu.excitementproject.eop.lap.biu.uima.ae.sentencesplitter.LingPipeSentenceSplitterAE;
import eu.excitementproject.eop.lap.biu.uima.ae.tokenizer.MaxentTokenizerAE;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBaseAE;

/**
 * BIU's LAP (Linguistic Analysis Pipeline). It fits the requirements of
 * {@link eu.excitementproject.eop.biutee.rteflow.systems.excitement.BiuteeEDA}.
 * 
 * @author Ofer Bronstein
 * @since May 2013
 */
public class BIUFullLAP extends LAP_ImplBaseAE implements LAPAccess {

	public BIUFullLAP(String taggerModelFile, String nerModelFile, String parserHost, Integer parserPort) throws LAPException {
		try 
		{
			// Step a) Build analysis engine descriptions
			AnalysisEngineDescription splitter =   createPrimitiveDescription(LingPipeSentenceSplitterAE.class);
			AnalysisEngineDescription tokenizer =  createPrimitiveDescription(MaxentTokenizerAE.class);
			AnalysisEngineDescription tagger =     createPrimitiveDescription(MaxentPosTaggerAE.class,
														MaxentPosTaggerAE.PARAM_MODEL_FILE , taggerModelFile);
			AnalysisEngineDescription ner =        createPrimitiveDescription(StanfordNamedEntityRecognizerAE.class,
														StanfordNamedEntityRecognizerAE.PARAM_MODEL_FILE , nerModelFile);
			AnalysisEngineDescription parser =     createPrimitiveDescription(EasyFirstParserAE.class,
														EasyFirstParserAE.PARAM_HOST , parserHost,
														EasyFirstParserAE.PARAM_PORT , parserPort
														);

			AnalysisEngineDescription[] descs = new AnalysisEngineDescription[] {
					splitter,
					tokenizer,
					tagger,
					ner,
					parser,
			};

			// Step b) call initializeViews() 
			// initialize view with EOP default views. 
			initializeViews(descs); 
			
			// Step c) set lang ID 
			languageIdentifier = "EN";		
		}
		catch (ResourceInitializationException e)
		{
			throw new LAPException(e); 
		}
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
	
	private static final String DEFAULT_SECTION_NAME = "rte_pairs_preprocess";
	private static final String DEFAULT_TAGGER_MODEL_FILE_PARAM = "easyfirst_stanford_pos_tagger";
	private static final String DEFAULT_NER_MODEL_FILE_PARAM = "stanford_ner_classifier_path";
	private static final String DEFAULT_PARSER_HOST_NAME = "easyfirst_host";
	private static final String DEFAULT_PARSER_PORT_NAME = "easyfirst_port";
}
