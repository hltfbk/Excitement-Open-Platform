package eu.excitementproject.eop.biutee.classifiers.scaling;
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
