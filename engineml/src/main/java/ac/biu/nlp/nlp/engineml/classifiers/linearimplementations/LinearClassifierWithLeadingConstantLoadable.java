package ac.biu.nlp.nlp.engineml.classifiers.linearimplementations;
import ac.biu.nlp.nlp.engineml.classifiers.ClassifierException;
import ac.biu.nlp.nlp.engineml.classifiers.io.LearningModel;
import ac.biu.nlp.nlp.engineml.classifiers.io.LoadableClassifier;

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
