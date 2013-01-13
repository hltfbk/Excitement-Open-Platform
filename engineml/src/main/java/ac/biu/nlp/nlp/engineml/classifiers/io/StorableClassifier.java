package ac.biu.nlp.nlp.engineml.classifiers.io;
import ac.biu.nlp.nlp.engineml.classifiers.Classifier;
import ac.biu.nlp.nlp.engineml.classifiers.ClassifierException;

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
