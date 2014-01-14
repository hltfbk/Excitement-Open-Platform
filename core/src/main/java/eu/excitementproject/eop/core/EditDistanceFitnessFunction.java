package eu.excitementproject.eop.core;

import eu.fbk.hlt.pso.*;
import java.io.*;

import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;

/**
 * The <code>EditDistanceFitnessFunction</code> class extends the <code>FitnessFunction</code> class.
 * The class represent the function to be optimized by PSO: 1 - accuracy
 * The accuracy is calculated by running EditDistanceEDA with different weights of the edit
 * distance operations as calculated by PSO.
 * 
 * @author Roberto Zanoli
 * 
 */
public class EditDistanceFitnessFunction extends FitnessFunction {
	
	/* 
	 * the EDA configuration file that the EDA requires.
	 */
	static CommonConfig CONFIG;
	
	@Override
	public FitnessFunction call() throws Exception {
		
		/*
		 * the configuration file required by the selected EDA
		 */
		String fileName = CONFIG.getConfigurationFileName(); 
		
		CommonConfig cc = new ImplCommonConfig(new File(fileName));
		
		/*
		 * EDA initialization; different weights configuration are tried by
		 * running EditDistanceEDA.
		 */
		EditDistanceEDA<EditDistanceTEDecision> editDistanceEDA = 
				new EditDistanceEDA<EditDistanceTEDecision>();
		
		/*
		 * during the training the EDA don't need to save the model
		 */
		editDistanceEDA.setWriteModel(false);
		
		/*
		 * the weights of the edit distance operations are set dynamically by PSO
		 * they are in the same order which are inserted in the array.
		 */
		double mDeleteWeight = position.getValue()[0];
		double mInsertWeight = position.getValue()[1];
		double mSubstituteWeight = position.getValue()[2];
		
		/*
		 * updating the weights of the edit distance operations of the selected EDA
		 */
		editDistanceEDA.setmMatchWeight(0.0);
		editDistanceEDA.setmDeleteWeight(mDeleteWeight);
		editDistanceEDA.setmInsertWeight(mInsertWeight);
		editDistanceEDA.setmSubstituteWeight(mSubstituteWeight);
		
		/*
		 * starting the training phase by using the updated weights
		 */
		editDistanceEDA.startTraining(cc);
		
		/*
		 * this is the measure to be optimized by PSO; the smaller the value the better
		 * the result. Basically it is the error done by the EDA on the training data set.
		 */
		this.value = 1 - editDistanceEDA.getTrainingAccuracy();
		
		/*
		 * EDA shutdown
		 */
		editDistanceEDA.shutdown();
		
		return this;
		
	}

}
