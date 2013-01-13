package ac.biu.nlp.nlp.engineml.utilities.preprocess;
import eu.excitementproject.eop.lap.biu.en.sentencesplit.SentenceSplitter;
import eu.excitementproject.eop.lap.biu.en.sentencesplit.nagel.NagelSentenceSplitter;

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
