package ac.biu.nlp.nlp.engineml.rteflow.preprocess;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames.PREPROCESS_BART_PORT;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames.PREPROCESS_BART_SERVER;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames.PREPROCESS_COREFERENCE_RESOLUTION_ENGINE;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames.PREPROCESS_DO_NER;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames.PREPROCESS_STANFORD_NE_CLASSIFIER_PATH;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.Constants.USE_NUMBER_NORMALIZER;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ac.biu.nlp.nlp.engineml.codeannotations.Workaround;
import ac.biu.nlp.nlp.engineml.rteflow.systems.Constants.Workarounds;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.engineml.utilities.preprocess.AddPunctuationTextPreprocessor;
import ac.biu.nlp.nlp.engineml.utilities.preprocess.DummyCoreferenceResolver;
import ac.biu.nlp.nlp.engineml.utilities.preprocess.HandleAllCapitalTextPreprocessor;
import ac.biu.nlp.nlp.engineml.utilities.preprocess.ListOfTextPreprocessors;
import ac.biu.nlp.nlp.engineml.utilities.preprocess.NewNormalizerBasedTextPreProcessor;
import ac.biu.nlp.nlp.engineml.utilities.preprocess.ParserFactory;
import ac.biu.nlp.nlp.engineml.utilities.preprocess.WorkaroundTextPreprocessor;
import ac.biu.nlp.nlp.instruments.coreference.CoreferenceResolver;
import ac.biu.nlp.nlp.instruments.coreference.arkref.ArkrefClient.ArkrefClientException;
import ac.biu.nlp.nlp.instruments.coreference.arkref.ArkrefCoreferenceResolver;
import ac.biu.nlp.nlp.instruments.coreference.arkrefbart.ArkrefAndBartCoreferenceResolver;
import ac.biu.nlp.nlp.instruments.coreference.bart.BartCoreferenceResolver;
import ac.biu.nlp.nlp.instruments.ner.NamedEntityPhrase;
import ac.biu.nlp.nlp.instruments.ner.NamedEntityRecognizer;
import ac.biu.nlp.nlp.instruments.ner.NamedEntityRecognizerException;
import ac.biu.nlp.nlp.instruments.ner.NamedEntityWord;
import ac.biu.nlp.nlp.instruments.ner.stanford.StanfordNamedEntityRecognizer;
import ac.biu.nlp.nlp.instruments.sentencesplit.SentenceSplitter;
import ac.biu.nlp.nlp.instruments.sentencesplit.nagel.NagelSentenceSplitter;
import ac.biu.nlp.nlp.instruments.tokenizer.Tokenizer;
import ac.biu.nlp.nlp.instruments.tokenizer.TokenizerException;
import eu.excitementproject.eop.common.representation.parse.BasicParser;
import eu.excitementproject.eop.common.representation.parse.ParserRunException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessor;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessorException;

/**
 * This class implements {@link Instruments} with default set of instruments.
 * Don't use this class directly. Use {@link InstrumentsFactory}.
 * 
 * @author Asher Stern
 * @since Feb 26, 2011
 *
 */
