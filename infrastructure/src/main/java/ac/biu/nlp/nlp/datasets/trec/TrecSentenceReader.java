/**
 * 
 */
package ac.biu.nlp.nlp.datasets.trec;

import java.io.File;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.bind.JAXBException;


import ac.biu.nlp.nlp.instruments.sentencesplit.SentenceSplitter;
import ac.biu.nlp.nlp.instruments.sentencesplit.SentenceSplitterException;

/**
 * This class adds sentence splitting to {@link TrecDocReader} and returns one String for each sentence of each text in the TREC corpora. 
 * @author Amnon Lotan
 *
 * @since Nov 21, 2011
 */
public class TrecSentenceReader extends TrecDocReader {

	public static final String POS_SEP = "_0";

	protected static final String ID_SEP  = "-";

	protected Iterator<String> sentenceIter;
	protected SentenceSplitter sentenceSplitter;

	protected String sentenceText;

	protected int sentenceNo;

	private String sentenceID;
	
	/**
	 * Ctor
	 * @param trecDir
	 * @param sentSplitter
	 * @throws TrecException
	 * @throws JAXBException
	 * @throws TrecException
	 * @throws SentenceSplitterException
	 */
	public TrecSentenceReader(File trecDir, SentenceSplitter sentSplitter) throws TrecException	 
	{
		super(trecDir);
		if (sentSplitter == null)
			throw new TrecException("null sentence splitter");
		this.sentenceSplitter = sentSplitter;
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.search.readers.TrecDocReader#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return super.hasNext() || sentenceIter.hasNext();
	}
	
	public boolean next() throws TrecException {
		close();
		boolean hasNext = hasNext();
		if (hasNext)
		{
			progressSentence();
		}
		return hasNext;
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.search.readers.TrecDocReader#getDocNo()
	 */
	@Override
	public String getCurrentDocNo() {
		return sentenceID;
	}
	
	////////////////////////////////////////////////////////////// protected	////////////////////////////////////////////////////////////////////////
	
	/**
	 * Load the next sentence and sentence number of the next doc
	 * @return
	 * @throws TrecException 
	 */
	protected void progressSentence() throws TrecException {
		while (sentenceIter == null || !sentenceIter.hasNext())	// some docs are empty, so we must loop until we hit one with text
		{
			super.next();	// advances docNo and docText
			
			try {
				sentenceSplitter.setDocument(super.docText);
				sentenceSplitter.split();
			} catch (SentenceSplitterException e) {
				throw new NoSuchElementException("Error sentence splitting the text in " + docNo + ". " + e.getStackTrace());
			}
			List<String> sentences = sentenceSplitter.getSentences();
			sentenceIter = sentences.iterator();
			sentenceNo = 0;
		}
		super.docText = sentenceIter.next();
		super.currReader = new StringReader(super.docText);
		sentenceNo++;
		sentenceID = docNo + ID_SEP + sentenceNo;
	}
}
