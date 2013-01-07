package ac.biu.nlp.nlp.engineml.classifiers.io;

import ac.biu.nlp.nlp.engineml.classifiers.Classifier;
import ac.biu.nlp.nlp.engineml.classifiers.ClassifierException;

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
