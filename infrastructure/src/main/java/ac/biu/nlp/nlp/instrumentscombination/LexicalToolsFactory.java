/**
 * 
 */
package ac.biu.nlp.nlp.instrumentscombination;

import java.io.File;
import java.net.MalformedURLException;

import ac.biu.nlp.nlp.general.configuration.ConfigurationException;
import ac.biu.nlp.nlp.general.configuration.ConfigurationParams;
import ac.biu.nlp.nlp.instruments.lemmatizer.GateLemmatizer;
import ac.biu.nlp.nlp.instruments.lemmatizer.Lemmatizer;
import ac.biu.nlp.nlp.instruments.lemmatizer.LemmatizerException;
import ac.biu.nlp.nlp.instruments.ner.NamedEntityRecognizer;
import ac.biu.nlp.nlp.instruments.ner.NamedEntityRecognizerException;
import ac.biu.nlp.nlp.instruments.ner.stanford.StanfordNamedEntityRecognizer;
import ac.biu.nlp.nlp.instruments.postagger.MaxentPosTagger;
import ac.biu.nlp.nlp.instruments.postagger.OpenNlpPosTagger;
import ac.biu.nlp.nlp.instruments.postagger.PosTagger;
import ac.biu.nlp.nlp.instruments.postagger.PosTaggerException;
import ac.biu.nlp.nlp.instruments.tokenizer.MaxentTokenizer;
import ac.biu.nlp.nlp.instruments.tokenizer.Tokenizer;
import ac.biu.nlp.nlp.instruments.tokenizer.TokenizerException;

/**
 * A factory for {@link Tokenizer}, {@link PosTagger}, {@link Lemmatizer} and {@link NamedEntityRecognizer}.
 * Given a {@link ConfigurationParams}, it creates the appropriate lexical tool. 
 * @author Eyal Shnarch
 * @since 3.9.12
 */
public class LexicalToolsFactory {
	
	/**
	 * Create an initialized Tokenizer using the given configuration params.
	 * Currently supports only {@link MaxentTokenizer}
	 * @throws InstrumentCombinationException
	 */
	public static Tokenizer createTokenizer(ConfigurationParams params) throws InstrumentCombinationException {
		Tokenizer tokenizer = new MaxentTokenizer();
		try {
			tokenizer.init();
		} catch (TokenizerException e) {
			throw new InstrumentCombinationException("Nested exception while initializing the tokenizer", e);
		}
		return tokenizer;
	}
	
	/**
	 * Creates an initialized PosTagger using the given configuration params.
	 * Priority: OpenNLP and then Stanford (since the former is much faster).
	 * @throws InstrumentCombinationException in case the appropriate input 
	 * was not found in the given ConfigurationParams.   
	 */
	public static PosTagger createPosTagger(ConfigurationParams params) throws InstrumentCombinationException {
		PosTagger posTagger = null;
		String stanfordPosTaggerModel = null;
		String opennlpPosTaggerModel = null;
		try {
			if(params.containsKey("opennlp-pos-tagger-model-file-path")){
				opennlpPosTaggerModel = params.get("opennlp-pos-tagger-model-file-path");
				posTagger = new OpenNlpPosTagger(new File(opennlpPosTaggerModel),params.get("opennlp-pos-tag-dictionary-file-path"));
			}else if (params.containsKey("easyfirst_stanford_pos_tagger")){
				stanfordPosTaggerModel = params.get("easyfirst_stanford_pos_tagger");
				posTagger = new MaxentPosTagger(stanfordPosTaggerModel);
			}else{
				throw new InstrumentCombinationException("missing model file for POS tagger");
			}
			posTagger.init();
		} catch (ConfigurationException e) {
			throw new InstrumentCombinationException("Nested exception with configuration file while initializing the POS tagger", e);
		} catch (PosTaggerException e) {
			throw new InstrumentCombinationException("Nested exception while initializing the POS tagger", e);
		}
		
		return posTagger;
	}
	
	/**
	 * Creates an initialized Lemmatizer using the given configuration params.
	 * Currently supports only {@link GateLemmatizer}.
	 * @throws InstrumentCombinationException
	 */
	public static Lemmatizer createLemmatizer(ConfigurationParams params) throws InstrumentCombinationException {
		Lemmatizer lemmatizer;
		try {
			lemmatizer = new GateLemmatizer(new File(params.get("lemmatizer_rule_file")).toURI().toURL());
			lemmatizer.init();
		} catch (MalformedURLException e) {
			throw new InstrumentCombinationException("Nested exception with lemmatizer URL while initializing the lemmatizer", e);
		} catch (LemmatizerException e) {
			throw new InstrumentCombinationException("Nested exception while initializing the lemmatizer", e);
		} catch (ConfigurationException e) {
			throw new InstrumentCombinationException("Nested exception with configuration file while initializing the lemmatizer", e);
		}
		return lemmatizer;
	}
	
	/**
	 * Create an initialized NamedEntityRecognizer using the given configuration params.
	 */
	public static NamedEntityRecognizer createNamedEntityRecognizer(ConfigurationParams params) throws InstrumentCombinationException{
		boolean doNer;
		try {
			doNer = (params.containsKey("do_named_entity_recognition")? params.getBoolean("do_named_entity_recognition"): false);
			if (doNer) {
				NamedEntityRecognizer ner =  new StanfordNamedEntityRecognizer(new File(params.getFile("ner-classifier-path").getAbsolutePath()));
				ner.init();
				return ner;
			} else
				return null;
		} catch (ConfigurationException e) {
			throw new InstrumentCombinationException("Nested exception with configuration file while initializing the NER", e);
		} catch (NamedEntityRecognizerException e) {
			throw new InstrumentCombinationException("Nested exception while initializing the NER", e);
		}
		
	}
}
