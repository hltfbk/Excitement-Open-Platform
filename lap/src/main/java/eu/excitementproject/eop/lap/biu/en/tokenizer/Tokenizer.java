/**
 * 
 */
package eu.excitementproject.eop.lap.biu.en.tokenizer;

import java.util.List;

/**
 * Tokenizer
 * <br>
 * The tokenizer assumes the input is a natural sentence
 * <P>
 * The input (and output) may be a String containing one sentence, or a List of sentences
 * <P>
 * <B>Note:</B> The following usage restrictions are enforced by this abstract Tokenizer, the rest is done in the extensions
 * <P>
 * <B>Note:</B> before starting to work with the Tokenizer, call {@link #init()}. When the
 * Tokenizer is not to be used any-more, call {@link #cleanUp()}. 
 * <P>
 * <B>Note:</B> {@link #getTokenizedSentence()} can be called only after {@link #setSentence(String)}, and
 * {@link #getTokenizedSentences()} only after {@link #setSentences(List)}
 * <p>
 * <B>Note:</B> you must call {@link #setSentence(String)} or {@link #setSentences(List)} before calling {@link #tokenize()}
 * <p>
 * <B>Note:</B> you must call {@link #tokenize()} before calling {@link #getTokenizedSentence()} or {@link #getTokenizedSentences()}
 * <p>
 * <b>Performance node:</b> a word may be tokenized differently when given within a longer/shorter text: E.g. 
 * <li>"ce d'ivoire be relocated by the government" --> [ce, d'ivoire, be, relocated, by, the, government]
 * <li>"ce d'ivoire" --> [ce, d', ivoire] 
 * <p>
 * 
 * <B>Thread safety: Tokenizer is not thread safe. Don't use the same
 * Tokenizer instance in two threads.</B>
 
 * 
 * @author Amnon Lotan
 *
 * @since 25/01/2011
 */
public abstract class Tokenizer
{
	private boolean initialized = false;
	private boolean multiSenteceMode;
	private boolean noInput = true;
	private boolean tokenized = false;
	
	/**
	 * Initialize. must be called once before (all calls to) {@link #tokenize()} 
	 * @throws TokenizerException
	 */
	public void init() throws TokenizerException
	{
		initPrivate();
		
		initialized = true;
	}
	
	/**
	 * set the input
	 * @param sentence a string of natural text
	 * @throws TokenizerException
	 */
	public void setSentence(String sentence) throws TokenizerException
	{
		if (sentence == null)
			throw new TokenizerException("null input");
		
		setSentencePrivate(sentence);
		
		multiSenteceMode = false;
		noInput = false;
		tokenized = false;
	}
	/**
	 * set the input
	 * @param sentences a list of natural text sentences
	 * @throws TokenizerException
	 */
	public void setSentences(List<String> sentences) throws TokenizerException
	{
		if (sentences == null)
			throw new TokenizerException("null input");
		
		setSentencesPrivate(sentences);
		
		multiSenteceMode = true;
		noInput = false;
		tokenized = false;
	}
	
	/**
	 * @return true if this tokenizer is initialized
	 */
	public boolean isInitialized() {
		return initialized;
	}
	
	/**
	 * tokenize the input set in {@link #setSentences()}
	 * @throws TokenizerException
	 */
	public void tokenize() throws TokenizerException
	{
		if (noInput)
			throw new TokenizerException("You must call setSentences() or setSentence() before tokenizing");
		
		tokenizePrivate();
		
		noInput = true;
		tokenized = true;
	}
	
	/**
	 * @return the data set in {@link #setSentence(String)} and tokenized in {@link #tokenize()}
	 * @throws TokenizerException
	 */
	public List<String> getTokenizedSentence() throws TokenizerException
	{
		if (!tokenized)
			throw new TokenizerException("You must call tokenize() before getTokenizedSentence() and getTokenizedSentences()");
		if (multiSenteceMode)
			throw new TokenizerException("can't call getTokenizedSentence() after calling setSentences()");
		
		return getTokenizedSentencePrivate();
	}
	
	/**
	 * @return the data set in {@link #setSentences(List)} and tokenized in {@link #tokenize()}
	 * @throws TokenizerException
	 */
	public List<List<String>> getTokenizedSentences() throws TokenizerException
	{
		if (!tokenized)
			throw new TokenizerException("You must call tokenize() before getTokenizedSentence() and getTokenizedSentences()");
		if (!multiSenteceMode)
			throw new TokenizerException("can't call getTokenizedSentences() after calling setSentence()");
		
		return getTokenizedSentencesPrivate();
	}
	
	/**
	 * 
	 */
	public abstract void cleanUp();
	
	////////////////////////////////// protected implementations //////////////////////////////////
	
	/**
	 * Initialize. must be called once before (all calls to) {@link #tokenize()} 
	 * @throws TokenizerException
	 */
	protected abstract void initPrivate() throws TokenizerException;
	
	/**
	 * set the input
	 * @param sentence a string of natural text
	 * @throws TokenizerException
	 */
	protected abstract void setSentencePrivate(String sentence) throws TokenizerException;
	/**
	 * set the input
	 * @param sentences a list of natural text sentences
	 * @throws TokenizerException
	 */
	protected abstract void setSentencesPrivate(List<String> sentences) throws TokenizerException;
	
	/**
	 * tokenize the input set in {@link #setSentences()}
	 * @throws TokenizerException
	 */
	protected abstract void tokenizePrivate() throws TokenizerException;
	
	/**
	 * @return the data set in {@link #setSentence(String)} and tokenized in {@link #tokenize()}
	 * @throws TokenizerException
	 */
	protected abstract List<String> getTokenizedSentencePrivate() throws TokenizerException;
	
	/**
	 * @return the data set in {@link #setSentences(List)} and tokenized in {@link #tokenize()}
	 * @throws TokenizerException
	 */
	protected abstract List<List<String>> getTokenizedSentencesPrivate() throws TokenizerException;
}