package ac.biu.nlp.nlp.engineml.rteflow.systems.gui;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames.PREPROCESS_DO_NER;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames.PREPROCESS_DO_TEXT_NORMALIZATION;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames.RTE_PAIRS_PREPROCESS_MODULE_NAME;
import ac.biu.nlp.nlp.engineml.generic.truthteller.AnnotatorException;
import ac.biu.nlp.nlp.engineml.rteflow.preprocess.Instruments;
import ac.biu.nlp.nlp.engineml.rteflow.preprocess.InstrumentsFactory;
import ac.biu.nlp.nlp.engineml.rteflow.systems.TESystemEnvironment;
import ac.biu.nlp.nlp.engineml.rteflow.systems.rtepairs.ExtendedPairData;
import ac.biu.nlp.nlp.engineml.rteflow.systems.rtepairs.PairData;
import ac.biu.nlp.nlp.engineml.rteflow.systems.rtepairs.PairDataToExtendedPairDataConverter;
import ac.biu.nlp.nlp.engineml.utilities.ShortMessageFire;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.ParserRunException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFileDuplicateKeyException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.TextHypothesisPair;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessorException;
import eu.excitementproject.eop.lap.biu.en.coreference.CoreferenceResolutionException;
import eu.excitementproject.eop.lap.biu.en.ner.NamedEntityRecognizerException;
import eu.excitementproject.eop.lap.biu.en.sentencesplit.SentenceSplitterException;


/**
 * 
 * TO-DO (comment by Asher Stern): GUI code is not of high quality and
 * should be improved. Need to re-design, make it more modular,
 * adding documentation and improve code.
 * 
 * @author Asher Stern
 * @since May 24, 2011
 *
 */
public class SinglePairPreProcessor
{
	public SinglePairPreProcessor(String text, String hypothesis,
			String configurationFileName, ShortMessageFire messageFire,
			TESystemEnvironment teSystemEnvironment)
	{
		super();
		this.text = text;
		this.hypothesis = hypothesis;
		this.configurationFileName = configurationFileName;
		this.messageFire = messageFire;
		this.teSystemEnvironment = teSystemEnvironment;
	}
	

	public void setTextAndHypothesis(String text, String hypothesis)
	{
		this.text = text;
		this.hypothesis = hypothesis;
	}


	public void setTaskName(String taskName)
	{
		this.taskName = taskName;
	}


	public void preprocess() throws NumberFormatException, ConfigurationFileDuplicateKeyException, ConfigurationException, TeEngineMlException, ParserRunException, NamedEntityRecognizerException, TextPreprocessorException, SentenceSplitterException, CoreferenceResolutionException, TreeCoreferenceInformationException, TreeStringGeneratorException, AnnotatorException
	{
		if (!areInstrumentsInitialized)
		{
			messageFire.fire("Initializing instruments");
			readConfigurationFile();
			initializeInstruments();
		}
		TextHypothesisPair thPair = new TextHypothesisPair(this.text, this.hypothesis, 1, this.taskName);
		
		ac.biu.nlp.nlp.engineml.rteflow.systems.rtepairs.SinglePairPreProcessor singlePreprocessor =
			new ac.biu.nlp.nlp.engineml.rteflow.systems.rtepairs.SinglePairPreProcessor(
					thPair,
					doTextNormalization, doNer, instruments, this.messageFire
					);
		
		singlePreprocessor.preprocess();
		
		PairData englishPairData = new PairData(thPair, singlePreprocessor.getTextTrees(), singlePreprocessor.getHypothesisTree(), singlePreprocessor.getMapTreesToSentences(), singlePreprocessor.getCoreferenceInformation());
		
		PairDataToExtendedPairDataConverter converter = new PairDataToExtendedPairDataConverter(englishPairData, teSystemEnvironment);
		converter.convert();
		this.pairData = converter.getExtendedPairData();
	}
	
	
	
	
	
	
	public ExtendedPairData getPairData()
	{
		return pairData;
	}






	private void readConfigurationFile() throws ConfigurationFileDuplicateKeyException, ConfigurationException, NumberFormatException, TeEngineMlException, ParserRunException, NamedEntityRecognizerException, TextPreprocessorException
	{
		configurationFile = new ConfigurationFile(this.configurationFileName);
		configurationFile.setExpandingEnvironmentVariables(true);
		ConfigurationParams params = configurationFile.getModuleConfiguration(RTE_PAIRS_PREPROCESS_MODULE_NAME);
		instruments = new InstrumentsFactory().getDefaultInstruments(params);
		if (params.containsKey(PREPROCESS_DO_NER))
		{
			doNer = params.getBoolean(PREPROCESS_DO_NER);
		}
		else
		{
			doNer = true;
		}
		if (params.containsKey(PREPROCESS_DO_TEXT_NORMALIZATION))
		{
			doTextNormalization = params.getBoolean(PREPROCESS_DO_TEXT_NORMALIZATION);
		}
		else
		{
			doTextNormalization=true;
		}
	}
	
	private void initializeInstruments() throws NamedEntityRecognizerException, ParserRunException, CoreferenceResolutionException
	{
		if (doNer)
		{
			instruments.getNamedEntityRecognizer().init();
		}
		this.instruments.getParser().init();
		this.instruments.getCoreferenceResolver().init();
		this.areInstrumentsInitialized=true;
	}
	

	
	private String text;
	private String hypothesis;
	private String configurationFileName;
	private ShortMessageFire messageFire;
	private TESystemEnvironment teSystemEnvironment;
	
	
	private String taskName = VisualTracingTool.IGNORE_TASK_NAME_STRING;
	
	private ExtendedPairData pairData;
	
	private ConfigurationFile configurationFile;
	private Instruments<Info,BasicNode> instruments;
	private boolean areInstrumentsInitialized = false;
	private boolean doNer = true;
	private boolean doTextNormalization = true;


}
