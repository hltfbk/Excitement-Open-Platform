package eu.excitementproject.eop.lap.biu.uima;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.biu.uima.ae.ner.StanfordNamedEntityRecognizerAE;
import eu.excitementproject.eop.lap.biu.uima.ae.parser.EasyFirstParserAE;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBaseAE;

/**
 * A lap combining both AEs used in {@link eu.excitementproject.eop.lap.biu.uima.BIUFullLAP}
 * and OpenNlp ones. This is mostly here to display some versatility in constructing different LAPs.
 * Currently, this is the only LAP in EOP (except for BIUFullLAP) that doesn't require TreeTagger,
 * and produces Lemmas and NER information.
 * 
 * @author Ofer Bronstein
 * @since April 2014
 */
public class BIUAndOpenNlpLAP extends LAP_ImplBaseAE implements LAPAccess {

	public BIUAndOpenNlpLAP(String nerModelFile, String parserHost, Integer parserPort) throws LAPException {
		try 
		{
			// Step a) Build analysis engine descriptions
			AnalysisEngineDescription splitter =   createPrimitiveDescription(OpenNlpSegmenter.class);
			AnalysisEngineDescription tagger =     createPrimitiveDescription(OpenNlpPosTagger.class);
			AnalysisEngineDescription ner =        createPrimitiveDescription(StanfordNamedEntityRecognizerAE.class,
														StanfordNamedEntityRecognizerAE.PARAM_MODEL_FILE , nerModelFile);
			AnalysisEngineDescription parser =     createPrimitiveDescription(EasyFirstParserAE.class,
														EasyFirstParserAE.PARAM_HOST , parserHost,
														EasyFirstParserAE.PARAM_PORT , parserPort
														);

			AnalysisEngineDescription[] descs = new AnalysisEngineDescription[] {
					splitter,
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
	
	public BIUAndOpenNlpLAP(NameValueTable section) throws LAPException, ConfigurationException {
		this(
			section.getFile(DEFAULT_NER_MODEL_FILE_PARAM).getAbsolutePath(),
			section.getString(DEFAULT_PARSER_HOST_NAME),
			section.getInteger(DEFAULT_PARSER_PORT_NAME)
			);
	}

	public BIUAndOpenNlpLAP(CommonConfig config) throws LAPException, ConfigurationException {
		this(config.getSection(DEFAULT_SECTION_NAME));
	}
	
	private static final String DEFAULT_SECTION_NAME = "rte_pairs_preprocess";
	private static final String DEFAULT_NER_MODEL_FILE_PARAM = "stanford_ner_classifier_path";
	private static final String DEFAULT_PARSER_HOST_NAME = "easyfirst_host";
	private static final String DEFAULT_PARSER_PORT_NAME = "easyfirst_port";
}
