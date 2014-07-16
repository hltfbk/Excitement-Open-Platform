package eu.excitementproject.eop.transformations.generic.truthteller;

import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;

import eu.excitementproject.eop.transformations.generic.truthteller.conll.ConllConverterException;

/* 
 * An interface for Truth annotations components 
 */

public interface TruthAnnotator {
	
	/**
	 * Call this method only once.
	 * <P>
	 * Call the method to initialize the {@linkplain TruthAnnotator}
	 * <P>
	 * Don't call other methods of this interface before calling {@linkplain #init()} method.
	 * @throws ConllConverterException 
	 * @throws NamedEntityRecognizerException An error occured while trying to initialize.
	 */
	public void init() throws TruthAnnotatorException, ResourceInitializationException, ConllConverterException ;
	
	/**
	 * Set a sentence to the {@linkplain TruthAnnotator}. 
	 * */
	public void setSentence(String sentence);

	/**
	 * Assigns truth value to the words in the sentence.<br>
	 * Assigns null for words which don't have truth values.
	 * @throws TruthAnnotatorException on Any error
	 * @throws ConllConverterException 
	 */
	public void annotate() throws TruthAnnotatorException, ConllConverterException;
	
	/**
	 * <P>Call this method only after calling {@link #annotate()} method.
	 * @return a map from positions in the sentence to truth values that start at that position.
	 * as assigned by the truth annotator (by the {@link #annotate} method).
	 */
	public Map<Integer, SingleTokenTruthAnnotation> getAnnotatedEntities();
	
	/**
	 * Call this method once you have finished using the {@link TruthAnnotator},
	 * and you will not use it any more.
	 * <P>
	 * I.e. <B> DON'T </B> call it each time you are done with a sentence, but
	 * only once there are no more sentences to be annotated any more.
	 */
	public void cleanUp();
}


