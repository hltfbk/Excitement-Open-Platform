/**
 * 
 */
package eu.excitementproject.eop.lap.biu.en.tokenizer;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * Wraps the tokenizer utility in {@link MaxentTagger} in {@code JARS\stanford-postagger-full-2008-06-06\stanford-postagger-2008-06-06.jar}
 * <p>
 * <b>Performance node:</b> a word may be tokenized differently when given within a longer/shorter text: E.g. 
 * <li>"ce d'ivoire be relocated by the government" --> [ce, d'ivoire, be, relocated, by, the, government]
 * <li>"ce d'ivoire" --> [ce, d', ivoire] 
 * 
 * @author Amnon Lotan
 *
 * @since 25/01/2011
 */
public class MaxentTokenizer extends Tokenizer
{
	private List<List<String>> tokenizedSentences = new ArrayList<List<String>>();
	private List<String> sentences = new ArrayList<String>();

	/**
	 * 
	 */
	public MaxentTokenizer()	{	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.tokenizer.Tokenizer#init()
	 */
	public void initPrivate() throws TokenizerException	{	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.tokenizer.Tokenizer#setSentence(java.lang.String)
	 */
	public void setSentencePrivate(String sentence) throws TokenizerException
	{		
		sentences = new ArrayList<String>();
		addSentence(sentence);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.tokenizer.Tokenizer#setSentences(java.lang.Iterable)
	 */
	public void setSentencesPrivate(List<String> sentences)
			throws TokenizerException
	{		
		this.sentences = new ArrayList<String>();
		
		for (String sentence : sentences)
			addSentence(sentence);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.tokenizer.Tokenizer#tokenize()
	 */
	public void tokenizePrivate() throws TokenizerException
	{
		tokenizedSentences = new ArrayList<List<String>>();
		
		for (String sentence : sentences)
		{
			//List<Sentence<? extends HasWord>> sentenceTokens;
			@SuppressWarnings("rawtypes")
			List sentenceTokens;
			try
			{
				sentenceTokens = MaxentTagger.tokenizeText(new StringReader(sentence));
			} catch (Exception e)
			{
				throw new TokenizerException("tokenizer error, see nested", e);
			}
			
			List<String> tokenizedSentence = new ArrayList<String>();

			try
			{
				// Asher 27-Jan-2011
				// We must use a reflection here due to Jar "dll hell".
				// We have two versions of the stanford-postagger jar-file, and the MaxentTagger
				// belongs to the older (2008) one.
				// Since same classes as its return values exist in the newer version,
				// we can't use them explicitly.
				for (Object tokensAsObject : sentenceTokens)
				{
					@SuppressWarnings("rawtypes")
					List tokensAsList = (List)tokensAsObject;

					for (Object hasWordAsObject : tokensAsList)
					{
						Method wordMethod = hasWordAsObject.getClass().getMethod("word");
						Object wordReturnValueAsObject = wordMethod.invoke(hasWordAsObject);

						tokenizedSentence.add((String)wordReturnValueAsObject);
					}
				}
			}
			catch (NoSuchMethodException e)
			{
				throw new TokenizerException("reflection failed in MaxentTokenizer");
			}
			catch (IllegalAccessException e)
			{
				throw new TokenizerException("reflection failed in MaxentTokenizer");
			}
			catch (InvocationTargetException e)
			{
				throw new TokenizerException("reflection failed in MaxentTokenizer");
			}
			
			tokenizedSentences.add(tokenizedSentence);
		}
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.tokenizer.Tokenizer#getTokens()
	 */
	public List<String> getTokenizedSentencePrivate() throws TokenizerException
	{
		return tokenizedSentences.get(0);
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.tokenizer.Tokenizer#getTokenizedSentences()
	 */
	public List<List<String>> getTokenizedSentencesPrivate() throws TokenizerException
	{
		return tokenizedSentences;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.tokenizer.Tokenizer#cleanUp()
	 */
	public void cleanUp()	{	}
	
	//////////////////////////////// private ////////////////////////////////////////
	
	/**
	 * @param sentence
	 */
	private void addSentence(String sentence)
	{
		// avoid tokenization errors in cases such as: 
		//								family man/permafrost scientist
		sentence = sentence.replaceAll("/", " ");

		sentences.add(sentence);
	}
}