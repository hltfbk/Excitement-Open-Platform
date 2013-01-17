package eu.excitementproject.eop.transformations.operations.rules.lexicalchain;
import java.io.Serializable;

public class ConfidenceChainItem implements Serializable
{
	private static final long serialVersionUID = 7636354280070501409L;
	
	public ConfidenceChainItem(String ruleBaseName, double confidence)
	{
		super();
		this.ruleBaseName = ruleBaseName;
		this.confidence = confidence;
	}
	
	
	public String getRuleBaseName()
	{
		return ruleBaseName;
	}
	public double getConfidence()
	{
		return confidence;
	}
	
	
	


	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(confidence);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((ruleBaseName == null) ? 0 : ruleBaseName.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConfidenceChainItem other = (ConfidenceChainItem) obj;
		if (Double.doubleToLongBits(confidence) != Double
				.doubleToLongBits(other.confidence))
			return false;
		if (ruleBaseName == null)
		{
			if (other.ruleBaseName != null)
				return false;
		} else if (!ruleBaseName.equals(other.ruleBaseName))
			return false;
		return true;
	}





	private final String ruleBaseName;
	private final double confidence;
}
