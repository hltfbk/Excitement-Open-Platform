package eu.excitementproject.eop.biutee.classifiers.io;
import eu.excitementproject.eop.biutee.classifiers.Classifier;
import eu.excitementproject.eop.biutee.classifiers.ClassifierException;

/**
 * 
 * @author Asher Stern
 * @since Dec 23, 2012
 *
 */
public interface LoadableClassifier extends Classifier
{
	public void load(LearningModel model) throws ClassifierException;
}
