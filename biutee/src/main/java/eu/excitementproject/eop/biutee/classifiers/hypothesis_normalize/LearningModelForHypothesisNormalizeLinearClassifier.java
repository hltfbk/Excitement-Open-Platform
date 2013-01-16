package eu.excitementproject.eop.biutee.classifiers.hypothesis_normalize;
import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessorOrder;

import eu.excitementproject.eop.biutee.classifiers.io.LearningModel;


/**
 * 
 * @author Asher Stern
 * @since Dec 23, 2012
 *
 */
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
public class LearningModelForHypothesisNormalizeLinearClassifier extends LearningModel
{
	public LearningModelForHypothesisNormalizeLinearClassifier(){}
	
	public LearningModelForHypothesisNormalizeLinearClassifier(
			String classifierClassOfModel, LearningModel nestedModel,
			String descriptionOfTraining)
	{
		super();
		this.classifierClassOfModel = classifierClassOfModel;
		this.nestedModel = nestedModel;
		this.descriptionOfTraining = descriptionOfTraining;
		
		if (this.nestedModel!=null)
		{
			this.nestedModel.setDescriptionOfTraining(null);
		}
	}



	@Override
	public String getClassifierClassOfModel()
	{
		return classifierClassOfModel;
	}

	@Override
	public LearningModel getNestedModel()
	{
		return nestedModel;
	}
	
	
	
	public void setClassifierClassOfModel(String classifierClassOfModel)
	{
		this.classifierClassOfModel = classifierClassOfModel;
	}

	public void setNestedModel(LearningModel nestedModel)
	{
		this.nestedModel = nestedModel;
	}
	
	public String getDescriptionOfTraining()
	{
		return this.descriptionOfTraining;
	}
	
	public void setDescriptionOfTraining(String descriptionOfTraining)
	{
		this.descriptionOfTraining = descriptionOfTraining;
		if (this.getNestedModel()!=null)
		{
			this.getNestedModel().setDescriptionOfTraining(null);
		}
	}




	private String classifierClassOfModel;
	private LearningModel nestedModel;
	private String descriptionOfTraining;
}
