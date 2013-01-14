package eu.excitementproject.eop.biutee.classifiers.f1_logicstic_regression;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.ClassifierUtils;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;


/**
 * Implementation of the Algorithm in paper:
 * "Maximum expected F-measure training of logistic regression models", Figure-1
 * 
 * @author Asher Stern
 * @since Mar 7, 2012
 *
 */
public class F_and_dF_calculator
{
	public F_and_dF_calculator(int alg_k, double[] alg_theta,
			List<LabeledSample> samples, double alg_alpha, double alg_gamma_sigmoid_coefficient) throws ClassifierException
	{
		super();
		this.alg_k = alg_k;
		this.alg_theta = alg_theta;
		this.samples = samples;
		this.alg_alpha = alg_alpha;
		this.alg_gamma_sigmoid_coefficient = alg_gamma_sigmoid_coefficient;
		
		verifyArguments();
	}




	/**
	 * Implementation of the algorithm in Figure 1 of the paper.
	 */
	public void calculate()
	{
		// I have to calculate npos before starting the algorithm
		int int_alg_npos = 0;
		for (LabeledSample sample : samples)
		{
			if (sample.getLabel()==true)
				++int_alg_npos;
		}
		double alg_npos = (double)int_alg_npos;
		
		// Algorithm starts here:
		
		double alg_m = 0; // line 1
		double alg_A = 0; // line 2
		
		double[] alg_dm = new double[alg_k];
		double[] alg_dA = new double[alg_k];
		for (int alg_j=0;alg_j<alg_k;++alg_j) // line 3
		{
			alg_dm[alg_j] = 0; // line 4
			alg_dA[alg_j] = 0; // line 5
		}
		
		for (LabeledSample sample : samples) // line 6
		{
			Map<Integer, Double> alg_x_i = sample.getFeatures();
			double alg_y_i = (sample.getLabel()==true)?1:-1;
			
			double alg_p = 0; // line 7
			for (int alg_j=0;alg_j<alg_k;++alg_j) // line 8
			{
				alg_p += alg_x_i.get(alg_j).doubleValue()*alg_theta[alg_j]; // line 9
			}
			alg_p = ClassifierUtils.sigmoid(this.alg_gamma_sigmoid_coefficient*alg_p); // line 10 (there is a typo in the algorithm). (In addition - I use the gamma in the product)
			alg_m += alg_p; // line 11
			
			if (alg_y_i == 1) // line 12
			{
				alg_A += alg_p; // line 13
			}
			
			for (int alg_j=0;alg_j<alg_k;++alg_j) // line 14
			{
				double alg_t = alg_p*(1-alg_p)*alg_x_i.get(alg_j); // line 15
				alg_dm[alg_j] += alg_t; // line 16
				if (alg_y_i == 1) // line 17
				{
					alg_dA[alg_j] += alg_t; // line 18
				}
			}
		}
		
		double alg_h = 1/(alg_alpha*alg_npos+(1-alg_alpha)*alg_m); // line 19
		alg_F = alg_h*alg_A; // line 20
		double alg_t = alg_F*(1-alg_alpha); // line 21
		for (int alg_j=0;alg_j<alg_k;++alg_j) // line 22
		{
			alg_dF.put(alg_j, alg_h*(alg_dA[alg_j]-alg_t*alg_dm[alg_j])); // line 23
		}
	}
	
	
	

	public double get_F()
	{
		return alg_F;
	}
	public Map<Integer, Double> get_dF()
	{
		return alg_dF;
	}
	



	private void verifyArguments() throws ClassifierException
	{
		if (alg_theta.length!=alg_k) throw new ClassifierException("alg_theta.length (= "+alg_theta.length+") != alg_k (= "+alg_k+")");
		if (null==samples) throw new ClassifierException("null==samples");
		if (alg_alpha <= 0) throw new ClassifierException("alg_alpha <= 0");
		if (alg_alpha > 1) throw new ClassifierException("alg_alpha > 1");
		if (samples.size()==0) throw new ClassifierException("samples.size()==0");
		if (this.alg_gamma_sigmoid_coefficient<=0) throw new ClassifierException("alg_gamma_sigmoid_coefficient<=0");
		// sanity check of feature-indexes
		Set<Integer> sampleKeySet = samples.get(0).getFeatures().keySet();
		boolean[] featureIndexFound = new boolean[alg_k];
		for (int i=0;i<featureIndexFound.length;++i){featureIndexFound[i]=false;}
		for (Integer featureIndex : sampleKeySet)
		{
			if (featureIndex<0) throw new ClassifierException("featureIndex<0");
			if (featureIndex>(alg_k-1)) throw new ClassifierException("featureIndex>(alg_k-1)");
			featureIndexFound[featureIndex] = true;
		}
		for (int i=0;i<featureIndexFound.length;++i)
		{
			if (false==featureIndexFound[i]) throw new ClassifierException("sanity check of feature indexes failed.");
		}
	}

	// input
	private int alg_k; // number of features
	private double[] alg_theta; // weight vector: from 0 to alg_k
	private List<LabeledSample> samples; // each sample has feature vector, such that the feature indexes are from 0(including) to alg_k(excluding)
	private double alg_alpha;
	private double alg_gamma_sigmoid_coefficient;
	
	// output
	private double alg_F = 0;
	private Map<Integer,Double> alg_dF = new LinkedHashMap<Integer, Double>();
}
