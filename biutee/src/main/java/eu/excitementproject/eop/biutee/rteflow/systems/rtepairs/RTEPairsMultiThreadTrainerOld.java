package eu.excitementproject.eop.biutee.rteflow.systems.rtepairs;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.ClassifierUtils;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.LinearTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.plugin.PluginAdministrationException;
import eu.excitementproject.eop.biutee.rteflow.macro.TextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.systems.RTESystemsUtils;
import eu.excitementproject.eop.biutee.rteflow.systems.SystemInitialization;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.script.ScriptFactory;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames;
import eu.excitementproject.eop.biutee.utilities.LogInitializer;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.ExperimentManager;
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
 * Training the system on the dev-set.
 * 
 * @deprecated Use {@link RTEPairsMultiThreadTrainer}
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
 * 
 * @author Asher Stern
 * @since Feb 18, 2011
 *
 */
@Deprecated
public class RTEPairsMultiThreadTrainerOld extends RTEPairsTrainer
{
	public static final int ATTEMPTS_TO_INITIALIZE_A_SCRIPT = 5;
	public static final int SLEEP_IN_SCRIPT_INIT_ATTEMPT = 1000;
	public static final double ACCURACY_DIFFERENCE_TO_STOP = BiuteeConstants.TRAINER_ACCURACY_DIFFERENCE_TO_STOP;
	
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

			
			RTEPairsMultiThreadTrainerOld trainer = new RTEPairsMultiThreadTrainerOld(configurationFileName);
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
	
	

	public RTEPairsMultiThreadTrainerOld(String configurationFileName) throws ConfigurationFileDuplicateKeyException, FileNotFoundException, ConfigurationException, IOException, ClassNotFoundException, OperationException, TeEngineMlException, TreeCoreferenceInformationException, LemmatizerException
	{
		super(configurationFileName);
	}
	
