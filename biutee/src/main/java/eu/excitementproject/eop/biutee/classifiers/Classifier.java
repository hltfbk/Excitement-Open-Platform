package eu.excitementproject.eop.biutee.classifiers;
import java.util.Map;
import java.util.Vector;

import eu.excitementproject.eop.biutee.classifiers.linearimplementations.ParametersExpanderClassifier;
import eu.excitementproject.eop.biutee.classifiers.scaling.ScalingClassifier;


/**
 * Represents a classifier like SVM or Logistic-regression.
 * <P>
 * <B>See instructions about classifiers in the package-java-doc (package-info.java)
 * of package <code>eu.excitementproject.eop.biutee.classifiers</code></B>
 * <P>
 * Thread-safety: The "get" methods can be called concurrently from two or more
 * threads. The "get" methods are: {@link #classify(Map)} {@link #classifyBoolean(Map)},
 * {@link #descriptionOfTraining()}, {@link #getFeatureNames()}, {@link #getNormalizedSample(LabeledSample)}.
 * <P>
 * <B>However, other methods are not considered thread-safe.<B> In particular,
 * the method {@link #setFeaturesNames(Map)}, and methods in the sub-interface
 * {@link TrainableClassifier}, like {@link TrainableClassifier#train(Vector)}
 * and {@link TrainableClassifier#reset()} are not considered thread-safe.
 * 
 * @author Asher Stern
 * @since Dec 29, 2010
 *
 */

public interface Classifier
{
	/**
	 * Returns a real number in the interval [0,1]. A value >=0.5 is interpreted as "true".
	 * @param featureVector
	 * @return
	 */
	public double classify(Map<Integer, Double> featureVector) throws ClassifierException;
	
	public boolean classifyBoolean(Map<Integer, Double> featureVector) throws ClassifierException;

	public String descriptionOfTraining();

	
	/**
	 * Returns the {@linkplain LabeledSample} as it is forwarded to the real underlying classifier.
	 * <BR>
	 * Some classifiers make some pre-processing on the samples before forwarding them into an
	 * underlying classifier, for example {@link ScalingClassifier} and
	 * {@link ParametersExpanderClassifier}. For those classifiers that method returns a
	 * {@linkplain LabeledSample} that differs from the given (input) sample.
	 * Classifiers that make no pre-processing on the samples should return the sample as it
	 * is given.
	 * <P>
	 * A typical implementation for classifiers that make pre-processing on samples would be:<BR>
	 * <code>return realClassifier.getNormalizedSample(normalizing_method_name(sample));</code>
	 * <P>
	 * A typical implementation for classifiers that <B>make no</B> pre-processing on samples would be:<BR>
	 * <code>return sample</code>
	 * 
	 *  
	 * @param sample A given sample
	 * @return The sample after pre-processing, if such pre-processing is done by this
	 * classifier, or the sample as it is given if not pre-processing is done.
	 * @throws ClassifierException
	 */
	public LabeledSample getNormalizedSample(LabeledSample sample) throws ClassifierException;
	
	
	public void setFeaturesNames(Map<Integer,String> featureNames) throws ClassifierException;
	
	public Map<Integer,String> getFeatureNames() throws ClassifierException;


}
