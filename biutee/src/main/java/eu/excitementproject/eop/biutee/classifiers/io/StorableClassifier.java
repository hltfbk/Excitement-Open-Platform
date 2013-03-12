package eu.excitementproject.eop.biutee.classifiers.io;
import eu.excitementproject.eop.biutee.classifiers.Classifier;
import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.classifiers.TrainableStorableClassifier;

/**
 * Storable classifier - a classifier that can be stored in a {@link LearningModel}.
 * The {@link LearningModel} can then be written to an XML file.
 * <P>
 * <B>See instructions about classifiers in the package-java-doc (package-info.java)
 * of package <code>eu.excitementproject.eop.biutee.classifiers</code></B>
 * <P>
 * 
 * @see LearningModel
 * @see TrainableStorableClassifier
 * @see LinearTrainableStorableClassifier
 * 
 * @author Asher Stern
 * @since Dec 23, 2012
 *
 */
public interface StorableClassifier extends Classifier
{
	public LearningModel store() throws ClassifierException;;
}
