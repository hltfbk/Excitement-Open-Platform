package eu.excitementproject.eop.biutee.rteflow.systems.rtepairs;

import static eu.excitementproject.eop.biutee.utilities.BiuteeConstants.LEARNING_MODEL_FILE_POSTFIX;
import static eu.excitementproject.eop.biutee.utilities.BiuteeConstants.LEARNING_MODEL_FILE_PREDICTIONS_INDICATOR;
import static eu.excitementproject.eop.biutee.utilities.BiuteeConstants.LEARNING_MODEL_FILE_PREFIX;
import static eu.excitementproject.eop.biutee.utilities.BiuteeConstants.LEARNING_MODEL_FILE_SEARCH_INDICATOR;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.ClassifierUtils;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.LinearTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.classifiers.io.StorableClassifier;
import eu.excitementproject.eop.biutee.plugin.PluginAdministrationException;
import eu.excitementproject.eop.biutee.rteflow.macro.TextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeAndFeatureVector;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapException;
import eu.excitementproject.eop.biutee.rteflow.systems.RTESystemsUtils;
import eu.excitementproject.eop.biutee.rteflow.systems.SystemInitialization;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.script.ScriptFactory;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames;
import eu.excitementproject.eop.biutee.utilities.LogInitializer;
import eu.excitementproject.eop.biutee.utilities.safemodel.classifiers_io.SafeClassifiersIO;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.ExperimentManager;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFileDuplicateKeyException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.lap.biu.lemmatizer.LemmatizerException;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.utilities.StopFlag;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * Training the system on the dev-set.
 * 
 * The results of the trainer are "ser" files: "labeled_samplesX.ser" where X
 * is an integer number, starting from 0. Each of those files contain a vector
 * of {@link LabeledSample}s. Using that vector, a classifier (a learning algorithm)
 * can learn how to classify a test sample.
 * 
 * One of the "labeled_samplesX.ser" files should be used for the test, and one
 * of them (possibly the same file) for the search in test.
 * 
 * <B>Note:</B> The real entailment work (the macro flow) starts in {@link TextTreesProcessor} and its implementations.
 * If you want to make porting of the system to another type of data-set or another mode
 * of work, consider starting your work by using {@link TextTreesProcessor} and {@link SystemInitialization}.
 * Please refer to <B>Developer Guide</B> for more information.
 * 
 * @see TextTreesProcessor
 * 
 * @author Asher Stern
 * @since 23 - July - 2012
 *
 */
public class RTEPairsMultiThreadTrainer extends RTEPairsTrainer
{
	
	/**
	 * The entry point of the trainer. Use configuration file name as a
	 * command line argument.
	 * 
	 * @param args should be configuration file name.
	 */
	public static void main(String[] args)
	{
		try
		{
			if (args.length<1)throw new TeEngineMlException("No arguments. Enter configuration file name as argument.");
			
			String configurationFileName = args[0];
			new LogInitializer(configurationFileName).init();
			
			ExperimentManager.getInstance().start();
			ExperimentManager.getInstance().setConfigurationFile(configurationFileName);

			logger.info("RTEPairsMultiThreadTrainer");

			
			RTEPairsMultiThreadTrainer trainer = new RTEPairsMultiThreadTrainer(configurationFileName);
			try
			{
				trainer.init();
				trainer.train();
			}
			finally
			{
				trainer.cleanUp();
			}
			
			boolean experimentManagedSucceeded = ExperimentManager.getInstance().save();
			logger.info("ExperimentManager save "+(experimentManagedSucceeded?"succeeded":"failed")+".");
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
			ExceptionUtil.logException(e, logger);
		}
	}
	
	

	public RTEPairsMultiThreadTrainer(String configurationFileName) throws ConfigurationFileDuplicateKeyException, FileNotFoundException, ConfigurationException, IOException, ClassNotFoundException, OperationException, TeEngineMlException, TreeCoreferenceInformationException, LemmatizerException
	{
		super(configurationFileName);
	}
	
