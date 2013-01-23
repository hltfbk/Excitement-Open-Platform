package eu.excitementproject.eop.lap.biu.sentencesplit;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * An interface for sentence splitter.
 * The text is given as a free text, and the sentence
 * splitter returns list of sentences.
 * 
 * <p>{@link AbstractSentenceSplitter} adds some method order usage constrictions. So all implemented 
 * sentence splitters should extend it, not this.
 * <B>Thread safety: SentenceSplitter is not thread safe. Don't use the same
 * SentenceSplitter instance in two threads.</B>
 * 
 * @author Asher Stern
 *
 */
public interface SentenceSplitter
{
	/**
	 * this is one of the setDocument() methods that
	 * are used to supply the text.
	 * @param textFile a text file that contains the text.
	 * @throws SentenceSplitterException
	 */
	public void setDocument(File textFile) throws SentenceSplitterException;
	
	/**
	 * this is one of the setDocument() methods that
	 * are used to supply the text.
	 * @param documentContents a string that contains the whole text.
	 * @throws SentenceSplitterException
	 */
	public void setDocument(String documentContents) throws SentenceSplitterException;
	
	/**
	 * this is one of the setDocument() methods that
	 * are used to supply the text.
	 * @param documentStream a stream that holds the whole text.
	 * @throws SentenceSplitterException
	 */
	public void setDocument(InputStream documentStream) throws SentenceSplitterException;

	/**
	 * Makes the sentence splitting.
	 * Later, the {@link #getSentences()} can return the result.
	 * @throws SentenceSplitterException
	 */
	public void split() throws SentenceSplitterException;
	
	/**
	 * After calling setDocument() method and split() method,
	 * use this method to get the result.
	 * @return
	 */
	public List<String> getSentences();
}
