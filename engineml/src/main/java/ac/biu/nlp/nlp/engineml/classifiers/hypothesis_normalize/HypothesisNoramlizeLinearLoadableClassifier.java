package ac.biu.nlp.nlp.engineml.classifiers.hypothesis_normalize;
import ac.biu.nlp.nlp.engineml.classifiers.ClassifierException;
import ac.biu.nlp.nlp.engineml.classifiers.LinearClassifier;
import ac.biu.nlp.nlp.engineml.classifiers.io.LearningModel;
import ac.biu.nlp.nlp.engineml.classifiers.io.LearningModelToClassifier;
import ac.biu.nlp.nlp.engineml.classifiers.io.LoadableClassifier;

/**
 * 
 * @author Asher Stern
 * @since Dec 25, 2012
 *
 */
public class HypothesisNoramlizeLinearLoadableClassifier extends HypothesisNoramlizeLinearClassifier implements LoadableClassifier
{
	@Override
	public void load(LearningModel model) throws ClassifierException
	{
		try
		{
			this.realClassifier = (LinearClassifier) LearningModelToClassifier.createForModel(model.getNestedModel());
		}
		catch(ClassCastException e)
		{
			throw new ClassifierException("Failed to load underlying classifier. See nested exception.",e);
		}
	}
	


}
