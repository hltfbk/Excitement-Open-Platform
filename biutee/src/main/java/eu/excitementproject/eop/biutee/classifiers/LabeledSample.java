package eu.excitementproject.eop.biutee.classifiers;
import java.io.Serializable;
import java.util.Map;

/**
 * A feature-vector with a true/false label.
 * 
 * @author Asher Stern
 * @since Dec 29, 2010
 *
 */
public class LabeledSample implements Serializable
{
	private static final long serialVersionUID = -5234423992930124431L;
	
	public LabeledSample(Map<Integer, Double> features, boolean label)
	{
		super();
		this.features = features;
		this.label = label;
	}
	
	
	public Map<Integer, Double> getFeatures()
	{
		return features;
	}
	public boolean getLabel()
	{
		return label;
	}
	
	


	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((features == null) ? 0 : features.hashCode());
		result = prime * result + (label ? 1231 : 1237);
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
		LabeledSample other = (LabeledSample) obj;
		if (features == null)
		{
			if (other.features != null)
				return false;
		} else if (!features.equals(other.features))
			return false;
		if (label != other.label)
			return false;
		return true;
	}




	private final Map<Integer, Double> features;
	private final boolean label;

}
