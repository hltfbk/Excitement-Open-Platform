package eu.excitementproject.eop.biutee.utilities.unigram;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.utilities.corpora.CorporaException;
import eu.excitementproject.eop.common.utilities.corpora.CorpusDocumentEntity;
import eu.excitementproject.eop.common.utilities.corpora.CorpusReader;
import eu.excitementproject.eop.common.utilities.corpora.DocumentReader;
import eu.excitementproject.eop.lap.biu.en.tokenizer.Tokenizer;
import eu.excitementproject.eop.lap.biu.en.tokenizer.TokenizerException;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitter;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitterException;

/**
 * 
 * @author Asher Stern
 * @since May 13, 2013
 *
 */
public class UnigramCountersCreator
{
	public UnigramCountersCreator(CorpusReader<? extends DocumentReader> corpusReader,
			Tokenizer tokenizer, SentenceSplitter sentenceSplitter)
	{
		super();
		this.corpusReader = corpusReader;
		this.tokenizer = tokenizer;
		this.sentenceSplitter = sentenceSplitter;
	}
	
	public void create() throws UnigramCreatorException
	{
		counters = new LinkedHashMap<>();
		try
		{
			Iterator<? extends CorpusDocumentEntity<? extends DocumentReader>> iterator = corpusReader.iterator();
			int index=0;
			while (iterator.hasNext())
			{
				CorpusDocumentEntity<? extends DocumentReader> document = iterator.next();
				logger.info(String.format("%,-6d", index)+": "+document.getDocumentDescription());
				addDocument(document);
				++index;
			}
			if (preprocessErrors>0)
			{
				logger.error("Total number of processing failures = "+preprocessErrors);
			}
			else
			{
				logger.info("There were no failures in processing documents.");
			}
			
			if (xmlErrors>0)
			{
				logger.error("Total number of XML failures = "+xmlErrors);
			}
			else
			{
				logger.info("There were no failures in parsing XML documents.");
			}

		}
		catch (CorporaException e)
		{
			throw new UnigramCreatorException("Error. See nested exception.",e);
		}
	}
	
	
	
	public Map<String, Long> getCounters() throws UnigramCreatorException
	{
		if (null==counters) throw new UnigramCreatorException("Not created");
		return counters;
	}

	private void addDocument(CorpusDocumentEntity<? extends DocumentReader> document)
	{
		try
		{

			document.getDocumentReader().read();
			String documentContents = document.getDocumentReader().getDocumentContents();
			sentenceSplitter.setDocument(documentContents);
			sentenceSplitter.split();
			List<String> sentences = sentenceSplitter.getSentences();
			for (String sentence : sentences)
			{
				tokenizer.setSentence(sentence);
				tokenizer.tokenize();
				List<String> tokens = tokenizer.getTokenizedSentence();
				for (String token : tokens)
				{
					CounterUtilities.add(counters, token, 1);
				}
			}
		}
		catch(SentenceSplitterException | TokenizerException e)
		{
			logger.error("Error processing document: "+document.getDocumentDescription(),e);
			++preprocessErrors;
		}
		catch(CorporaException e)
		{
			logger.error("Error processing document: "+document.getDocumentDescription(),e);
			++xmlErrors;
		}
	}
	
	
	private final CorpusReader<? extends DocumentReader> corpusReader;
	private final SentenceSplitter sentenceSplitter;
	private final Tokenizer tokenizer;
	
	private int preprocessErrors = 0;
	private int xmlErrors = 0;
	
	private Map<String, Long> counters = null;
	

	private static final Logger logger = Logger.getLogger(UnigramCountersCreator.class);
}
