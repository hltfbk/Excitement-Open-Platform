package eu.excitementproject.eop.biutee.classifiers.f1_logicstic_regression;
import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.io.LearningModel;
import eu.excitementproject.eop.biutee.classifiers.io.LoadableClassifier;

/**
 * 
 * @author Asher Stern
 * @since Dec 26, 2012
 *
 */
public class AdjustingVectorLinearWithGammaLoadableClassifier extends AdjustingVectorLinearWithGammaClassifier implements LoadableClassifier
{
	@Override
	public void load(LearningModel model) throws ClassifierException
	{
		try
		{
			LearningModelForAdjustingVectorLinearWithGammaClassifier castedModel =
					(LearningModelForAdjustingVectorLinearWithGammaClassifier) model;
			
			weights = castedModel.getWeights();
			gammaCoefficientSigmoid = castedModel.getGammaCoefficientSigmoid();
		}
		catch(ClassCastException e)
		{
			throw new ClassifierException("Model casting failure.",e);
		}
				
	}
}
