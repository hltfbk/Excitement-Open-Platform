/**
 * 
 */
package ac.biu.nlp.nlp.instruments.tokenizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import opennlp.maxent.MaxentModel;
import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.tools.tokenize.TokenizerME;

/**
 * A wrapper for the OpenNlp {@link TokenizerME} in {@code JARS\opennlp-tools-1.3.0.jar}
 * <p>
 * From the Jar:
 * <br>
 * A Tokenizer for converting raw text into separated tokens.  It uses
 * Maximum Entropy to make its decisions.  The features are loosely
 * based off of Jeff Reynar's UPenn thesis "Topic Segmentation:
 * Algorithms and Applications.", which is available from his
 * homepage: {@link http://www.cis.upenn.edu/~jcreynar}.
 * <p>
 * <b>Performance node:</b> a word may be tokenized differently when given within a longer/shorter text: E.g. 
 * <li>"ce d'ivoire be relocated by the government" --> [ce, d'ivoire, be, relocated, by, the, government]
 * <li>"ce d'ivoire" --> [ce, d', ivoire] 
 *
 * @author      Tom Morton
 * @version $Revision: 1.11 $, $Date: 2005/11/20 04:52:19 $
 * 
 * 
 * @author Amnon Lotan
 *
 * @since 25/01/2011
 */
public class OpenNlpTokenizer extends Tokenizer
{
	private TokenizerME tokenizer = null;
	private File tokenizerModelFile;
	private List<List<String>> tokenizedSentences;
	private List<String> sentences = new ArrayList<String>();

	/**
	 * 
	 * @param tokenizerModelFile e.g. "JARS/opennlp-tools-1.3.0/models/english/tokenize/EnglishTok.bin.gz"
	 * @throws TokenizerException
	 */
	public OpenNlpTokenizer(File tokenizerModelFile) throws TokenizerException
	{
		if(tokenizerModelFile == null)
			throw new TokenizerException("no tokenizer model file specified");
		if (!tokenizerModelFile.exists())
			throw new TokenizerException(tokenizerModelFile + " doesn't exist");
		
		this.tokenizerModelFile = tokenizerModelFile;
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.tokenizer.Tokenizer#init()
	 */
	public void initPrivate() throws TokenizerException
	{
		// load tokenizer

		MaxentModel tokenizerModel;
		try {
			tokenizerModel = new SuffixSensitiveGISModelReader(tokenizerModelFile).getModel();
		} catch (IOException e) {
			throw new TokenizerException("Error constructing a SuffixSensitiveGISModelReader with " + tokenizerModelFile + ". See nested.", e);		}

		tokenizer = new TokenizerME(tokenizerModel);
		tokenizer.setAlphaNumericOptimization(true);

	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.tokenizer.Tokenizer#setSentence(java.lang.String)
	 */
	public void setSentencePrivate(String sentence) throws TokenizerException
	{
		sentences = new ArrayList<String>();
		sentences.add(sentence);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.tokenizer.Tokenizer#setSentences(java.lang.Iterable)
	 */
	public void setSentencesPrivate(List<String> sentences) throws TokenizerException
	{
		this.sentences = sentences;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.tokenizer.Tokenizer#tokenize()
	 */
	public void tokenizePrivate() throws TokenizerException
	{
		tokenizedSentences = new ArrayList<List<String>>();
		
		for (String sentence : sentences)
		{
			String[] tokensArr = tokenizer.tokenize(sentence);
			
			List<String> tokenizedSentence = new ArrayList<String>();
			for (String token : tokensArr)
				tokenizedSentence.add(token);
			
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
}