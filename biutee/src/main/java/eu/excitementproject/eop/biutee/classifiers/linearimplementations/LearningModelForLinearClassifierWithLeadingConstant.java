package eu.excitementproject.eop.biutee.classifiers.linearimplementations;
import java.util.Map;

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
public class LearningModelForLinearClassifierWithLeadingConstant extends LearningModel
{
	public LearningModelForLinearClassifierWithLeadingConstant(
			Map<Integer, Double> parameters, String classifierClassOfModel,
			LearningModel nestedModel, String descriptionOfTraining)
	{
		super();
		this.parameters = parameters;
		this.classifierClassOfModel = classifierClassOfModel;
		this.nestedModel = nestedModel;
		this.descriptionOfTraining = descriptionOfTraining;
	}
	
	public LearningModelForLinearClassifierWithLeadingConstant(){}

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
	
	

	public Map<Integer, Double> getParameters()
	{
		return parameters;
	}

	public void setParameters(Map<Integer, Double> parameters)
	{
		this.parameters = parameters;
	}

	public void setNestedModel(LearningModel nestedModel)
	{
		this.nestedModel = nestedModel;
	}

	public void setClassifierClassOfModel(String classifierClassOfModel)
	{
		this.classifierClassOfModel = classifierClassOfModel;
	}
	
	@Override
	public String getDescriptionOfTraining()
	{
		return descriptionOfTraining;
	}
	
	@Override
	public void setDescriptionOfTraining(String descriptionOfTraining)
	{
		this.descriptionOfTraining = descriptionOfTraining;
		if (nestedModel!=null)
		{
			nestedModel.setDescriptionOfTraining(null);
		}
	}


	private Map<Integer,Double> parameters;
	private String classifierClassOfModel;
	private LearningModel nestedModel;
	private String descriptionOfTraining;
 
}