	@Override
	protected void init() throws ConfigurationFileDuplicateKeyException, MalformedURLException, ConfigurationException, LemmatizerException, TeEngineMlException, IOException, PluginAdministrationException
	{
		super.init();
		
		ConfigurationParams trainParams = configurationFile.getModuleConfiguration(ConfigurationParametersNames.RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME);
		this.numberOfThreads = trainParams.getInt(ConfigurationParametersNames.RTE_ENGINE_NUMBER_OF_THREADS_PARAMETER_NAME);
		
		this.perThreadPairsData = RTESystemsUtils.splitPairsForThreads(this.pairsData, this.numberOfThreads);
		for (int perThreadIndex=0;perThreadIndex<perThreadPairsData.length;++perThreadIndex)
		{
			logger.info("Thread #"+perThreadIndex+" has "+perThreadPairsData[perThreadIndex].size()+" pairs.");
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
		this.stopFlag = new StopFlag();
		this.allPairsResults = new LinkedHashMap<ExtendedPairData, PairProcessResult>();
		
		// Define array with all threads
		Thread[] threads = new Thread[numberOfThreads];
		OneThreadProcessor[] oneThreadProcessors = new OneThreadProcessor[numberOfThreads];
		// Run all the threads
		for (int threadIndex=0;threadIndex<numberOfThreads;threadIndex++)
		{
			OneThreadProcessor processor = new OneThreadProcessor(this.perThreadPairsData[threadIndex]);
			oneThreadProcessors[threadIndex] = processor;
			threads[threadIndex] = new Thread(processor);
			threads[threadIndex].start();
		}
		// Wait for all the threads. Join them when they are done, and
		// read their information
		//
		// If any thread has thrown an exception, then it is assigned here to
		// "threadException", and thrown upwards.
		TeEngineMlException threadException = null;
		for (int threadIndex=0;threadIndex<numberOfThreads;threadIndex++) 
		{
			threads[threadIndex].join();
			if (null==threadException)
				threadException = oneThreadProcessors[threadIndex].getThreadException();
			if (null==threadException)
			{
				logger.info("Adding samples of thread: "+threadIndex+". Adding: "+oneThreadProcessors[threadIndex].getResults().size()+" samples.");
				allPairsResults.putAll(oneThreadProcessors[threadIndex].getResults());
			}
			else
			{
				logger.error("threadException is not null:\nThe program is about to end with an appropriate error message.");
			}
		}
		if (threadException!=null)
			throw threadException;
		
		// Create a vector of samples
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

		RTESystemsUtils.printIterationSummary(allPairsResults, samples, classifierForPredictions);
		
		RTESystemsUtils.savePairResultsInSerFile(allPairsResults,mainLoopIterationIndex);
		
		mainLoopIterationIndex++;
		return ClassifierUtils.accuracyOf(classifierForPredictions, samples);
	}
	
	
	
	protected class OneThreadProcessor implements Runnable
	{
		public OneThreadProcessor(List<ExtendedPairData> threadPairsData)
		{
			super();
			this.threadPairsData = threadPairsData;
		}
		
		public void run()
		{
			try
			{
				processAllPairs();
			}
			catch (Throwable e)
			{
				RTEPairsMultiThreadTrainerOld.this.stopFlag.stop();
				this.threadException = new TeEngineMlException("Failure in processing pairs. See nested exception",e);
				
				// RTTI is legal for exceptions in cases like this, since it imitates throws
				try{if (e instanceof TeEngineMlException){this.threadException = (TeEngineMlException)e;}}
				catch(Throwable t){}
				ExceptionUtil.logException(this.threadException, logger);
			}
			
		}
		
		public LinkedHashMap<ExtendedPairData, PairProcessResult> getResults()
		{
			return results;
		}

		public TeEngineMlException getThreadException()
		{
			return threadException;
		}
		
		private void processAllPairs() throws TeEngineMlException, OperationException, ClassifierException, ScriptException, RuleBaseException, AnnotatorException
		{
			logger.info("Creating and initializing Operation-Script for current thread.");
			script = createAndInitializeScript();
			try
			{
				if (ruleBasesNames==null)
					ruleBasesNames = script.getRuleBasesNames();
				
				if (! completeInitializationWithScriptDone) // double check for efficiency
				{
					synchronized (lockerCompleteInitialization)
					{
						if (! completeInitializationWithScriptDone) // double check for efficiency
						{
							completeInitializationWithScript(script);
							completeInitializationWithScriptDone=true;
						}
					}
				}

				logger.info("Starting to process all pairs assigned for current thread.");
				// Create a processor that makes the processing on a list of pairs.
				// It is "list of pairs --- processor", i.e. processor for list of pairs.
				ListOfPairsProcessor pairsTrainer = new ListOfPairsProcessor(RTEPairsMultiThreadTrainerOld.this.stopFlag,threadPairsData, script, classifierForSearch, lemmatizer, teSystemEnvironment);
				pairsTrainer.processList();
				this.results = pairsTrainer.getResults();
			}
			finally
			{
				script.cleanUp();
			}
		}
		
		private OperationsScript<Info, BasicNode> createAndInitializeScript() throws OperationException
		{
			OperationsScript<Info, BasicNode> ret = null;
			boolean initialized = false;
			int attempt = 0;
			while ( (attempt<ATTEMPTS_TO_INITIALIZE_A_SCRIPT) && (!initialized) )
			{
				try
				{
					ret = new ScriptFactory(configurationFile,teSystemEnvironment.getPluginRegistry()).getDefaultScript();
					ret.init();
					initialized = true;
				}
				catch(OperationException e)
				{
					if (attempt<ATTEMPTS_TO_INITIALIZE_A_SCRIPT)
					{
						try
						{
							logger.error("Could not initialized Operation-Script, but the program will not stop but will try again. The error is:",e);
							logger.info("Sleeping for "+SLEEP_IN_SCRIPT_INIT_ATTEMPT+" MS...");
							Thread.sleep(SLEEP_IN_SCRIPT_INIT_ATTEMPT);
							System.gc();
							logger.info("Wake up! Will try to initialize the script again.");
						}
						catch(InterruptedException intex)
						{
							throw new OperationException("Could not make a thread sleep.",intex);
						}
					}
					else
					{
						throw new OperationException("Failed to initialize script for "+ATTEMPTS_TO_INITIALIZE_A_SCRIPT+" attempts. See nested exception for last init failure.",e);
					}
				}
				
				++attempt;
			}
			if (!initialized) // Note that this code should never be executed
			{
				throw new OperationException("Failed to initialize Operation-Script, but a detailed error information is unavailable due to a bug in the code.");
			}
			return ret;
		}
		
		private OperationsScript<Info, BasicNode> script = null;
		private List<ExtendedPairData> threadPairsData;
		private LinkedHashMap<ExtendedPairData, PairProcessResult> results;
		private TeEngineMlException threadException = null;
	}
	

	
	protected int numberOfThreads;
	
	protected List<ExtendedPairData>[] perThreadPairsData;
	protected LinkedHashSet<String> ruleBasesNames = null;
	private Object lockerCompleteInitialization = new Object();
	private boolean completeInitializationWithScriptDone = false;
	private LinkedHashMap<ExtendedPairData, PairProcessResult> allPairsResults;
	private StopFlag stopFlag;
	
	private static final Logger logger = Logger.getLogger(RTEPairsMultiThreadTrainerOld.class);


}
