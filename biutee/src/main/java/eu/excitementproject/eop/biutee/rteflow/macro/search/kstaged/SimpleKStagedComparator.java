package eu.excitementproject.eop.biutee.rteflow.macro.search.kstaged;
import eu.excitementproject.eop.transformations.utilities.SingleTreeEvaluations;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * Compares elements of {@link KStagedElement} by current cost and
 * gap-estimation.
 * <P>
 * In the simple mode - it calculates a weighted sum of the cost and 
 * the gap, and compares it between the two elements.
 * <P>
 * In "dynamic weightening" mode, it changes the weight of the gap
 * in each iteration.
 * <BR>
 * The change, in dynamic mode, is done as follows:
 * <BR>
 * original-weight + (iteration-number^2)*d
 * <BR>
 * where d is a "decay" factor. d is computed such that for a pre-defined
 * iteration, i, the weight-of-gap will be equal to the weight-of-cost.
 * Thus, to work in the dynamic-weightening mode, the user should give
 * the parameter i in the constructor. 
 * @author Asher Stern
 * @since September 2011
 *
 */
public class SimpleKStagedComparator implements ByIterationComparator<KStagedElement>
{
	
	public SimpleKStagedComparator(double weightOfCost, double weightOfFuture)
	{
		super();
		this.weightOfCost = weightOfCost;
		this.weightOfFuture = weightOfFuture;
		dynamicWeightening = false;
	}
	
	/**
	 * Constructor to work in the dynamic weightening mode. The parameters
	 * are described in the main comment of this class.
	 * 
	 * @param weightOfCost weight-of-cost
	 * @param weightOfFuture weight-of-future
	 * @param dynamicWeightening_iterationOfEquality the parameter <tt>i</tt>
	 * in which the weight-of-cost = weight-of-future. See main comment of this class.
	 * @throws TeEngineMlException
	 */
	public SimpleKStagedComparator(double weightOfCost, double weightOfFuture,
			int dynamicWeightening_iterationOfEquality) throws TeEngineMlException
	{
		super();
		this.weightOfCost = weightOfCost;
		this.weightOfFuture = weightOfFuture;
		this.dynamicWeightening_iterationOfEquality = dynamicWeightening_iterationOfEquality;
		this.originalWeightOfFuture = weightOfFuture;
		dynamicWeightening = true;
		
		if (weightOfCost<=weightOfFuture)
			throw new TeEngineMlException("these weights are not supported: weightOfCost="+weightOfCost+". weightOfFuture="+weightOfFuture);
		
		this.dynamicWeighteningDecay =
		(weightOfCost-weightOfFuture)/(
		((double)dynamicWeightening_iterationOfEquality)*((double)dynamicWeightening_iterationOfEquality)
		);
		
		
	}




	public int compare(KStagedElement o1, KStagedElement o2)
	{
		final double eval1 = getEval(o1);
		final double eval2 = getEval(o2);
		if (eval1<eval2)return -1;
		else if (eval1==eval2)return 0;
		else return 1;
	}

	public void setIteration(int iteration)
	{
		if (dynamicWeightening)
		{
			double doubleIter = (double) iteration;
			this.weightOfFuture=originalWeightOfFuture+
			doubleIter*doubleIter*dynamicWeighteningDecay;
		}
	}
	
	public static double getGap(SingleTreeEvaluations evaluations)
	{
		return 
		evaluations.getMissingLemmas()+
		evaluations.getMissingNodes()+
		evaluations.getMissingRelations();
		
	}

	private double getEval(KStagedElement element)
	{
		double eval =
			weightOfFuture*
			getGap(element.getEvaluations())
			+
			weightOfCost*
			element.getCost();

		return eval;
	}
	
	

	private double weightOfCost;
	private double weightOfFuture;
	
	@SuppressWarnings("unused")
	private int dynamicWeightening_iterationOfEquality;
	private boolean dynamicWeightening = false;
	private double originalWeightOfFuture;
	
	private double dynamicWeighteningDecay = 0; 
}
