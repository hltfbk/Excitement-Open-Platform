package eu.excitementproject.eop.biutee.classifiers.io;

import eu.excitementproject.eop.biutee.classifiers.Classifier;
import eu.excitementproject.eop.biutee.utilities.safemodel.classifiers_io.SafeClassifiersIO;
import eu.excitementproject.eop.biutee.utilities.safemodel.classifiers_io.SafeLearningModel;

/**
 * Learning-model - for saving the learning model of a {@link Classifier}
 * in an XML file.
 * <B>See instructions about classifiers in the package-java-doc (package-info.java)
 * of package <code>eu.excitementproject.eop.biutee.classifiers</code></B>
 * <P>
 * 
 * @see LoadableClassifier
 * @see StorableClassifier
 * @see LearningModelToClassifier
 * @see SafeClassifiersIO
 * @see SafeLearningModel
 * 
 * @author Asher Stern
 * @since Dec 23, 2012
 *
 */
public abstract class LearningModel
{
	public abstract String getClassifierClassOfModel();
	
	public abstract LearningModel getNestedModel();
	
	public abstract void setClassifierClassOfModel(String classifierClassOfModel);
	
	public abstract void setNestedModel(LearningModel learningModel);
	
	public abstract String getDescriptionOfTraining();
	
	public abstract void setDescriptionOfTraining(String descriptionOfTraining);
}
