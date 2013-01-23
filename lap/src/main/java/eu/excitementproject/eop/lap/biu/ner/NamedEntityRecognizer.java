package eu.excitementproject.eop.lap.biu.ner;

import java.util.List;
import java.util.Map;

import eu.excitementproject.eop.common.representation.parse.representation.basic.NamedEntity;
import eu.excitementproject.eop.lap.biu.en.tokenizer.Tokenizer;
import eu.excitementproject.eop.lap.biu.en.tokenizer.TokenizerException;


/**
 * An interface for Named Entity Recognizer.
 * <P>
 * Named Entity Recognizer is a utility which assigns
 * words in the text a category that reflects the meaning of
 * them as named entities, if they are indeed named entities.
 * <P>
 * A named entity is something like "PERSON", "ORGANIZATION",
 * "LOCATION", etc.
 * <P>
 * <B>Thread safety: NamedEntityRecognizer is not thread safe. Don't use the same
 * NamedEntityRecognizer instance in two threads.</B>
 * 
 * @author Asher Stern & Erel Segal
 *
 */
public interface NamedEntityRecognizer
{
	
	/**
	 * Call this method only once.
	 * <P>
	 * Call the method to initialize the {@linkplain NamedEntityRecognizer}
	 * <P>
	 * Don't call other methods of this interface before calling {@linkplain #init()} method.
	 * @throws NamedEntityRecognizerException An error occured while trying to initialize.
	 */
	public void init() throws NamedEntityRecognizerException;
	
	/**
	 * Set a sentence to the {@linkplain NamedEntityRecognizer}.
	 * <br/>you may use this function as an alternative to {@link #setSentence(List)}. 
	 *
	 * @param sentence - The sentence.
	 * @param tokenizer may be used, by some implementations, to tokenize the sentence. If it is needed, it must be initialized before it is passed to this function.
	 * <br/>some implementations do the NER on the raw string, without tokenization. These implementations will ignore the tokenizer. In that case, the tokenizer may be null.
	 */
	public void setSentence(String sentence, Tokenizer tokenizer) throws TokenizerException;
	
	/**
	 * Set a sentence - as a list of tokens - to the {@linkplain NamedEntityRecognizer}.
	 * The tokens in the sentence will be assigned later with {@link NamedEntity} values
	 * (depending on that they are indeed named entities), by the {@linkplain NamedEntityRecognizer}.
	 * <br/>as an alternative to this function, you may use {@link #setSentence(String, Tokenizer)}. 
	 * 
	 * @param sentence - The sentence, as a list of tokens.
	 */
	public void setSentence(List<String> tokens);
	
	/**
	 * Assigns {@link NamedEntity} to the words in the sentence.<br>
	 * Assigns null for words which are not named entities.
	 * @throws NamedEntityRecognizerException Any error
	 */
	public void recognize() throws NamedEntityRecognizerException;
	
	/**
	 * The annotated sentence is exactly the original sentence, such that
	 * each word is given attached to a {@link NamedEntity} that was assigned
	 * by the {@link NamedEntityRecognizer}.
	 * <P>
	 * Call this method only after calling {@link #recognize()} method.
	 * @return The sentence with the appropriate values to the words,
	 * as assigned by the {@link NamedEntityRecognizer} (by the {@link #recognize()} method).
	 */
	public List<NamedEntityWord> getAnnotatedSentence();
	
	/**
	 * <P>Call this method only after calling {@link #recognize()} method.
	 * @return a map from positions in the sentence to named entities that start at that position.
	 * as assigned by the {@link NamedEntityRecognizer} (by the {@link #recognize()} method).
	 */
	public Map<Integer, NamedEntityPhrase> getAnnotatedEntities();
	
	/**
	 * Call this method once you have finished using the {@link NamedEntityRecognizer},
	 * and you will not use it any more.
	 * <P>
	 * I.e. <B> DON'T </B> call it each time you are done with a sentence, but
	 * only once there are no more sentences to be annotated any more.
	 */
	public void cleanUp();
}
