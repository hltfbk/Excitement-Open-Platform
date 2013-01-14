package eu.excitementproject.eop.biutee.classifiers.f1_logicstic_regression;
import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessorOrder;

import eu.excitementproject.eop.biutee.classifiers.io.LearningModel;



/**
 * 
 * @author Asher Stern
 * @since Dec 26, 2012
 *
 */
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
public class LearningModelForAdjustingVectorLinearWithGammaClassifier extends LearningModel
{
	public LearningModelForAdjustingVectorLinearWithGammaClassifier(){}
	
	public LearningModelForAdjustingVectorLinearWithGammaClassifier(
			String classifierClassOfModel, LearningModel nestedModel,
			double[] weights, double gammaCoefficientSigmoid,
			String descriptionOfTraining)
	{
		super();
		this.classifierClassOfModel = classifierClassOfModel;
		this.nestedModel = nestedModel;
		this.weights = weights;
		this.gammaCoefficientSigmoid = gammaCoefficientSigmoid;
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
	public void setClassifierClassOfModel(String classifierClassOfModel)
	{
		this.classifierClassOfModel = classifierClassOfModel;
	}
	@Override
	public LearningModel getNestedModel()
	{
		return nestedModel;
	}
	public void setNestedModel(LearningModel nestedModel)
	{
		this.nestedModel = nestedModel;
	}
	public double[] getWeights()
	{
		return weights;
	}
	public void setWeights(double[] weights)
	{
		this.weights = weights;
	}
	public double getGammaCoefficientSigmoid()
	{
		return gammaCoefficientSigmoid;
	}
	public void setGammaCoefficientSigmoid(double gammaCoefficientSigmoid)
	{
		this.gammaCoefficientSigmoid = gammaCoefficientSigmoid;
	}
	

	@Override
	public String getDescriptionOfTraining()
	{
		return this.descriptionOfTraining;
	}
	
	@Override
	public void setDescriptionOfTraining(String descriptionOfTraining)
	{
		this.descriptionOfTraining = descriptionOfTraining;
	}

	
	private String classifierClassOfModel;
	private LearningModel nestedModel;
	
	private double[] weights;
	private double gammaCoefficientSigmoid;
	
	private String descriptionOfTraining;
}
