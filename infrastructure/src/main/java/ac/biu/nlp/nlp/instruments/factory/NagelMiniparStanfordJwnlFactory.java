package ac.biu.nlp.nlp.instruments.factory;

import java.io.File;

import eu.excitementproject.eop.lap.biu.en.ner.NamedEntityRecognizer;
import eu.excitementproject.eop.lap.biu.en.ner.NamedEntityRecognizerException;
import eu.excitementproject.eop.lap.biu.en.ner.stanford.StanfordNamedEntityRecognizer;
import eu.excitementproject.eop.lap.biu.en.parser.BasicParser;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lap.biu.en.parser.minipar.MiniparParser;
import eu.excitementproject.eop.lap.biu.en.sentencesplit.SentenceSplitter;
import eu.excitementproject.eop.lap.biu.en.sentencesplit.SentenceSplitterException;
import eu.excitementproject.eop.lap.biu.en.sentencesplit.nagel.NagelSentenceSplitter;

import ac.biu.nlp.nlp.instruments.dictionary.wordnet.Dictionary;
import ac.biu.nlp.nlp.instruments.dictionary.wordnet.WordNetException;
import ac.biu.nlp.nlp.instruments.dictionary.wordnet.ext_jwnl.ExtJwnlDictionary;

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
