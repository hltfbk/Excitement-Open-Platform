package ac.biu.nlp.nlp.engineml.utilities;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableListWrapper;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import ac.biu.nlp.nlp.engineml.datastructures.SingleItemList;
import ac.biu.nlp.nlp.instruments.lemmatizer.GateLemmatizer;
import ac.biu.nlp.nlp.instruments.lemmatizer.Lemmatizer;
import ac.biu.nlp.nlp.instruments.lemmatizer.LemmatizerException;

/**
 * This is a {@link Lemmatizer}, which wraps another lemmatizer, and returns exactly
 * what the wrapped lemmatizer returns, except lemmatization of apostrophe.
 * If the input is an apostrophe (an upper comma) - then regardless what the real
 * lemmatizer returns, this lemmatizer returns the apostrophe (i.e. the input word) itself.
 * <P>
 * This class is required since {@link GateLemmatizer} returns "be" as the lemma of
 * an apostrophe.
 * 
 * @author Asher Stern
 * @since Aug 15, 2012
 *
 */
public class LemmatizerFilterApostrophe implements Lemmatizer
{
	public static final String APOSTROPHE = "\'s";
	
	public LemmatizerFilterApostrophe(Lemmatizer realLemmatizer)
	{
		super();
		this.realLemmatizer = realLemmatizer;
	}

	

	@Override
	public void init() throws LemmatizerException
	{
		realLemmatizer.init();
		
	}

	@Override
	public void set(String word) throws LemmatizerException
	{
		lastInputWord = word;
		realLemmatizer.set(word);
	}

	@Override
	public void set(String word, PartOfSpeech partOfSpeech)
			throws LemmatizerException
	{
		lastInputWord = word;
		realLemmatizer.set(word,partOfSpeech);
	}

	@Override
	public void process() throws LemmatizerException
	{
		realLemmatizer.process();
	}

	@Override
	public String getLemma() throws LemmatizerException
	{
		if (APOSTROPHE.equals(lastInputWord))
		{
			return lastInputWord;
		}
		else
		{
			return realLemmatizer.getLemma();
		}
	}

	@Override
	public ImmutableList<String> getLemmas() throws LemmatizerException
	{
		if (APOSTROPHE.equals(lastInputWord))
		{
			return new ImmutableListWrapper<String>(new SingleItemList<String>(lastInputWord));
		}
		else
		{
			return realLemmatizer.getLemmas();
		}
	}

	@Override
	public void cleanUp()
	{
		realLemmatizer.cleanUp();
		
	}
	
	private Lemmatizer realLemmatizer;
	private String lastInputWord = null;

}
