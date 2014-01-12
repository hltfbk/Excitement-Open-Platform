package eu.excitementproject.eop.biutee.rteflow.endtoend;

import static eu.excitementproject.eop.biutee.utilities.BiuteeConstants.LEARNING_MODEL_FILE_POSTFIX;
import static eu.excitementproject.eop.biutee.utilities.BiuteeConstants.LEARNING_MODEL_FILE_PREDICTIONS_INDICATOR;
import static eu.excitementproject.eop.biutee.utilities.BiuteeConstants.LEARNING_MODEL_FILE_PREFIX;
import static eu.excitementproject.eop.biutee.utilities.BiuteeConstants.LEARNING_MODEL_FILE_SEARCH_INDICATOR;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.LinearTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.classifiers.TrainableStorableClassifier;
import eu.excitementproject.eop.biutee.classifiers.io.StorableClassifier;
import eu.excitementproject.eop.biutee.rteflow.systems.FeatureVectorStructure;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;
import eu.excitementproject.eop.biutee.utilities.safemodel.classifiers_io.SafeClassifiersIO;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.ExperimentManager;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * This class is given a (labeled) dataset of T-H pairs, and trains BIUTEE
 * on this dataset. Then it stores the learned model in XML files in the working
 * directory.
 * 
 * @author Asher Stern
 * @since July 15, 2013
 *
 * @param <I>
 * @param <P>
 */
public class Trainer<I extends Instance, P extends Proof>
{
	public Trainer(Dataset<I> dataset,
			List<OperationsScript<Info, BasicNode>> scripts,
			ClassifierGenerator classifierGenerator, Prover<I, P> prover,
			int numberOfThreads, ResultsFactory<I, P> resultsFactory,
			ClassifierTrainer classifierTrainer,
			FeatureVectorStructure featureVectorStructure)
	{
		super();
		this.dataset = dataset;
		this.scripts = scripts;
		this.classifierGenerator = classifierGenerator;
		this.prover = prover;
		this.numberOfThreads = numberOfThreads;
		this.resultsFactory = resultsFactory;
		this.classifierTrainer = classifierTrainer;
		this.featureVectorStructure = featureVectorStructure;
	}
	

	public void train() throws BiuteeException
	{
		try
		{
			verifyInput();
			classifierForSearch = classifierGenerator.createReasonableGuessClassifier();
			iterationNumber = 0;
			Double successRatePreviousIteration = null;
			Double successRateCurrentIteration = null;
			do
			{
				if (samplesLastIteration!=null)
				{
					samplesOfOlderIterations.add(samplesLastIteration);
				}
				iterate();
				storeClassifiers();

				successRatePreviousIteration = successRateCurrentIteration;
				successRateCurrentIteration = resultsLastIteration.getSuccessRate();
				++iterationNumber;

				logIterationResults();
				endOfIterationEntryPoint();
			}
			while (!isMainLoopDone(iterationNumber,successRatePreviousIteration,successRateCurrentIteration));
			logger.info("Training done. Results of last iteration:\n"+resultsLastIteration.print());
		}
		catch (IOException e)
		{
			throw new BiuteeException("IO failure. Please see nested exception.",e);
		}
	}
	
	/**
	 * A function to be optionally overridden by subclasses.
	 * It is called after each iteration is done.
	 * <P>
	 * Actually, there is no need to override this function, unless the user wants
	 * to do something with the results, in addition to storing the learned
	 * model (which is done by this class as it is). 
	 */
	protected void endOfIterationEntryPoint() throws BiuteeException
	{
		
	}
	
	

	
	/**
	 * 
	 * @param iterationNumber iteration number of last iteration (the iteration that
	 * has been completed right now). The counter begins from 1. So, after the first
	 * iteration is done, the iterationNumber is 1.
	 * 
	 * @param previousIterationSuccessRate
	 * @param currentIterationSuccessRate
	 * @return
	 */
	protected boolean isMainLoopDone(int iterationNumber, Double previousIterationSuccessRate, double currentIterationSuccessRate)
	{
		if (iterationNumber<BiuteeConstants.MAX_NUMBER_OF_MAIN_LOOP_ITERATIONS)
		{
			if (BiuteeConstants.MAIN_LOOP_STOPS_WHEN_ACCURACY_CONVERGES)
			{
				if ( (iterationNumber>1) && (previousIterationSuccessRate!=null) )
				{
					if (Math.abs(currentIterationSuccessRate-previousIterationSuccessRate)<=BiuteeConstants.TRAINER_ACCURACY_DIFFERENCE_TO_STOP)
					{
						return true;
					}
				}
			}
		}
		else
		{
			// if iteration number is 3 - then we are done.
			return true;
		}
		return false;
	}
	
