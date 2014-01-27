package eu.excitementproject.eop.common.utilities.math;

import java.util.Random;

/**
 * A pseudo-random-generator that generates numbers according to
 * normal (Gaussian) distribution.
 * 
 * see http://www.taygeta.com/random/gaussian.html
 * and http://www.taygeta.com/random/boxmuller.html
 * @author Asher Stern
 * @since Jul 26, 2011
 *
 */
public class GaussianPseudoRandomGenerator
{
	public GaussianPseudoRandomGenerator()
	{
		random = new Random();
	}

	public GaussianPseudoRandomGenerator(long randomSeed)
	{
		random = new Random(randomSeed);
	}

	
	public double generate(double mean, double standardDeviation)
	{
		double y1=0;
		if (useLast)
		{
			y1=y2;
			useLast=false;
		}
		else
		{
			double x1=0;
			double x2=0;
			double w=0;
			do
			{
				x1 = 2.0*random.nextDouble() -1.0;
				x2 = 2.0*random.nextDouble() -1.0;
				w = x1*x1 +x2*x2;
			}
			while(w >= 1.0);
			w = Math.sqrt( (-2.0*Math.log(w))/w );
			y1 = x1*w;
			y2 = x2*w;
			useLast=true;
		}
		
		return (mean + y1*standardDeviation);
	}
	
	private Random random = null;
	private double y2 = 0;
	private boolean useLast=false;
}
