package ac.biu.nlp.nlp.instruments.factory;

import ac.biu.nlp.nlp.instruments.dictionary.wordnet.Dictionary;
import ac.biu.nlp.nlp.instruments.dictionary.wordnet.WordNetException;
import ac.biu.nlp.nlp.instruments.ner.NamedEntityRecognizer;
import ac.biu.nlp.nlp.instruments.ner.NamedEntityRecognizerException;
import ac.biu.nlp.nlp.instruments.parse.BasicParser;
import ac.biu.nlp.nlp.instruments.parse.ParserRunException;
import ac.biu.nlp.nlp.instruments.sentencesplit.SentenceSplitter;
import ac.biu.nlp.nlp.instruments.sentencesplit.SentenceSplitterException;

public interface Factory
{
	public SentenceSplitter newSentenceSplitter() throws SentenceSplitterException;
	
	public BasicParser newParser() throws ParserRunException;
	
	public NamedEntityRecognizer newNamedEntityRecognizer() throws NamedEntityRecognizerException;
	
	
	
	
	
	/**
	 * Returns a WordNet {@linkplain Dictionary}, either a new one, or
	 * an existing one, if that {@linkplain Dictionary} is thread-safe.
	 * 
	 * @return
	 * @throws WordNetException
	 */
	public Dictionary getWordnetDictionary() throws WordNetException;
	
	
	

}
