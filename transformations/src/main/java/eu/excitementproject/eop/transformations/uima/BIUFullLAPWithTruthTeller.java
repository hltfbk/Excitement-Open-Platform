package eu.excitementproject.eop.transformations.uima;
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
import eu.excitementproject.eop.transformations.biu.en.predicatetruth.TruthTellerAnnotatorAE;
import eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames;

/**
 * A class to extend BIUFullLAP with truth annotations
 * This is implemented within the Transformations package in order to avoid circular dependency between packages
 * @author Gabi Stanovsky
 * @since Aug 2014
 */


public class BIUFullLAPWithTruthTeller extends LAP_ImplBaseAE implements LAPAccess {
	
	
	public BIUFullLAPWithTruthTeller(String taggerModelFile, String nerModelFile, String parserHost, Integer parserPort, String truthTellerAnnotationsFile) throws LAPException {
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
			AnalysisEngineDescription truthteller =	createPrimitiveDescription(TruthTellerAnnotatorAE.class,
					TruthTellerAnnotatorAE.PARAM_CONFIG , truthTellerAnnotationsFile);

			AnalysisEngineDescription[] descs = new AnalysisEngineDescription[] {
					splitter,
					tokenizer,
					tagger,
					ner,
					parser,
					truthteller,
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
	
	public BIUFullLAPWithTruthTeller(NameValueTable biuFullLAPSection, NameValueTable truthTellerSection) throws LAPException, ConfigurationException {
		this(
			biuFullLAPSection.getFile(DEFAULT_TAGGER_MODEL_FILE_PARAM).getAbsolutePath(),
			biuFullLAPSection.getFile(DEFAULT_NER_MODEL_FILE_PARAM).getAbsolutePath(),
			biuFullLAPSection.getString(DEFAULT_PARSER_HOST_NAME),
			biuFullLAPSection.getInteger(DEFAULT_PARSER_PORT_NAME),
			truthTellerSection.getFile(TransformationsConfigurationParametersNames.ANNOTATION_RULES_FILE).getAbsolutePath()
			);
	}

	public BIUFullLAPWithTruthTeller(CommonConfig config) throws LAPException, ConfigurationException {
		this(config.getSection(DEFAULT_SECTION_NAME),
			 config.getSection(TransformationsConfigurationParametersNames.TRUTH_TELLER_MODULE_NAME));
	}
	
	private static final String DEFAULT_SECTION_NAME = "rte_pairs_preprocess";
	private static final String DEFAULT_TAGGER_MODEL_FILE_PARAM = "easyfirst_stanford_pos_tagger";
	private static final String DEFAULT_NER_MODEL_FILE_PARAM = "stanford_ner_classifier_path";
	private static final String DEFAULT_PARSER_HOST_NAME = "easyfirst_host";
	private static final String DEFAULT_PARSER_PORT_NAME = "easyfirst_port";
	
}
