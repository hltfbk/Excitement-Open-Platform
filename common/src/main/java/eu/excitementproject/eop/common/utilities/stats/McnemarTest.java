package eu.excitementproject.eop.common.utilities.stats;

/**
 * Compute Mcnemar style chi_sqaure and confidence level
 * <p>
 * This class was originally in project BURST, based on the article " approximate statistical tests for comparing supervised classification learning algorithms.
Dietterich Thomas" 
 * 
 * @author Jonathan Berant
 * @since 2 Jan 2010
 * 
 */
public class McnemarTest 
{	
	/**
	 * Given two classifiers, the null hypothesis is that their error rate
	 * is similar. 
	 * 
	 * @param n_01 - examples misclassified by the first classifier and not by the second
	 * @param n_10 - examples misclassified by the second classifier and not by the first
	 * @return chi-sqaure
	 */
	public static double chi_sqaure(int n_01, int n_10) 
	{		
		double nominator = Math.pow(Math.abs(n_01 - n_10) - 1 , 2);
		double denominator = n_01 + n_10;	
		return nominator/denominator;
	}
	
	/**
	 * Return the value's confidence level
	 * 
	 * @param value
	 * @return the value's confidence level
	 */
	public static double confidenceLevel(double value)
	{	
		if(value>P_0001)
			return 0.001;
		if(value>P_001)
			return 0.01;
		if(value>P_002)
			return 0.02;
		if(value>P_005)
			return 0.05;
		if(value>P_01)
			return 0.1;
		if(value>P_05)
			return 0.5;
		return 1;
	}
	
	private final static double P_05=0.455;
	private final static double P_01=2.706;
	private final static double P_005=3.841;
	private final static double P_002=5.412;
	private final static double P_001=6.635;
	private final static double P_0001=10.827;
	
	
	public static void main(String[] args) {
		int n_01 = 794; 
		int n_10 = 125;
		double chi = McnemarTest.chi_sqaure(n_01, n_10);
		double confidence = McnemarTest.confidenceLevel(chi);
		System.out.println("McNemar Test: confidence level\t"+confidence);
	}
}


