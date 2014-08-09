package eu.excitementproject.eop.transformations.uima.ae.truthteller;

import java.util.List;

import eu.excitementproject.eop.lap.biu.ner.NamedEntityRecognizerException;
import eu.excitementproject.eop.transformations.generic.truthteller.conll.ConllConverterException;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;

/** 
 * An interface for Truth annotations components
 * 
 *  @author Gabi Stanovsky
 *  @since Aug 2014
 */

public interface PredicateTruth {
	
	/**
	 * Call this method only once.
	 * <P>
	 * Call the method to initialize the {@linkplain PredicateTruth}
	 * <P>
	 * Don't call other methods of this interface before calling {@linkplain #init()} method.
	 * @throws ConllConverterException 
	 * @throws NamedEntityRecognizerException An error occured while trying to initialize.
	 */
	public void init() throws PredicateTruthException;
	
	/**
	 * Set a sentence to the {@linkplain PredicateTruth}. 
	 * */
	public void setSentence(ExtendedNode annotatedSentence);

	/**
	 * Assigns truth value to the words in the sentence.<br>
	 * Assigns null for words which don't have truth values.
	 * @throws PredicateTruthException on Any error
	 * @throws ConllConverterException 
	 */
	public void annotate() throws PredicateTruthException;
	
	/**
	 * <P>Call this method only after calling {@link #annotate()} method.
	 * @return a List of corresponding to truth values according to the position of the token in the sentence
	 * as assigned by the truth annotator (by the {@link #annotate} method).
	 */
	public List<SingleTokenTruthAnnotation> getAnnotatedEntities();
	
	/**
	 * Call this method once you have finished using the {@link PredicateTruth},
	 * and you will not use it any more.
	 * <P>
	 * I.e. <B> DON'T </B> call it each time you are done with a sentence, but
	 * only once there are no more sentences to be annotated any more.
	 */
	public void cleanUp();
}


