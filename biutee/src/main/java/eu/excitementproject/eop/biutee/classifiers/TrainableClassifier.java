package eu.excitementproject.eop.biutee.classifiers;
import java.util.Map;
import java.util.Vector;

/**
 * Represents a classifier like SVM or Logistic-regression.
 * <P>
 * <B>See instructions about classifiers in the package-java-doc (package-info.java)
 * of package <code>eu.excitementproject.eop.biutee.classifiers</code></B>
 * <P>
 * Thread-safety: The "get" methods can be called concurrently from two or more
 * threads. The "get" methods are:  {@link #classify(Map)} {@link #classifyBoolean(Map)},
 * {@link #descriptionOfTraining()}, {@link #getFeatureNames()}, {@link #getNormalizedSample(LabeledSample)}.
 * <P>
 * <B>However, other methods are not considered thread-safe.<B> In particular, the methods
 * {@link #train(Vector)}, {@link #reset()} and {@link #setFeaturesNames(Map)} are not
 * considered thread-safe.
 * 
 * @author Asher Stern
 * @since Dec 29, 2010
 *
 */
public interface TrainableClassifier extends Classifier
{
	/**
	 * Trains the classifier with the given vector of labeled-samples.
	 * @param samples
	 * @throws ClassifierException
	 */
	public void train(Vector<LabeledSample> samples) throws ClassifierException;
	
	/**
	 * Deletes the model learned by the training.
	 * After calling {@link #reset()}, the classifier is useless, and has to be
	 * retrained (by calling {@link #train(Vector)}) in order to be used again.
	 * 
	 * @throws ClassifierException
	 */
	public void reset() throws ClassifierException;
	
}
