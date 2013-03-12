package eu.excitementproject.eop.biutee.classifiers.io;
import eu.excitementproject.eop.biutee.classifiers.Classifier;
import eu.excitementproject.eop.biutee.classifiers.ClassifierException;

/**
 * Loadable classifier. Allows loading classifier's parameters
 * from a {@link LearningModel}.
 * <P>
 * <B>See instructions about classifiers in the package-java-doc (package-info.java)
 * of package <code>eu.excitementproject.eop.biutee.classifiers</code></B>
 * <P>
 * 
 * @see LearningModel
 * @see LearningModelToClassifier
 * 
 * @author Asher Stern
 * @since Dec 23, 2012
 *
 */
public interface LoadableClassifier extends Classifier
{
	public void load(LearningModel model) throws ClassifierException;
}