	@Override
	public void init() throws ConfigurationFileDuplicateKeyException, MalformedURLException, ConfigurationException, LemmatizerException, TeEngineMlException, IOException, PluginAdministrationException
	{
		super.init();
		
		ConfigurationParams trainParams = configurationFile.getModuleConfiguration(ConfigurationParametersNames.RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME);
		this.numberOfThreads = trainParams.getInt(ConfigurationParametersNames.RTE_ENGINE_NUMBER_OF_THREADS_PARAMETER_NAME);
		
		// scriptQueue holds {OperationScript}s, one for each thread.
		this.scriptQueue = new ArrayBlockingQueue<OperationsScript<Info,BasicNode>>(numberOfThreads);
		logger.info("Initializing Operation-Scripts...");
		
		// Initialize OperationsScripts
		ExecutorService executorInitScripts = Executors.newFixedThreadPool(numberOfThreads);
		try
		{
			List<ScriptInitializerIntoQueue> scriptInitCallables = new ArrayList<ScriptInitializerIntoQueue>(numberOfThreads);
			for (int i =0;i<numberOfThreads;++i)
			{
				scriptInitCallables.add(new ScriptInitializerIntoQueue());
			}
			try
			{
				List<Future<Boolean>> futures = executorInitScripts.invokeAll(scriptInitCallables);

				// Make sure non of them has thrown an exception
				for (Future<?> future : futures)
				{
					future.get();
				}
				
				// Complete the initialization
				logger.info("Completing the initialization using a script...");
				OperationsScript<Info, BasicNode> oneOfTheScripts = scriptQueue.take();
				completeInitializationWithScript(oneOfTheScripts);
				RTEPairsMultiThreadTrainer.this.ruleBasesNames = oneOfTheScripts.getRuleBasesNames();
				scriptQueue.put(oneOfTheScripts);
				logger.info("Complete initialization using a sciprt - done.");

			}
			catch(InterruptedException e)
			{
				throw new TeEngineMlException("Failed to init threads",e);
			}
			catch (ExecutionException e)
			{
				throw new TeEngineMlException("Failed to init threads",e);
			}
		}
		finally
		{
			executorInitScripts.shutdown();
		}
		
	}
	

	
	@Override
	public void cleanUp()
	{
		super.cleanUp();
		if (this.scriptQueue!=null)
		{
			try
			{
				logger.info("CleanUp all Operation-Scripts...");
				for (OperationsScript<?, ?> script : this.scriptQueue)
				{
					script.cleanUp();
				}
				logger.info("CleanUp all Operation-Scripts - done.");
			}
			catch(Exception e)
			{
				// stop this exception. It should not stop the execution of the program, since it is only a clean up.
				logger.error("CleanUp of OperationsScript failed. Program continues.",e);
			}
		}
	}
	

	
	@Override
	protected LinearTrainableStorableClassifier newReasonableGuessClassifier()
			throws ClassifierException, OperationException, TeEngineMlException
	{
		//return new SynchronizedClassifier(RTEPairsTrainerUtils.reasonableGuessClassifier(configurationFile));
		//return RTESystemsUtils.reasonableGuessClassifier(configurationFile,teSystemEnvironment.getPluginRegistry());
		
		return RTESystemsUtils.reasonableGuessClassifier(teSystemEnvironment.getFeatureVectorStructureOrganizer());
	}




	
	
