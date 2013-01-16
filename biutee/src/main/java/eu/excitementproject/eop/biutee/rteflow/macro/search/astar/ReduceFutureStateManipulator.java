package eu.excitementproject.eop.biutee.rteflow.macro.search.astar;
import eu.excitementproject.eop.biutee.rteflow.macro.search.astar.AStarAlgorithm.StateCalculations;
import eu.excitementproject.eop.biutee.rteflow.macro.search.astar.AStarAlgorithm.StateManipulator;

/**
 * 
 * @author Asher Stern
 * @since Jun 23, 2011
 *
 */
public class ReduceFutureStateManipulator implements StateManipulator<AStarElement>
{
	public ReduceFutureStateManipulator(double reduceFutureFactor, double initialWeightOfFuture, GeneratedTreeStateCalculations stateCalculations)
	{
		this.reduceFutureFactor = reduceFutureFactor;
		this.actualWeightOfFuture = initialWeightOfFuture;
		this.stateCalculations = stateCalculations;
	}


	public AStarElement makeManipulation(AStarElement state)
	{
		double future = state.getFutureEstimation();
		future = future*this.reduceFutureFactor;
		
		AStarElement ret =
			new AStarElement(state.getIteration(),
					state.getTree(),
					state.getOriginalSentence(),
					state.getFeatureVector(),
					state.getLastSpec(),
					state.getHistory(),
					state.getParent(),
					state.getCost(),
					state.getUnweightedFutureEstimation(),
					future,
					state.isGoal());
		
		return ret;
	}
	
	public StateCalculations<AStarElement> getNewStateCalculations()
	{
		actualWeightOfFuture = reduceFutureFactor*actualWeightOfFuture;
		stateCalculations.setWeightOfFuture(actualWeightOfFuture);
		return this.stateCalculations;
	}

	

	private final double reduceFutureFactor;
	private final GeneratedTreeStateCalculations stateCalculations;
	
	private double actualWeightOfFuture;
	
}