public class DefaultInstruments implements Instruments<Info, BasicNode>
{
	@Workaround("The workaround here is due to the usage of WorkaroundTextPreprocessor.")
	public static DefaultInstruments fromConfigurationFile(ConfigurationParams params) throws ConfigurationException, TeEngineMlException, NumberFormatException, ParserRunException, NamedEntityRecognizerException, TextPreprocessorException
	{
		// prepare the parameters for the constructor:
		// public DefaultInstruments(ConfigurationParams params, String stanfordNerClassifier, CoreferenceEngineChoice corefChoice, String bartServer, Integer bartPort, TextPreprocessor textProcessor)
		
		// If using BART for coreference - resolve BART-server and BART-port.
		CoreferenceEngineChoice corefChoice = CoreferenceEngineChoice.NONE;
		String bartServer = null;
		String bartPort  = null;
		if (params.containsKey(PREPROCESS_COREFERENCE_RESOLUTION_ENGINE))
		{
			corefChoice = params.getEnum(CoreferenceEngineChoice.class, PREPROCESS_COREFERENCE_RESOLUTION_ENGINE);
		}
		if (corefChoice.equals(CoreferenceEngineChoice.BART) || corefChoice.equals(CoreferenceEngineChoice.ARKREF_AND_BART))
		{
			bartServer = params.get(PREPROCESS_BART_SERVER);
			if (params.containsKey(PREPROCESS_BART_PORT))
			{
				bartPort = params.get(PREPROCESS_BART_PORT);
			}
			else
			{
				bartPort = null;
			}
		}
		
		// TextPreprocessor is built here, not in the constructor.
		// Create a list of TextPreprocessors, and fill it with actual TextProprocessors.
		ArrayList<TextPreprocessor> listPreprocessors = new ArrayList<TextPreprocessor>();
		
		// Number normalizer
		if (USE_NUMBER_NORMALIZER)
		{
			NewNormalizerBasedTextPreProcessor normalizerTextPreprocessor = new NewNormalizerBasedTextPreProcessor(params);
			listPreprocessors.add(normalizerTextPreprocessor);
		}
		else
		{
			logger.warn("Number normalizer is not used!");
		}
		
		// Preprocessor that converts to lower-case sentences that are only-upper-case.
		// Document-titles are usually only-upper-case.
		HandleAllCapitalTextPreprocessor handleAllCapitalTextPreprocessor = new HandleAllCapitalTextPreprocessor();
		listPreprocessors.add(handleAllCapitalTextPreprocessor);
		
		AddPunctuationTextPreprocessor addPunctuationTextPreprocessor = new AddPunctuationTextPreprocessor();
		listPreprocessors.add(addPunctuationTextPreprocessor);
		
		if (Workarounds.USE_WORKAROUND_TEXT_PROCESSOR)
		{
			listPreprocessors.add(new WorkaroundTextPreprocessor());
		}
		listPreprocessors.trimToSize();
		
		// List<TextPreprocessor> listPreprocessors = Arrays.asList(arrayOfTextPreprocessors);
		ListOfTextPreprocessors listOfTextPreprocessors = new ListOfTextPreprocessors(listPreprocessors);

		// The path for Stanford-NER classifier.
		String stanfordNERClassifierPath = params.getFile(PREPROCESS_STANFORD_NE_CLASSIFIER_PATH).getAbsolutePath();
		boolean doNer = true;
		if (params.containsKey(PREPROCESS_DO_NER))
		{
			doNer = params.getBoolean(PREPROCESS_DO_NER);
		}
		else
		{
			doNer = true;
		}
		
		
		return new DefaultInstruments(params, doNer?stanfordNERClassifierPath:null, corefChoice, bartServer, (bartPort==null)?null:Integer.parseInt(bartPort), listOfTextPreprocessors);
	}
	