	/**
	 * This method updates the classifier.
	 * Returns the accuracy on training data.
	 * 
	 * Each "iteration" is the whole process on the whole data set. In each
	 * iteration the system finds the "cheapest" proof for each T-H pair, and
	 * constructs a feature vector for those proofs. Then, the classifier (the
	 * learning algorithm) is trained on those feature vectors. Thus, this
	 * method updates the classifier, and returns the accuracy of the trained
	 * classifier on the data set it was trained on.
	 * 
	 * @throws ClassifierException 
	 * @throws OperationException 
	 * @throws TeEngineMlException 
	 * @throws ScriptException 
	 * @throws RuleBaseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws InterruptedException 
	 * @throws PluginAdministrationException 
	 */
	protected double oneIteration() throws InterruptedException, TeEngineMlException, IOException, ClassifierException, PluginAdministrationException
	{
		// stopFlag is used to stop all threads in case of a failure in one thread.
		this.stopFlag = new StopFlag();
		
		// allPairsResults is a map that holds the results for each pair in the data-set.
		// Each result is basically the proof that was found for that pair, along with other information.
		this.allPairsResults = new LinkedHashMap<ExtendedPairData, PairProcessResult>();

		// The executor will run all the threads.
		// Each thread will sequentially (iteratively) get a pair and will find a proof for that pair.
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
		List<SinglePairCallable> allPairsCallables = new ArrayList<SinglePairCallable>(pairsData.size());
		for (ExtendedPairData pair : pairsData)
		{
			allPairsCallables.add(new SinglePairCallable(pair));
		}
		logger.info("Processing all pairs...");
		executor.invokeAll(allPairsCallables);
		logger.info("Processing all pairs done. Shutting down executor...");
		executor.shutdown();
		logger.info("Executor is shut down.");
		
		
		if (this.exceptionThrownByOnePairProcessor!=null)
		{
			throw this.exceptionThrownByOnePairProcessor;
		}
		
		// Make the map ordered by the original order of the pairs (the order of the pairs as they are in the data-set).
		allPairsResults = reorderMap(allPairsResults, pairsData);
		
		// Create a vector of samples (used to train the classifier)
		Vector<LabeledSample> samples = new Vector<LabeledSample>(this.pairsData.size());
		for (ExtendedPairData pair : allPairsResults.keySet())
		{
			samples.add(allPairsResults.get(pair).getLabeledSample());
		}
		
		// Save the samples, create new classifiers, based on the new samples
		// print iteration summary, and return the current accuracy
		logger.info("Iteration done.");
		RTESystemsUtils.saveSamplesInSerFile(samples, mainLoopIterationIndex, pathToStoreLabledSamples,teSystemEnvironment.getFeatureVectorStructureOrganizer());

		logger.info("Number of samples: "+samples.size());
		logger.info("training classifierForSearch...");
		this.classifierForSearch = RTESystemsUtils.createClassifierForSearch(teSystemEnvironment.getFeatureVectorStructureOrganizer(), samples);
		logger.info("training classifierForPredictions...");
		classifierForPredictions = RTESystemsUtils.createClassifierForPredictions(teSystemEnvironment.getFeatureVectorStructureOrganizer(), samples);

		
		storeClassifier(classifierForSearch,mainLoopIterationIndex,LEARNING_MODEL_FILE_SEARCH_INDICATOR);
		storeClassifier(classifierForPredictions,mainLoopIterationIndex,LEARNING_MODEL_FILE_PREDICTIONS_INDICATOR);

		
		RTESystemsUtils.printIterationSummary(allPairsResults, samples, classifierForPredictions);
		
		RTESystemsUtils.savePairResultsInSerFile(allPairsResults,mainLoopIterationIndex);
		
		mainLoopIterationIndex++;
		return ClassifierUtils.accuracyOf(classifierForPredictions, samples);
	}
	
	private void storeClassifier(StorableClassifier classifier, int loopIndex, String searchOrPredictionsIndicator) throws TeEngineMlException
	{
		String storeClassifierFileName = LEARNING_MODEL_FILE_PREFIX+"_"+searchOrPredictionsIndicator+"_"+loopIndex+LEARNING_MODEL_FILE_POSTFIX;
		File storeClassifierForSearchFile;
		if (pathToStoreLabledSamples!=null)
		{
			storeClassifierForSearchFile = new File(new File(pathToStoreLabledSamples),storeClassifierFileName);	
		}
		else
		{
			storeClassifierForSearchFile = new File(storeClassifierFileName);
		}
		SafeClassifiersIO.store(classifier, teSystemEnvironment.getFeatureVectorStructureOrganizer(), storeClassifierForSearchFile);
		ExperimentManager.getInstance().register(storeClassifierForSearchFile);

	}
	
	
	
	private OperationsScript<Info, BasicNode> createAndInitializeScript() throws OperationException, GapException
	{
		OperationsScript<Info, BasicNode> ret = null;
		ret = new ScriptFactory(configurationFile,teSystemEnvironment.getPluginRegistry(),teSystemEnvironment).getDefaultScript();
		ret.init();
		return ret;
	}
	
	/**
	 * Returns a map in which the keys are ordered as they are ordered in the given list.
	 * @param map A Map in which the keys might be ordered in a different order than they are in the given list
	 * @param listKeys A list of the keys, which is ordered as we would like them to be.
	 * @return A new map, ordered like the list.
	 * @throws TeEngineMlException
	 */
	private static <K,V> LinkedHashMap<K, V> reorderMap(Map<K, V> map, List<K> listKeys) throws TeEngineMlException
	{
		if (listKeys.size()!=map.keySet().size()) throw new TeEngineMlException("reorderMap: incorrect input - incompatible sizes.");
		LinkedHashMap<K,V> ret = new LinkedHashMap<K, V>();
		for (K key : listKeys)
		{
			if (!map.containsKey(key)) throw new TeEngineMlException("reorderMap key does not exist: "+key.toString());
			ret.put(key, map.get(key));
		}
		return ret;
	}
	
	
	private class ScriptInitializerIntoQueue implements Callable<Boolean>
	{
		@Override
		public Boolean call() throws Exception
		{
			logger.info("Initializing an OperationsScript...");
			OperationsScript<Info, BasicNode> script;
			try
			{
				script = createAndInitializeScript();
				scriptQueue.put(script);
				
				logger.info("Initialization of OperationsScript is done.");
			}
			catch (OperationException e)
			{
				throw new TeEngineMlException("Cannot initialize Operation-Script",e);
			}
			catch (InterruptedException e)
			{
				throw new TeEngineMlException("Cannot put Operation-Script in queue",e);
			}
			
			return true;
		}
	}
	
