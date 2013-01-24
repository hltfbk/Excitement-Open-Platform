package eu.excitementproject.eop.lap.biu.lemmatizer;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.lap.biu.en.lemmatizer.gate.DemoGateLemmatizer;

/**
 * Lemmatizer: gets a word as an input, and returns its lemma as output.
 * For example: children -> child. going -> go.
 * <P>
 * The Lemmatizer may be set with word and part-of-speech, in which case it finds only lemmas
 * that fit that part-of-speech, or it may be set with merely the word, in which case it returns
 * all lemmas that may fit that word in any part-of-speech.
 * <P>
 * Usage:
 * <OL>
 * <LI>Call {@link #init()} </LI>
 * <LI>Set the word by either {@link #set(String)} or {@link #set(String, PartOfSpeech)} </LI>
 * <LI>Call {@link #process()} </LI>
 * <LI>Get the lemma with {@link #getLemma()} or a list of lemmas by {@link #getLemmas()} </LI>
 * <LI>Repeat steps 2 - 4 as many times as you want.</LI>
 * <LI>When you are done - call {@link #cleanUp()}, and then don't use the lemmatizer object
 * any more (You can construct a new lemmatizer object if required). </LI>
 * </OL>
 * <P>
 * <B>Thread safety: Lemmatizer is not thread safe. Don't use the same Lemmatizer instance
 * in two threads.
 * </B>
 * 
 * @see DemoGateLemmatizer
 * 
 * @author Asher Stern
 * @since Jan 18, 2011
 *
 */
public interface Lemmatizer
{
	public void init() throws LemmatizerException;
	
	public void set(String word) throws LemmatizerException;
	
	public void set(String word, PartOfSpeech partOfSpeech) throws LemmatizerException;
	
	public void process() throws LemmatizerException;
	
	public String getLemma() throws LemmatizerException;
	
	public ImmutableList<String> getLemmas() throws LemmatizerException;
	
	public void cleanUp();

}
