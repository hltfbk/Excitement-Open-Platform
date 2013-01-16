package eu.excitementproject.eop.biutee.classifiers.hypothesis_normalize;
import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.classifiers.io.LearningModel;
import eu.excitementproject.eop.biutee.classifiers.io.LearningModelToClassifier;
import eu.excitementproject.eop.biutee.classifiers.io.LoadableClassifier;

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
