package eu.excitementproject.eop.transformations.utilities;

/**
 * 
 * Just stores two double values, which should be mean and standard-deviation.
 * 
 * @author Asher Stern
 * @since Jul 26, 2011
 *
 */
public class MeanAndStandardDeviation
{
	
	public MeanAndStandardDeviation(double mean, double standardDeviation)
	{
		super();
		this.mean = mean;
		this.standardDeviation = standardDeviation;
	}
	
	
	public double getMean()
	{
		return mean;
	}
	public double getStandardDeviation()
	{
		return standardDeviation;
	}


	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(mean);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(standardDeviation);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		MeanAndStandardDeviation other = (MeanAndStandardDeviation) obj;
		if (Double.doubleToLongBits(mean) != Double
				.doubleToLongBits(other.mean))
			return false;
		if (Double.doubleToLongBits(standardDeviation) != Double
				.doubleToLongBits(other.standardDeviation))
			return false;
		return true;
	}



	private final double mean;
	private final double standardDeviation;

}
