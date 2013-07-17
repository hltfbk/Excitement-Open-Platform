package eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl;

import java.util.ArrayList;
import java.util.List;

import eu.excitementproject.eop.biutee.classifiers.Classifier;
import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.ClassifierUtils;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Instance;
import eu.excitementproject.eop.biutee.rteflow.endtoend.InstanceAndProof;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Proof;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Results;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;

/**
 * 
 * @author Asher Stern
 * @since Jul 15, 2013
 *
 * @param <I>
 * @param <P>
 */
public abstract class DefaultAbstractResults<I extends Instance, P extends Proof> extends Results<I, P>
{
	protected DefaultAbstractResults(List<InstanceAndProof<I, P>> proofs, Classifier classifierForPredictions, boolean f1_optimized) throws BiuteeException
	{
		super(proofs, classifierForPredictions);
		this.f1_optimized = f1_optimized;
	}
	
	@Override
	public void compute() throws BiuteeException
	{
		this.computable = canItBeComputable();
		try
		{
			computeClassifications();
			if (this.computable)
			{
				computeTrueAndFalse();
				computeSuccessRates();
			}
		}
		catch (ClassifierException e)
		{
			throw new BiuteeException("Failed to classify the given instances.",e);
		}
		computeHasBeenCalled=true;
	}
	
	@Override
	public Double getSuccessRate() throws BiuteeException
	{
		if (!computeHasBeenCalled) throw new BiuteeException("Caller\'s bug: compute() has not been called yet.");
		if (!this.computable) return null;
		if (f1_optimized)
		{
			return f1;
		}
		else
		{
			return accuracy;
		}
	}
	
	@Override
	public String print() throws BiuteeException
	{
		if (!computeHasBeenCalled) throw new BiuteeException("Caller\'s bug: compute() has not been called yet.");
		if (!this.computable) return "Results cannot be computed. It seems that the given dataset was unannotated.";

		return "Results: " +
				"Accuracy = "+strDouble(accuracy)+
				", Recall = "+strDouble(recall)+
				", Precision = "+strDouble(precision)+
				", F1 = "+strDouble(f1);
	}
	
	@Override
	public String toString()
	{
		if (isComputeHasBeenCalled())
		{
			try
			{
				return print();
			}
			catch(BiuteeException e)
			{
				return "Results: unknown.";
			}
		}
		else
		{
			return "Results: unknown.";
		}
	}
	
	
	
	
	/////////////// PROTECTED & PRIVATE ///////////////

	protected boolean isComputeHasBeenCalled()
	{
		return computeHasBeenCalled;
	}

	

	private static final String strDouble(Double d)
	{
		if (null==d) return "null";
		else return String.format("%-3.4f", d.doubleValue());
	}
	
	protected boolean canItBeComputable() throws BiuteeException
	{
		boolean canBeComputable = true;
		for (InstanceAndProof<I, P> instanceAndProof : proofs)
		{
			if (null==instanceAndProof.getInstance().getBinaryLabel())
			{
				canBeComputable=false;
			}
		}
		return canBeComputable;
	}
	
	protected void computeClassifications() throws ClassifierException
	{
		classifications = new ArrayList<>(proofs.size());
		for (InstanceAndProof<I, P> proof : proofs)
		{
			double score = classifierForPredictions.classify(proof.getProof().getFeatureVector());
			boolean classificationBoolean = ClassifierUtils.classifierResultToBoolean(score);
			
			classifications.add(new InstanceAndProofAndClassification<I,P>(proof,score,classificationBoolean));
		}
	}
	
	protected void computeTrueAndFalse() throws BiuteeException
	{
		for (InstanceAndProofAndClassification<I,P> classification : classifications)
		{
			boolean goldStandard = classification.getInstanceAndProof().getInstance().getBinaryLabel().booleanValue();
			boolean classifiedAs = classification.getClassification();
			
			if (goldStandard&&classifiedAs)
			{
				++truePositive;
			}
			else if ((!goldStandard)&&classifiedAs)
			{
				++falsePositive;
			}
			else if (goldStandard&&(!classifiedAs))
			{
				++falseNegative;
			}
			else if ((!goldStandard)&&(!classifiedAs))
			{
				++trueNegative;
			}
			else
			{
				throw new BiuteeException("BUG");
			}
		}
	}
	
	protected void computeSuccessRates()
	{
		double _truePositive=(double)truePositive;
		double _falsePositive=(double)falsePositive;
		double _trueNegative=(double)trueNegative;
		double _falseNegative=(double)falseNegative;
		
		double all = _truePositive+_falsePositive+_trueNegative+_falseNegative;
		double correct = _truePositive+_trueNegative;
		
		accuracy = correct/all;
		
		if ((_truePositive+_falseNegative)>0)
		{
			recall = _truePositive/(_truePositive+_falseNegative);
		}
		if ((_truePositive+_falsePositive)>0)
		{
			precision = _truePositive/(_truePositive+_falsePositive);
		}
		if ( (recall!=null) && (precision!=null) )
		{
			f1 = 2*recall*precision/(recall+precision);
		}
	}
	

	protected final boolean f1_optimized;
	private boolean computeHasBeenCalled = false;
	
	protected boolean computable = false;
	
	protected List<InstanceAndProofAndClassification<I,P>> classifications;
	
	protected int truePositive=0;
	protected int falsePositive=0;
	protected int trueNegative=0;
	protected int falseNegative=0;
	
	protected double accuracy=0.0;
	protected Double recall=null;
	protected Double precision=null;
	protected Double f1=null;
}