	protected void storeClassifiers() throws BiuteeException
	{
		storeClassifier(classifierForSearch,iterationNumber,LEARNING_MODEL_FILE_SEARCH_INDICATOR);
		storeClassifier(classifierForPredictions,iterationNumber,LEARNING_MODEL_FILE_PREDICTIONS_INDICATOR);
	}


	
	private void iterate() throws BiuteeException
	{
		DatasetProcessor<I,P> datasetProcessor = new DatasetProcessor<I,P>(dataset,scripts,classifierForSearch,prover,numberOfThreads);
		datasetProcessor.process();
		List<InstanceAndProof<I, P>> proofs = datasetProcessor.getProofs();
		samplesLastIteration = proofsToLabeledSamples(proofs);
		
		TrainedClassifiers trainedClassifiers = classifierTrainer.train(samplesLastIteration, samplesOfOlderIterations, classifierGenerator);
		classifierForSearch = trainedClassifiers.getClassifierForSearch();
		classifierForPredictions = trainedClassifiers.getClassifierForPredictions();
		logger.info("Classifier for search:\n"+classifierForSearch.descriptionOfTraining());
		logger.info("Classifier for predictions:\n"+classifierForPredictions.descriptionOfTraining());
		
		resultsLastIteration = resultsFactory.createResults(proofs, classifierForPredictions);
		resultsLastIteration.compute();
	}
	
	private void logIterationResults() throws BiuteeException, IOException
	{
		logger.info("Iteration done.\nProofs:");
		Iterator<String> detailsIterator = resultsLastIteration.instanceDetailsIterator();
		while (detailsIterator.hasNext())
		{
			String details = detailsIterator.next();
			logger.info(details);
		}
		logger.info("Current iteration result summary:\n"+resultsLastIteration.print());
		
		
		if (BiuteeConstants.SAVE_SERIALIZED_RESULTS)
		{
			File serFile = new File(BiuteeConstants.RESULTS_SER_FILE_PREFIX+"_"+iterationNumber+BiuteeConstants.RESULTS_SER_FILE_POSTFIX);
			logger.info("Iteration "+iterationNumber+" done."
					+ " Saving results in ser file to "+serFile.getName());
			try(ObjectOutputStream serStream = new ObjectOutputStream(new FileOutputStream(serFile)))
			{
				serStream.writeObject(resultsLastIteration.getProofs());
			}
			ExperimentManager.getInstance().register(serFile);
		}
	}

	
	private Vector<LabeledSample> proofsToLabeledSamples(List<InstanceAndProof<I, P>> proofs) throws BiuteeException
	{
		Vector<LabeledSample> samples = new Vector<>();
		for (InstanceAndProof<I, P> proof : proofs)
		{
			samples.add(new LabeledSample(
					proof.getProof().getFeatureVector(),
					proof.getInstance().getBinaryLabel().booleanValue()
					));
		}
		return samples;
	}
	
	private void verifyInput() throws BiuteeException
	{
		for (I instance : dataset.getListOfInstances())
		{
			if (null==instance.getBinaryLabel()) throw new BiuteeException("Invalid input: at least one T-H pair has no binary label. Perhaps the given dataset is an unannotated test-set?");
		}
	}
	
	
	private void storeClassifier(StorableClassifier classifier, int loopIndex, String searchOrPredictionsIndicator) throws BiuteeException
	{
		try
		{
			String storeClassifierFileName = LEARNING_MODEL_FILE_PREFIX+"_"+searchOrPredictionsIndicator+"_"+loopIndex+LEARNING_MODEL_FILE_POSTFIX;
			File storeClassifierFile = new File(storeClassifierFileName);
			SafeClassifiersIO.store(classifier, featureVectorStructure, storeClassifierFile);
			ExperimentManager.getInstance().register(storeClassifierFile);
		}
		catch (TeEngineMlException e)
		{
			throw new BiuteeException("Failed to stroe classifier.",e);
		}
	}

	

	
	
	
	
	// input
	private final Dataset<I> dataset;
	private final List<OperationsScript<Info, BasicNode>> scripts;
	private final ClassifierGenerator classifierGenerator;
	private final Prover<I, P> prover;
	private final int numberOfThreads;
	private final ResultsFactory<I, P> resultsFactory;
	private final ClassifierTrainer classifierTrainer;
	private final FeatureVectorStructure featureVectorStructure;
	
	// internals
	private int iterationNumber;
	protected LinearTrainableStorableClassifier classifierForSearch;
	protected TrainableStorableClassifier classifierForPredictions;
	protected Results<I, P> resultsLastIteration;
	protected Vector<LabeledSample> samplesLastIteration = null;
	protected LinkedList<Vector<LabeledSample>> samplesOfOlderIterations = new LinkedList<>();

	private static final Logger logger = Logger.getLogger(Trainer.class);
}
