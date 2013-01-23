package eu.excitementproject.eop.biutee.utilities.preprocess;
import eu.excitementproject.eop.lap.biu.en.sentencesplit.nagel.NagelSentenceSplitter;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitter;

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
