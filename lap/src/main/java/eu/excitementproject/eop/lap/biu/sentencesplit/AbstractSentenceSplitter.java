/**
 * 
 */
package eu.excitementproject.eop.lap.biu.sentencesplit;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * An abstract partial implementation for {@link SentenceSplitter} that adds some method order usage constrictions. All implemented 
 * sentence splitters should extend this, not SentenceSplitter.
 * <p>The text is given as a free text, and the sentence
 * splitter returns list of sentences.
 * 
 * @author Amnon Lotan
 *
 * @since 02/02/2011
 */
public abstract class AbstractSentenceSplitter implements SentenceSplitter
{
	////////////////////////////////////////////////////////////// public final interface ////////////////////////////////////////////////////////
	
	private boolean documentSet = false;
	private boolean splitWasCalled = false;
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.sentencesplit.SentenceSplitter#setDocument(java.io.File)
	 */
	public final void setDocument(File textFile) throws SentenceSplitterException
	{
		setDocumentImpl(textFile);
		documentSet  = true;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.sentencesplit.SentenceSplitter#setDocument(java.lang.String)
	 */
	public final void setDocument(String documentContents) throws SentenceSplitterException
	{
		setDocumentImpl(documentContents);
		documentSet  = true;
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.sentencesplit.SentenceSplitter#setDocument(java.io.InputStream)
	 */
	public final void setDocument(InputStream documentStream) throws SentenceSplitterException
	{
		setDocumentImpl(documentStream);
		documentSet  = true;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.sentencesplit.SentenceSplitter#split()
	 */
	public final void split() throws SentenceSplitterException
	{
		
		if (!documentSet)
			throw new SentenceSplitterException("Invalid call to split() method. Document not set yet.");
		
		splitImpl();
		
		documentSet = false;
		splitWasCalled  = true;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.sentencesplit.SentenceSplitter#getSentences()
	 */
	public final List<String> getSentences()
	{
		if (!splitWasCalled)
			throw new RuntimeException(new SentenceSplitterException("Illegal call to getSentences(). Must call split before."));

		return getSentencesImpl();
	}

	/////////////////////////////////////////////////// protected abstract implementation ///////////////////////////////////////////////	
	
	/**
	 * this is one of the setDocument() methods that
	 * are used to supply the text.
	 * @param textFile a text file that contains the text.
	 * @throws SentenceSplitterException
	 */
	protected abstract void setDocumentImpl(File textFile) throws SentenceSplitterException;
	
	/**
	 * this is one of the setDocument() methods that
	 * are used to supply the text.
	 * @param documentContents a string that contains the whole text.
	 * @throws SentenceSplitterException
	 */
	protected abstract void setDocumentImpl(String documentContents) throws SentenceSplitterException;
	
	/**
	 * this is one of the setDocument() methods that
	 * are used to supply the text.
	 * @param documentStream a stream that holds the whole text.
	 * @throws SentenceSplitterException
	 */
	protected abstract void setDocumentImpl(InputStream documentStream) throws SentenceSplitterException;

	/**
	 * Makes the sentence splitting.
	 * Later, the {@link #getSentences()} can return the result.
	 * @throws SentenceSplitterException
	 */
	protected abstract void splitImpl() throws SentenceSplitterException;

	/**
	 * After calling setDocument() method and split() method,
	 * use this method to get the result.
	 * @return
	 */
	protected abstract List<String> getSentencesImpl();
}