	/**
	 * Used by the {@link ExecutorService} that processes all the pairs.
	 * @author Asher Stern
	 * @since Oct 18, 2012
	 *
	 */
	protected class SinglePairCallable implements Callable<TeEngineMlException>
	{
		public SinglePairCallable(ExtendedPairData pairData)
		{
			super();
			this.pairData = pairData;
		}

		/**
		 * Processes the given pair, i.e., finds a proof for the given pair.
		 */
		@Override
		public TeEngineMlException call() throws Exception
		{
			TeEngineMlException ret = null;
			if (stopFlag.isStop())
			{}
			else
			{
				boolean succeeded = false;
				try
				{
					// Process the pair, and store the result in the map of allPairsResults
					PairProcessResult result = process();
					synchronized(allPairsResults)
					{
						allPairsResults.put(pairData, result);
					}
					succeeded = true;
				}
				catch(Exception e)
				{
					try{ExceptionUtil.logException(e, logger);}catch(Exception x){}
					ret = new TeEngineMlException("Process failed",e);
					exceptionThrownByOnePairProcessor = ret;
				}
				finally
				{
					if (!succeeded)
					{
						try
						{
							try{logger.error("Degrade gracefully...");}catch(Throwable xx){}
							stopFlag.stop();
							try{logger.error("Soon all threads will stop.");}catch(Throwable xx){}
						}
						catch(Throwable x)
						{
							try{logger.error("Cannot even degrade gracefully.");}catch(Throwable xx){}
						}
					}
				} // end of finally
			}
			return ret;			
		}
		
		protected PairProcessResult process() throws InterruptedException, TeEngineMlException, OperationException, ClassifierException, AnnotatorException, ScriptException, RuleBaseException
		{
			logger.info("Processing pair: "+((pairData.getDatasetName()!=null)?pairData.getDatasetName()+": ":"") + pairData.getPair().getId());
			OperationsScript<Info, BasicNode> script = scriptQueue.take();
			try
			{
				PairProcessor processor = new PairProcessor(pairData, classifierForSearch, lemmatizer, script, RTEPairsMultiThreadTrainer.this. teSystemEnvironment);
				processor.process();
				TreeAndFeatureVector treeAndFeatureVector = processor.getBestTree();
				LabeledSample sample = new LabeledSample(treeAndFeatureVector.getFeatureVector(), pairData.getPair().getBooleanClassificationType().booleanValue());
				PairProcessResult result;
				if (BiuteeConstants.PRINT_TIME_STATISTICS)
				{
					result = new PairProcessResult(treeAndFeatureVector.getTree(), treeAndFeatureVector.getFeatureVector(), processor.getBestTreeSentence(), pairData, processor.getBestTreeHistory(), sample, processor.getCpuTime(),processor.getWorldClockTime());
				}
				else
				{
					result = new PairProcessResult(treeAndFeatureVector.getTree(), treeAndFeatureVector.getFeatureVector(), processor.getBestTreeSentence(), pairData, processor.getBestTreeHistory(), sample);
				}
				logger.info("Pair processing done. Memory used: "+Utils.stringMemoryUsedInMB());
				return result;
			}
			finally
			{
				scriptQueue.put(script);
			}
		}
		
		protected ExtendedPairData pairData;
	}


	
	protected int numberOfThreads;
	
	protected BlockingQueue<OperationsScript<Info, BasicNode>> scriptQueue;
	protected LinkedHashSet<String> ruleBasesNames = null;
	private LinkedHashMap<ExtendedPairData, PairProcessResult> allPairsResults;
	private TeEngineMlException exceptionThrownByOnePairProcessor = null;
	private StopFlag stopFlag;

	
	
	private static final Logger logger = Logger.getLogger(RTEPairsMultiThreadTrainer.class);
}
