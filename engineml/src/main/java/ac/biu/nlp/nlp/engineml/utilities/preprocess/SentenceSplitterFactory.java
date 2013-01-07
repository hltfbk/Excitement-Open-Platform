package ac.biu.nlp.nlp.engineml.utilities.preprocess;

import ac.biu.nlp.nlp.instruments.sentencesplit.SentenceSplitter;
import ac.biu.nlp.nlp.instruments.sentencesplit.nagel.NagelSentenceSplitter;

/**
 * Factory to create and return a {@link SentenceSplitter}.
 * @author Asher Stern
 * @since Feb 15, 2011
 *
 */
public class SentenceSplitterFactory
{
	public SentenceSplitter getDefaultSentenceSplitter()
	{
		return new NagelSentenceSplitter();
	}

}
