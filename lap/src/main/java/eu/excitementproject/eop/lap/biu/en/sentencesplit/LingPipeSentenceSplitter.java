/**
 * 
 */
package eu.excitementproject.eop.lap.biu.en.sentencesplit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunking;
import com.aliasi.sentences.IndoEuropeanSentenceModel;
import com.aliasi.sentences.SentenceChunker;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;

import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.lap.biu.sentencesplit.AbstractSentenceSplitter;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitterException;

/**
 * Uses the OpenNlp {@link SentenceChunker}, in {@code JARS\lingpipe-3.1.1.jar} to split sentences. I believe this is
 * the <b>fastest</b> of our sentence splitters.
 * 
 * @author Amnon Lotan
 *
 * @since 26/01/2011
 */
public class LingPipeSentenceSplitter extends AbstractSentenceSplitter
{
	private SentenceChunker sentenceChunker = null;
	private String text;
	private List<String> cookedSentences;
	
	/**
	 * 
	 */
	public LingPipeSentenceSplitter()
	{
		// load sentence splitter
		sentenceChunker = new SentenceChunker(new IndoEuropeanTokenizerFactory(), new IndoEuropeanSentenceModel(true, true));

	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.sentencesplit.SentenceSplitter#setDocument(java.io.File)
	 */
	protected void setDocumentImpl(File textFile) throws SentenceSplitterException
	{
		if (textFile == null)
			throw new SentenceSplitterException("null input");
		if (!textFile.exists())
			throw new SentenceSplitterException(textFile + "doesn't exist");
		
		try
		{
			setDocument(FileUtils.loadFileToString(textFile));
		} catch (IOException e)
		{
			throw new SentenceSplitterException("error with file, see nested.",e);
		}
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.sentencesplit.SentenceSplitter#setDocument(java.lang.String)
	 */
	protected void setDocumentImpl(String documentContents)
			throws SentenceSplitterException
	{
		if (documentContents == null)
			throw new SentenceSplitterException("null input");
		
		text = documentContents;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.sentencesplit.SentenceSplitter#setDocument(java.io.InputStream)
	 */
	protected void setDocumentImpl(InputStream documentStream)
			throws SentenceSplitterException
	{
		try
		{
			setDocument(StringUtil.convertStreamToString(documentStream));
		} catch (IOException e)
		{
			throw new SentenceSplitterException("error with InputStream, see nested.",e);
		}
	}
	

	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.sentencesplit.SentenceSplitter#split()
	 */
	protected void splitImpl() throws SentenceSplitterException
	{
		Chunking chunkedText = sentenceChunker.chunk(text);
		Set<Chunk> sentences = (Set<Chunk>)chunkedText.chunkSet();
		CharSequence seq = chunkedText.charSequence();
		
		cookedSentences = new LinkedList<String>();
		for(Chunk sentence : sentences)
			cookedSentences.add(seq.subSequence(sentence.start(), sentence.end()).toString());
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.sentencesplit.SentenceSplitter#getSentences()
	 */
	protected List<String> getSentencesImpl()
	{
		return cookedSentences;
	}
}
