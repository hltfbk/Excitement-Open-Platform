package eu.excitementproject.eop.biutee.classifiers.io;
import eu.excitementproject.eop.biutee.classifiers.Classifier;
import eu.excitementproject.eop.biutee.classifiers.ClassifierException;

/**
 * 
 * @author Asher Stern
 * @since Dec 23, 2012
 *
 */
public interface StorableClassifier extends Classifier
{
	public LearningModel store() throws ClassifierException;;
}
