package eu.excitementproject.eop.biutee.rteflow.preprocess;
import static eu.excitementproject.eop.biutee.utilities.BiuteeConstants.USE_NUMBER_NORMALIZER;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.PREPROCESS_BART_PORT;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.PREPROCESS_BART_SERVER;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.PREPROCESS_COREFERENCE_RESOLUTION_ENGINE;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.PREPROCESS_DO_NER;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.PREPROCESS_STANFORD_NE_CLASSIFIER_PATH;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.PREPROCESS_SENTENCE_SPLITTER_TYPE;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.biutee.utilities.preprocess.AddPunctuationTextPreprocessor;
import eu.excitementproject.eop.biutee.utilities.preprocess.DummyCoreferenceResolver;
import eu.excitementproject.eop.biutee.utilities.preprocess.HandleAllCapitalTextPreprocessor;
import eu.excitementproject.eop.biutee.utilities.preprocess.ListOfTextPreprocessors;
import eu.excitementproject.eop.biutee.utilities.preprocess.NewNormalizerBasedTextPreProcessor;
import eu.excitementproject.eop.biutee.utilities.preprocess.ParserFactory;
import eu.excitementproject.eop.biutee.utilities.preprocess.WorkaroundTextPreprocessor;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessor;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessorException;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolver;
import eu.excitementproject.eop.lap.biu.en.coreference.arkref.ArkrefClient.ArkrefClientException;
import eu.excitementproject.eop.lap.biu.en.coreference.arkref.ArkrefCoreferenceResolver;
import eu.excitementproject.eop.lap.biu.en.coreference.arkrefbart.ArkrefAndBartCoreferenceResolver;
import eu.excitementproject.eop.lap.biu.en.coreference.arkreffiles.ArkrefFilesCoreferenceResolver;
import eu.excitementproject.eop.lap.biu.en.coreference.bart.BartCoreferenceResolver;
import eu.excitementproject.eop.lap.biu.en.ner.stanford.StanfordNamedEntityRecognizer;
import eu.excitementproject.eop.lap.biu.en.parser.BasicParser;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lap.biu.en.sentencesplit.LingPipeSentenceSplitter;
import eu.excitementproject.eop.lap.biu.en.sentencesplit.MorphAdornerSentenceSplitter;
import eu.excitementproject.eop.lap.biu.en.sentencesplit.nagel.NagelSentenceSplitter;
import eu.excitementproject.eop.lap.biu.en.tokenizer.Tokenizer;
import eu.excitementproject.eop.lap.biu.en.tokenizer.TokenizerException;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityPhrase;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityRecognizer;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityRecognizerException;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityWord;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitter;
import eu.excitementproject.eop.transformations.codeannotations.Workaround;
import eu.excitementproject.eop.transformations.utilities.GlobalMessages;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

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
		
		if (BiuteeConstants.Workarounds.USE_WORKAROUND_TEXT_PROCESSOR)
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
		SentenceSplitterType sentenceSplitterType = params.getEnum(SentenceSplitterType.class, PREPROCESS_SENTENCE_SPLITTER_TYPE);
		logger.info("Using "+sentenceSplitterType.name()+" sentence splitter.");
		switch(sentenceSplitterType)
		{
		case NAGEL:
			this.sentenceSplitter = new NagelSentenceSplitter();
			break;
		case MORPH_ADORNER:
			this.sentenceSplitter = new MorphAdornerSentenceSplitter();
			break;
		case LING_PIPE:
			this.sentenceSplitter = new LingPipeSentenceSplitter();
			break;
		}
		
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
			GlobalMessages.globalWarn("Using an old wrapper for ArkRef. This wrapper might be buggy. It is recommended to use a newer wrapper, by setting the enum-constant "+CoreferenceEngineChoice.ARKREF_FILES.name()+" in the configuration file.", logger);
			try {this.coreferenceResolver = new ArkrefCoreferenceResolver();} 
			catch (ArkrefClientException e) {	throw new TeEngineMlException("Could not construct a new ArkrefCoreferenceResolver", e); }
			catch (IOException e) {	throw new TeEngineMlException("Could not construct a new ArkrefCoreferenceResolver", e); }
			break;
		case ARKREF_AND_BART:
			logger.info("Using ArkRef_and_Bart coreference resolver");
			GlobalMessages.globalWarn("Co-reference resolver is configured as "+CoreferenceEngineChoice.ARKREF_AND_BART.name()+". Note that this configuration uses an older wrapper for ArkRef, which is known to be buggy. It is recommended to use the "+CoreferenceEngineChoice.ARKREF_FILES.name()+" configuration.",logger);
			try {	this.coreferenceResolver = new ArkrefAndBartCoreferenceResolver(bartServer, bartPort != null ? String.valueOf(bartPort) : null); }
			catch (ArkrefClientException e) {	throw new TeEngineMlException("Could not construct a new ArkrefCoreferenceResolver", e); }
			catch (IOException e) {	throw new TeEngineMlException("Could not construct a new ArkrefCoreferenceResolver", e); }
			break;
		case ARKREF_FILES:
			logger.info("Using ArkRef coreference resolver by running it as a separate process.");
			this.coreferenceResolver = new ArkrefFilesCoreferenceResolver(); 
			break;
		case NONE:
		default:
			GlobalMessages.globalWarn("Not using coreference resolver!",logger);
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
