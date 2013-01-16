package eu.excitementproject.eop.biutee.rteflow.macro.search.kstaged;
import eu.excitementproject.eop.transformations.utilities.SingleTreeEvaluations;

/**
 * 
 * @author Asher Stern
 * @since Sep 25, 2011
 *
 * @param <T>
 */
public class GradientKStagedComparator implements ByIterationComparator<KStagedElement>
{
	////////////////////////////// Public ////////////////////////////////
	
	public GradientKStagedComparator(double weightOfCost, double weightOfFuture)
	{
		super();
		this.weightOfCost = weightOfCost;
		this.weightOfFuture = weightOfFuture;
	}

	
	public int compare(KStagedElement o1, KStagedElement o2)
	{
		Double attractivenessO1 = getAttractivenessOfElement(o1);
		Double attractivenessO2 = getAttractivenessOfElement(o2);
		if (attractivenessO1==attractivenessO2)return 0;
		else if (null==attractivenessO1)return 1;
		else if (null==attractivenessO2)return -1;
		else return Double.compare(attractivenessO1, attractivenessO2);
	}

	public void setIteration(int iteration)
	{
		// Do nothing
	}
	
	///////////////////////// Protected & Private ////////////////////////////
	
	
	protected Double getAttractivenessOfElement(KStagedElement element) throws GradientKStagedComparatorBugException
	{
		Double ret = null;
		double deltaGap = getFutureEstimation(element.getEvaluationsOfOriginalTree())-getFutureEstimation(element.getEvaluations());
		if (deltaGap<=0)
			ret = null;
		else
		{
			double deltaCost = element.getCost()-element.getCostOfOriginalTree();
			if (deltaCost<0)throw new GradientKStagedComparatorBugException("deltaCost<0");
//			ret = deltaCost/deltaGap;
			ret = getAttractivenessByCostAndGap(deltaCost,deltaGap);
		}
		return ret;
	}
	
	protected double getAttractivenessByCostAndGap(double deltaCost, double deltaGap)
	{
		if ( (1==weightOfCost) && (1==weightOfFuture) )
			return deltaCost/deltaGap;
		else
		{
			return Math.pow(deltaCost, weightOfCost)/Math.pow(deltaGap, weightOfFuture);
		}
	}
	
	// TODO CHANGE TO PROTECTED
	public static double getFutureEstimation(SingleTreeEvaluations evaluations)
	{
		return evaluations.getMissingLemmas()+evaluations.getMissingNodes()+evaluations.getMissingRelations();
	}
	
	@SuppressWarnings("serial")
	private static class GradientKStagedComparatorBugException extends RuntimeException
	{
		public GradientKStagedComparatorBugException(String message){super(message);}
	}
	
	
	private double weightOfCost;
	private double weightOfFuture;
}
