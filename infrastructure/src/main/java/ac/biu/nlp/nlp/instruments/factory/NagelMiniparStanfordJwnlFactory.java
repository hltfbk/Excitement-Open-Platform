package ac.biu.nlp.nlp.instruments.factory;

import java.io.File;

import ac.biu.nlp.nlp.instruments.dictionary.wordnet.Dictionary;
import ac.biu.nlp.nlp.instruments.dictionary.wordnet.WordNetException;
import ac.biu.nlp.nlp.instruments.dictionary.wordnet.ext_jwnl.ExtJwnlDictionary;
import ac.biu.nlp.nlp.instruments.ner.NamedEntityRecognizer;
import ac.biu.nlp.nlp.instruments.ner.NamedEntityRecognizerException;
import ac.biu.nlp.nlp.instruments.ner.stanford.StanfordNamedEntityRecognizer;
import ac.biu.nlp.nlp.instruments.parse.BasicParser;
import ac.biu.nlp.nlp.instruments.parse.ParserRunException;
import ac.biu.nlp.nlp.instruments.parse.minipar.MiniparParser;
import ac.biu.nlp.nlp.instruments.sentencesplit.SentenceSplitter;
import ac.biu.nlp.nlp.instruments.sentencesplit.SentenceSplitterException;
import ac.biu.nlp.nlp.instruments.sentencesplit.nagel.NagelSentenceSplitter;

public class NagelMiniparStanfordJwnlFactory implements Factory
{
	public NagelMiniparStanfordJwnlFactory(
			String miniparDataDir,
			File classifierPath,
//			JwnlDictionaryManagementType dictionaryType ,JwnlDictionarySupportedVersion dictionaryVersion ,
			File wordnetDictDir
			)
	{
		  this.miniparDataDir = miniparDataDir;
		  this.stanfordClassifierPath = classifierPath;
//		  this.dictionaryType = dictionaryType;
//		  this.dictionaryVersion = dictionaryVersion;
		  this.wordnetDictDir = wordnetDictDir;
	}
	

	
	
	public Dictionary getWordnetDictionary() throws WordNetException
	{
		if (null==this.dictionary)
		{
			dictionary = new ExtJwnlDictionary(wordnetDictDir);
//			synchronized(newDictionarySynchronizer)
//			{
//				if (null==this.dictionary)
//				{
//					JwnlDictionaryManager manager = new JwnlDictionaryManager(this.dictionaryType,this.dictionaryVersion,wordnetDictDir);
//					this.dictionary = manager.newDictionary();
//				}
//			}
		}
		return this.dictionary;
	}

	public NamedEntityRecognizer newNamedEntityRecognizer() throws NamedEntityRecognizerException
	{
		return new StanfordNamedEntityRecognizer(this.stanfordClassifierPath);
	}

	public BasicParser newParser() throws ParserRunException
	{
		return new MiniparParser(miniparDataDir);
	}

	public SentenceSplitter newSentenceSplitter() throws SentenceSplitterException
	{
		return new NagelSentenceSplitter();
	}
	

	protected Dictionary dictionary = null;
	
	
	protected String miniparDataDir;
	protected File stanfordClassifierPath;
//	protected JwnlDictionaryManagementType dictionaryType;
//	protected JwnlDictionarySupportedVersion dictionaryVersion;
	protected File wordnetDictDir;
	
//	private Object newDictionarySynchronizer = new Object();

}
