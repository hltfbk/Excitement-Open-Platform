package eu.excitementproject.eop.lap.biu.uima;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

//import java.util.HashMap;
//import java.util.Map;

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

	public BIUFullLAP(String taggerModelFile, String nerModelFile,
			String parserHost, Integer parserPort) throws LAPException {
				
		// OLD CODE 
		// // super(buildDescriptorArgs(taggerModelFile, nerModelFile, parserHost, parserPort));	
		// // languageIdentifier = "EN"; // set languageIdentifer 
		// END OLD CODE 
		
		// Gil: (temporary) change. Ofer, please check and revise as you see fit :-) 		
		//      Now we no longer uses MAP<>, neither listAEDescriptos. 
		//      Any class that uses LAP_ImplBaseAE only needs to do the following three things 
		//      in its Constructor.  
		
		//  a) prepare AE descriptors --- you can use your constructor arguments freely here. 
		//  b) call initializeViews(descriptors)  --- the method that attaches AAE to Views (defined in ImplBaseAE)
		//  c) set lang ID 
		
		// Step a) prepare AE descriptors 
		// Gil: I copied them from listDescriptors & buildDescriptorArgs, and marked (commented) original lines.  
		//      Note that, here we don't have "first-liner-super-arg-passing" things. 
		//      (It does call super() implicitly -- ImplBaseAE only has default (no arg) constructor. 
		//      I think this is one valid case why Java made implicit (default) call as implicit. 
		//      of course, you can make super() here... 
		
		AnalysisEngineDescription[] descs = null; 
		try 
		{
			// Build analysis engine descriptions
			AnalysisEngineDescription splitter =   createPrimitiveDescription(LingPipeSentenceSplitterAE.class);
			AnalysisEngineDescription tokenizer =  createPrimitiveDescription(MaxentTokenizerAE.class);
//			AnalysisEngineDescription tagger =     createPrimitiveDescription(MaxentPosTaggerAE.class,
//					MaxentPosTaggerAE.PARAM_MODEL_FILE , args.get(ARGNAME_MAXENT_TAGGER_MODEL_FILE));
			AnalysisEngineDescription tagger =     createPrimitiveDescription(MaxentPosTaggerAE.class,
			MaxentPosTaggerAE.PARAM_MODEL_FILE , taggerModelFile);
			
//			AnalysisEngineDescription ner =        createPrimitiveDescription(StanfordNamedEntityRecognizerAE.class,
//					StanfordNamedEntityRecognizerAE.PARAM_MODEL_FILE , args.get(ARGNAME_STANFORD_NER_MODEL_FILE));
			AnalysisEngineDescription ner =        createPrimitiveDescription(StanfordNamedEntityRecognizerAE.class,
			StanfordNamedEntityRecognizerAE.PARAM_MODEL_FILE , nerModelFile);

//			AnalysisEngineDescription parser =     createPrimitiveDescription(EasyFirstParserAE.class,
//					EasyFirstParserAE.PARAM_HOST , args.get(ARGNAME_EASYFIRST_HOST),
//					EasyFirstParserAE.PARAM_PORT , Integer.parseInt(args.get(ARGNAME_EASYFIRST_PORT))
//					);
			AnalysisEngineDescription parser =     createPrimitiveDescription(EasyFirstParserAE.class,
			EasyFirstParserAE.PARAM_HOST , parserHost,
			EasyFirstParserAE.PARAM_PORT , parserPort
			);

			descs = new AnalysisEngineDescription[] {
					splitter,
					tokenizer,
					tagger,
					ner,
					parser,
			};
		}
		catch (ResourceInitializationException e)
		{
			throw new LAPException("Unable to create AE descriptions", e); 
		}
		
		// Step b) call initializeViews() 
		// Gil: Note that ImplBaseAE no longer uses/requires listAEDescripts, but 
		//      the implementations are now expected to call initializeView() in the constructor. 
		//      Two methods are provided: one with default ViewNames (Text, Hypothesis, InitialView) 
		//      the other with full flexibility to set any View names. 
		
		// initialize view with EOP default views. 
		initializeViews(descs); 
		
		// Step c) set lang ID 
		languageIdentifier = "EN";		
		
		
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

//  Gil: commented -- no longer needed with new constructors. 
//	private static String name(Class<?> cls, String paramName) {
//		return cls.getName() + "." + paramName;
//	}

	
//  Gil: commented -- no longer needed with new constructors. 
//	private static Map<String,String> buildDescriptorArgs(String taggerModelFile, String nerModelFile, String parserHost, Integer parserPort) {
//		HashMap<String,String> args = new HashMap<String,String>();
//		
//		args.put(ARGNAME_MAXENT_TAGGER_MODEL_FILE, taggerModelFile);
//		args.put(ARGNAME_STANFORD_NER_MODEL_FILE, nerModelFile);
//		args.put(ARGNAME_EASYFIRST_HOST, parserHost);
//		args.put(ARGNAME_EASYFIRST_PORT, parserPort.toString());
//		
//		return args;
//	}
	
//	@Override
//	public AnalysisEngineDescription[] listAEDescriptors(Map<String,String> args) throws LAPException {
//		try 
//		{
//			// Build analysis engine descriptions
//			AnalysisEngineDescription splitter =   createPrimitiveDescription(LingPipeSentenceSplitterAE.class);
//			AnalysisEngineDescription tokenizer =  createPrimitiveDescription(MaxentTokenizerAE.class);
//			AnalysisEngineDescription tagger =     createPrimitiveDescription(MaxentPosTaggerAE.class,
//					MaxentPosTaggerAE.PARAM_MODEL_FILE , args.get(ARGNAME_MAXENT_TAGGER_MODEL_FILE));
//			AnalysisEngineDescription ner =        createPrimitiveDescription(StanfordNamedEntityRecognizerAE.class,
//					StanfordNamedEntityRecognizerAE.PARAM_MODEL_FILE , args.get(ARGNAME_STANFORD_NER_MODEL_FILE));
//			AnalysisEngineDescription parser =     createPrimitiveDescription(EasyFirstParserAE.class,
//					EasyFirstParserAE.PARAM_HOST , args.get(ARGNAME_EASYFIRST_HOST),
//					EasyFirstParserAE.PARAM_PORT , Integer.parseInt(args.get(ARGNAME_EASYFIRST_PORT))
//					);
//			
//			AnalysisEngineDescription[] descs = new AnalysisEngineDescription[] {
//					splitter,
//					tokenizer,
//					tagger,
//					ner,
//					parser,
//			};
//			return descs;
//		}
//		catch (ResourceInitializationException e)
//		{
//			throw new LAPException("Unable to create AE descriptions", e); 
//		}
//	}

//  Gil: commented -- no longer needed with new constructors. 
//	private static final String ARGNAME_MAXENT_TAGGER_MODEL_FILE = name(MaxentPosTaggerAE.class, MaxentPosTaggerAE.PARAM_MODEL_FILE);
//	private static final String ARGNAME_STANFORD_NER_MODEL_FILE = name(StanfordNamedEntityRecognizerAE.class, StanfordNamedEntityRecognizerAE.PARAM_MODEL_FILE);
//	private static final String ARGNAME_EASYFIRST_HOST = name(EasyFirstParserAE.class, EasyFirstParserAE.PARAM_HOST);
//	private static final String ARGNAME_EASYFIRST_PORT = name(EasyFirstParserAE.class, EasyFirstParserAE.PARAM_PORT);
	
	private static final String DEFAULT_SECTION_NAME = "rte_pairs_preprocess";
	private static final String DEFAULT_TAGGER_MODEL_FILE_PARAM = "easyfirst_stanford_pos_tagger";
	private static final String DEFAULT_NER_MODEL_FILE_PARAM = "stanford_ner_classifier_path";
	private static final String DEFAULT_PARSER_HOST_NAME = "easyfirst_host";
	private static final String DEFAULT_PARSER_PORT_NAME = "easyfirst_port";
}
