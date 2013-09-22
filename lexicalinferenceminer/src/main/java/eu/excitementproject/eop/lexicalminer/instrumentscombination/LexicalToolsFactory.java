/**
 * 
 */
package eu.excitementproject.eop.lexicalminer.instrumentscombination;

import java.io.File;
import java.net.MalformedURLException;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.lap.biu.en.lemmatizer.gate.GateLemmatizer;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.lap.biu.lemmatizer.LemmatizerException;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityRecognizer;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityRecognizerException;
import eu.excitementproject.eop.lap.biu.en.ner.stanford.StanfordNamedEntityRecognizer;
//import eu.excitementproject.eop.lap.biu.en.old_opennlp_1_3_0.OpenNlpPosTagger;
import eu.excitementproject.eop.lap.biu.en.postagger.stanford.MaxentPosTagger;
import eu.excitementproject.eop.lap.biu.postagger.PosTagger;
import eu.excitementproject.eop.lap.biu.postagger.PosTaggerException;
import eu.excitementproject.eop.lap.biu.en.tokenizer.MaxentTokenizer;
import eu.excitementproject.eop.lap.biu.en.tokenizer.Tokenizer;
import eu.excitementproject.eop.lap.biu.en.tokenizer.TokenizerException;

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
	 * Ofer Bronstein 29/1/13: OpenNLPPosTagger of version 1.3.0 is no longer supported
	 * @throws InstrumentCombinationException in case the appropriate input 
	 * was not found in the given ConfigurationParams.   
	 */
	public static PosTagger createPosTagger(ConfigurationParams params) throws InstrumentCombinationException {
		PosTagger posTagger = null;
		String stanfordPosTaggerModel = null;
//		String opennlpPosTaggerModel = null;
		try {
//			if(params.containsKey("opennlp-pos-tagger-model-file-path")){
//				opennlpPosTaggerModel = params.get("opennlp-pos-tagger-model-file-path");
//				posTagger = new OpenNlpPosTagger(new File(opennlpPosTaggerModel),params.get("opennlp-pos-tag-dictionary-file-path"));
//			}else
			if (params.containsKey("easyfirst_stanford_pos_tagger")){
				stanfordPosTaggerModel = params.get("easyfirst_stanford_pos_tagger");
				posTagger = new MaxentPosTagger(stanfordPosTaggerModel);
			}else{
				throw new InstrumentCombinationException("missing model file for POS tagger (param 'easyfirst_stanford_pos_tagger')");
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
