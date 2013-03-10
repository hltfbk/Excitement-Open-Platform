package eu.excitementproject.eop.biutee.classifiers.io;
import eu.excitementproject.eop.biutee.classifiers.ClassifierException;

/**
 * Takes a {@link LearningModel}, and creates a classifier by that model.
 * <P>
 * <B>See instructions about classifiers in the package-java-doc (package-info.java)
 * of package <code>eu.excitementproject.eop.biutee.classifiers</code></B>
 * <P>
 * 
 * @see LearningModel
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
