package ac.biu.nlp.nlp.engineml.classifiers.scaling;

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
public class LinearScalingLoadableClassifier extends LinearScalingClassifier implements LoadableClassifier
{

	@Override
	public void load(LearningModel model) throws ClassifierException
	{
		this.realLinearClassifier = (LinearClassifier) LearningModelToClassifier.createForModel(model.getNestedModel());
		this.realClassifier = this.realLinearClassifier;
		
		try
		{
			LearningModelForLinearScalingClassifier scalingModel =
					(LearningModelForLinearScalingClassifier) model;
			
			this.maxMap = scalingModel.getMaxMap();
			this.minMap = scalingModel.getMinMap();
			this.doNotScale = scalingModel.getDoNotScale();
		}
		catch(ClassCastException e)
		{
			throw new ClassifierException("Failed to load "+LinearScalingLoadableClassifier.class.getSimpleName(),e);
		}
				
				
		
		
	}

}