	public DefaultInstruments(ConfigurationParams params, String stanfordNerClassifier, CoreferenceEngineChoice corefChoice, String bartServer, Integer bartPort, TextPreprocessor textProcessor) throws TeEngineMlException, ParserRunException, NamedEntityRecognizerException, ConfigurationException
	{
		// Construct the parser.
		this.parser = ParserFactory.getParser(params);
		
		// Construct NER.
		if (stanfordNerClassifier!=null)
		{
			this.namedEntityRecognizer = new StanfordNamedEntityRecognizer(new File(stanfordNerClassifier));
		}
		else
		{
			this.namedEntityRecognizer = new DummyNer();
		}
		
		// Construct sentence-splitter.
		this.sentenceSplitter = new NagelSentenceSplitter();
		//this.sentenceSplitter = new MorphAdornerSentenceSplitter();
		
		
		// Construct coreference-resolver.
		switch (corefChoice)
		{
		case BART:
			if (null==bartServer)
			{
				throw new TeEngineMlException(CoreferenceEngineChoice.BART+" was given as the \""+PREPROCESS_COREFERENCE_RESOLUTION_ENGINE+"\" in the config file, but no \""+PREPROCESS_BART_SERVER+"\" was given"); 
			}
			else
			{
				logger.info("Using BART coreference resolver");
				if (null==bartPort)
				{
					this.coreferenceResolver = new BartCoreferenceResolver(bartServer);
				}
				else
				{
					this.coreferenceResolver = new BartCoreferenceResolver(bartServer, String.valueOf(bartPort));
				}
			}
			break;
		case ARKREF:
			logger.info("Using ArkRef coreference resolver");
			try {this.coreferenceResolver = new ArkrefCoreferenceResolver();} 
			catch (ArkrefClientException e) {	throw new TeEngineMlException("Could not construct a new ArkrefCoreferenceResolver", e); }
			catch (IOException e) {	throw new TeEngineMlException("Could not construct a new ArkrefCoreferenceResolver", e); }
			break;
		case ARKREF_AND_BART:
			logger.info("Using ArkRef_and_Bart coreference resolver");
			try {	this.coreferenceResolver = new ArkrefAndBartCoreferenceResolver(bartServer, bartPort != null ? String.valueOf(bartPort) : null); }
			catch (ArkrefClientException e) {	throw new TeEngineMlException("Could not construct a new ArkrefCoreferenceResolver", e); }
			catch (IOException e) {	throw new TeEngineMlException("Could not construct a new ArkrefCoreferenceResolver", e); }
			break;
		case NONE:
		default:
			logger.warn("Not using coreference resolver!");
			this.coreferenceResolver = new DummyCoreferenceResolver();
			break;
		}
		
		// TextPreprocessor is already constructed and was given as
		// parameter to the constructor.
		this.textProcessor = textProcessor;
	}

	public BasicParser getParser()
	{
		return this.parser;
	}

	public NamedEntityRecognizer getNamedEntityRecognizer()
	{
		return this.namedEntityRecognizer;
	}

	public CoreferenceResolver<BasicNode> getCoreferenceResolver()
	{
		return this.coreferenceResolver;
	}

	public SentenceSplitter getSentenceSplitter()
	{
		return this.sentenceSplitter;
	}
	
	public TextPreprocessor getTextPreprocessor()
	{
		return this.textProcessor;
	}
	
	// DUMMY NER
	private static class DummyNer implements NamedEntityRecognizer
	{
		public void init() throws NamedEntityRecognizerException { // No initialization 
		}
		public void cleanUp(){ // No cleanup
		}

		public void setSentence(List<String> sentence)
		{
			this.sentence = sentence;
		}
		
		public void setSentence(String sentence, Tokenizer tokenizer)	throws TokenizerException {
			tokenizer.setSentence(sentence);
			tokenizer.tokenize();
			setSentence(tokenizer.getTokenizedSentence());
		}
		
		public void recognize() throws NamedEntityRecognizerException
		{
			if (null==sentence) throw new NamedEntityRecognizerException("null sentence or sentence not set.");
			this.listOfEntities = new ArrayList<NamedEntityWord>(sentence.size());
			this.mapOfEntities = new HashMap<Integer, NamedEntityPhrase>();
			for (String word : sentence)
			{
				listOfEntities.add(new NamedEntityWord(word, null));
			}
		}
		
		public List<NamedEntityWord> getAnnotatedSentence()
		{
			return this.listOfEntities;
		}

		public Map<Integer, NamedEntityPhrase> getAnnotatedEntities() {
			return mapOfEntities;
		}
	
		private List<NamedEntityWord> listOfEntities;
		private Map<Integer, NamedEntityPhrase> mapOfEntities;

		private List<String> sentence;
	}
	
	protected BasicParser parser;
	protected CoreferenceResolver<BasicNode> coreferenceResolver;
	protected NamedEntityRecognizer namedEntityRecognizer;
	protected SentenceSplitter sentenceSplitter;
	protected TextPreprocessor textProcessor;
	
	
	private static Logger logger = Logger.getLogger(DefaultInstruments.class);
}
