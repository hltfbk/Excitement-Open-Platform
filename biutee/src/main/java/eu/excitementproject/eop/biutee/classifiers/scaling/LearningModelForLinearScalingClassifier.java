package eu.excitementproject.eop.biutee.classifiers.scaling;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessorOrder;

import eu.excitementproject.eop.biutee.classifiers.io.LearningModel;
import eu.excitementproject.eop.common.datastructures.DummySet;

/**
 * 
 * @author Asher Stern
 * @since Dec 25, 2012
 *
 */
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
public class LearningModelForLinearScalingClassifier extends LearningModel
{
	public LearningModelForLinearScalingClassifier(Map<Integer, Double> maxMap,
			Map<Integer, Double> minMap, Set<Integer> doNotScale,
			LearningModel nestedModel, String descriptionOfTraining)
	{
		super();
		this.maxMap = maxMap;
		this.minMap = minMap;
		this.doNotScale = doNotScale;
		this.nestedModel = nestedModel;
		this.descriptionOfTraining = descriptionOfTraining;
		if (this.nestedModel!=null)
		{
			this.nestedModel.setDescriptionOfTraining(null);
		}
	}
	
	public LearningModelForLinearScalingClassifier(){}

	@Override
	public String getClassifierClassOfModel()
	{
		return LinearScalingLoadableClassifier.class.getName();
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

	public Map<Integer, Double> getMaxMap()
	{
		return maxMap;
	}

	public void setMaxMap(Map<Integer, Double> maxMap)
	{
		this.maxMap = maxMap;
	}

	public Map<Integer, Double> getMinMap()
	{
		return minMap;
	}

	public void setMinMap(Map<Integer, Double> minMap)
	{
		this.minMap = minMap;
	}

	public Set<Integer> getDoNotScale()
	{
		return doNotScale;
	}

	public void setDoNotScale(Set<Integer> doNotScale)
	{
		this.doNotScale = doNotScale;
	}
	
	@Override
	public void setClassifierClassOfModel(String classifierClassOfModel)
	{
		// Do nothing...
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
		if (this.getNestedModel()!=null)
		{
			this.getNestedModel().setDescriptionOfTraining(null);
		}
	}











	private Map<Integer,Double> maxMap;
	private Map<Integer,Double> minMap;
	
	private Set<Integer> doNotScale = new DummySet<Integer>();
	
	private LearningModel nestedModel;
	
	private String descriptionOfTraining;
}
