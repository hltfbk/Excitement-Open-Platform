package ac.biu.nlp.nlp.engineml.classifiers.io;
import ac.biu.nlp.nlp.engineml.classifiers.ClassifierException;

/**
 * 
 * @author Asher Stern
 * @since Dec 23, 2012
 *
 */
public class LearningModelToClassifier
{
	public static LoadableClassifier createForModel(LearningModel learningModel) throws ClassifierException
	{
		try
		{
			Class<?> clazz = Class.forName( 
					learningModel.getClassifierClassOfModel());
			LoadableClassifier classifier = (LoadableClassifier) clazz.newInstance();
			classifier.load(learningModel);
			return classifier;
		}
		catch (ClassNotFoundException e)
		{
			throw new ClassifierException("Failed to load classifier.",e);
		}
		catch (InstantiationException e)
		{
			throw new ClassifierException("Failed to load classifier.",e);
		}
		catch (IllegalAccessException e)
		{
			throw new ClassifierException("Failed to load classifier.",e);
		}
		catch(ClassCastException e)
		{
			throw new ClassifierException("Failed to load classifier.",e);
		}
	}
}
