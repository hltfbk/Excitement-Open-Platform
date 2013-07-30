package eu.excitementproject.eop.core;

import java.util.List;
import java.util.ArrayList;

import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.TEDecision;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;

import eu.fbk.hlt.pso.*;


/**
 * The <code>EditDistancePSOEDA</code> class extends the <code>EditDistanceEDA</code> class.
 * Given a certain configuration, it can be trained over a specific data set in order to optimize its
 * performance. In the training phase this class produces a distance model for the data set, which
 * includes a distance threshold that best separates the positive and negative examples in the training data.
 * 
 * Differently to the class EditDistanceEDA the weights of the edit distance operations are calculated
 * and optimized by Particle Swarm Optimization (PSO): Kennedy, J.; Eberhart, R. (1995). Particle Swarm 
 * Optimization. Proceedings of IEEE International Conference on Neural Networks IV. pp. 1942–1948.
 * 
 * During the test phase it applies the calculated threshold, so that pairs resulting in a distance below the
 * threshold are classified as ENTAILMENT, while pairs above the threshold are classified as NONENTAILMENT.
 * <code>EditDistanceEDA</code> uses <code>FixedWeightEditDistance</code> for calculating edit distance
 * between each pair of T and H. 
 * 
 * @author  Roberto Zanoli
 * 
 * @version 0.2
 */
public class EditDistancePSOEDA<T extends TEDecision> extends EditDistanceEDA<T> {
	
	//The following 3 parameters determines when PSO stops
	//max number of iteration of PSO
	private int maxIteration = -1;
	//max number of iteration without any changes in results
	private int maxIterationWithoutChanges = -1;
	//the error tolerance used by PSO to terminate 
	private double errorTolerance = -1;
	
	//the swarm size of PSO
	private int swarmSize = -1;
	//the number of processors to be used for running PSO
	private int processors = -1;
	
	
	/**
	 * Construct an edit distance EDA.
	 */
	public EditDistancePSOEDA() {
		
		super();
		
	}
	
	/*
	 * PSO initialization
	 * 
	 * @param config The configuration file
	 * 
	 * @return an instance of PSO
	 */
	private PSO initializePSO(CommonConfig config) throws ConfigurationException, EDAException, ComponentException {
		
		PSO pso = null;
		
		try {
			
			//the range of values of the delete operation where PSO has to select a value from
			Value mDeleteWeighValue = null;
			//the range of values of the insert operation where PSO has to select a value from
			Value mInsertWeighValue = null;
			//the range of values of the substitute operation where PSO has to select a value from
		    Value mSubstituteWeighValue = null;
			
			//getting the name value table
			NameValueTable nameValueTable = config.getSection(getCanonicalName());
			
			//the max number of the iteration of PSO
			if (this.maxIteration == -1)
				this.maxIteration = Integer.parseInt(nameValueTable.getString("maxIteration"));
			//the swarm size
			if (this.swarmSize == -1)
				this.swarmSize = Integer.parseInt(nameValueTable.getString("swarmSize"));
			//processors to be used to run PSO
			if (this.processors == -1)
				this.processors = Integer.parseInt(nameValueTable.getString("processors"));
			//the tolerance error 
			if (this.errorTolerance == -1)
				this.errorTolerance = Double.parseDouble(nameValueTable.getString("errorTolerance"));
			if (this.maxIterationWithoutChanges == -1)
				this.maxIterationWithoutChanges = Integer.parseInt(nameValueTable.getString("maxIterationWithoutChanges"));
			
			//the range of values of the delete operation
			if (mDeleteWeighValue == null) {
				String deleteValuesRange = nameValueTable.getString("deleteValuesRange");
				mDeleteWeighValue = 
						new Value(Double.parseDouble(deleteValuesRange.split(",")[0]), 
								Double.parseDouble(deleteValuesRange.split(",")[1]));
			}
			//the range of values of the insert operation
			if (mInsertWeighValue == null) {
				String insertValuesRange = nameValueTable.getString("insertValuesRange");
				mInsertWeighValue = new Value(Double.parseDouble(insertValuesRange.split(",")[0]), 
						Double.parseDouble(insertValuesRange.split(",")[1]));
			}
			//the range of values of the substitute operation
			if (mSubstituteWeighValue == null) {
				String substituteValuesRange = nameValueTable.getString("substituteValuesRange");
				mSubstituteWeighValue = new Value(Double.parseDouble(substituteValuesRange.split(",")[0]), 
						Double.parseDouble(substituteValuesRange.split(",")[1]));
			}
			
			//EditDistanceFitnessFunction represents the function to be optimized by PSO
			EditDistanceFitnessFunction.CONFIG = config;
			EditDistanceFitnessFunction.CANONICAL_NAME = getCanonicalName();
			
			Class<?> fitnessFunction = EditDistanceFitnessFunction.class;
			
			//this is the space of the problem to be solved by PSO
			//in the present work it has 3 dimensions: delete, insert, substitute
			List<Value> problemSpace = new ArrayList<Value>();	
			//adding the 3 dimensions
			problemSpace.add(mDeleteWeighValue );
			problemSpace.add(mInsertWeighValue );
			problemSpace.add(mSubstituteWeighValue);
			
			//problem initialization
			ProblemSet problemSet = new ProblemSet(fitnessFunction, problemSpace);
			problemSet.setMaxIteration(this.maxIteration);
			problemSet.setMaxNumberOfIterationsWithoutChanges(this.maxIterationWithoutChanges);
			problemSet.setSwarmSize(this.swarmSize);
			problemSet.setProcessors(this.processors);
			problemSet.setErrorTolerance(this.errorTolerance);
			
			//initialize PSO and run it
			pso = new PSO(problemSet);
			
		} catch (ConfigurationException e) {
			throw e;
		} catch (Exception e) {
			throw new EDAException(e.getMessage());
		}
		
		return pso;
		
	}
	
	@Override
	public void startTraining(CommonConfig config) throws ConfigurationException, EDAException, ComponentException {
		
		try {
		
			logger.info("startTraining()");
			
			//EDA initialization
			initialize(config);
			
			PSO pso = initializePSO(config);
			Position gBestPosition = pso.execute();
			
			//these are the optimized values of the edit distance operations calculated by PSO
			setmMatchWeight(0.0);
			setmDeleteWeight(gBestPosition.getValue()[0]);
			setmInsertWeight(gBestPosition.getValue()[1]);
			setmSubstituteWeight(gBestPosition.getValue()[2]);
			
			//set the edit distance component with the optimized weights calculated by PSO
			getComponent().setmMatchWeight(getmMatchWeight());
			getComponent().setmDeleteWeight(getmDeleteWeight());
			getComponent().setmInsertWeight(getmInsertWeight());
			getComponent().setmSubstituteWeight(getmSubstituteWeight());
			
			//train EditDistanceEDA with the optimized weights
			super.startTraining(config);
			
		} catch (Exception e) {
			throw new EDAException(e.getMessage());
		}
		
	}
	
}
