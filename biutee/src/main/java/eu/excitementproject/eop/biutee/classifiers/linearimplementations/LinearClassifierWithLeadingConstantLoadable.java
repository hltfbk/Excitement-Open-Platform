package eu.excitementproject.eop.biutee.classifiers.linearimplementations;
import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.io.LearningModel;
import eu.excitementproject.eop.biutee.classifiers.io.LoadableClassifier;

/**
 * 
 * @author Asher Stern
 * @since Dec 23, 2012
 *
 */
public class LinearClassifierWithLeadingConstantLoadable extends LinearClassifierWithLeadingConstant implements LoadableClassifier
{
	@Override
	public void load(LearningModel model) throws ClassifierException
	{
		try
		{
			LearningModelForLinearClassifierWithLeadingConstant actualModel = (LearningModelForLinearClassifierWithLeadingConstant)model;
			this.parameters = actualModel.getParameters();
			this.trainedOrModelLoaded = true;
		}
		catch(ClassCastException e)
		{
			throw new ClassifierException("Wrong learning-model for "+LinearClassifierWithLeadingConstantLoadable.class.getSimpleName()+
					"\nGiven: "+model.getClass().getSimpleName()+", but expected: "+LearningModelForLinearClassifierWithLeadingConstant.class.getSimpleName(),e);
		}
	}
}
