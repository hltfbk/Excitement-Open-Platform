package eu.excitementproject.eop.biutee.classifiers.io;

/**
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
