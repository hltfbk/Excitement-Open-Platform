package eu.excitementproject.eop.biutee.rteflow.systems.excitement;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.EmptyStackException;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.Classifier;
import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.plugin.PluginAdministrationException;
import eu.excitementproject.eop.biutee.rteflow.systems.SystemInitialization;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.ExtendedPairData;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.PairData;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.PairDataToExtendedPairDataConverter;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.PairProcessor;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.PairResult;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.script.ScriptFactory;
import eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames;
import eu.excitementproject.eop.biutee.utilities.safemodel.classifiers_io.SafeClassifiersIO;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFileDuplicateKeyException;
import eu.excitementproject.eop.lap.biu.lemmatizer.LemmatizerException;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * The actual BIUTEE system used by {@link BiuteeEDA}.
 * This system is used by the methods
 * {@link BiuteeEDA#initialize(eu.excitementproject.eop.common.configuration.CommonConfig)},
 * {@link BiuteeEDA#process(org.apache.uima.jcas.JCas)}
 * and
 * {@link BiuteeEDA#shutdown()}.
 * <P>
 * 
 * Note that the method
 * {@link BiuteeEDA#startTraining(eu.excitementproject.eop.common.configuration.CommonConfig)}
 * does not use this class.
 * 
 * @author Asher Stern
 * @since Jan 23, 2013
 *
 */
public class BiuteeEdaUnderlyingSystem extends SystemInitialization
{
	public static final long WAIT_TERMINATION_THREAD_POOL_MINUTES = 10;
	/**
	 * Constructor with the configuration-file-name. This file is BIU
	 * configuration file, not Excitement configuration file.
	 * 
	 * @see BiuteeEdaUtilities#convertExcitementConfigurationFileToBiuConfigurationFile(File, File).
	 * 
	 * @param configurationFileName The configuration file name. This file is
	 * BIU configuration file, not Excitement configuration file.
	 */
	public BiuteeEdaUnderlyingSystem(String configurationFileName)
	{
		super(configurationFileName, ConfigurationParametersNames.RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME);
	}
	
	/**
	 * Initialization of the system.
	 */
	@Override
	public void init() throws ConfigurationFileDuplicateKeyException, ConfigurationException, MalformedURLException, LemmatizerException, TeEngineMlException, IOException, PluginAdministrationException
	{
		super.init();
		script = new ScriptFactory(this.configurationFile, this.teSystemEnvironment.getPluginRegistry(),this.teSystemEnvironment).getDefaultScript();
		try
		{
			logger.info("Initializing operation sciprt...");
			script.init();
			logger.info("Initializing operation sciprt - done.");
			scriptInitialized = true;
			
			this.completeInitializationWithScript(script);
			
			logger.info("Loading learning models and constructing classifiers...");
			File modelForSearch = this.configurationParams.getFile(ConfigurationParametersNames.RTE_TEST_SEARCH_MODEL);
			classifierForSearch = SafeClassifiersIO.loadLinearClassifier(this.teSystemEnvironment.getFeatureVectorStructureOrganizer(), modelForSearch);
			
			File modelForPredictions = this.configurationParams.getFile(ConfigurationParametersNames.RTE_TEST_PREDICTIONS_MODEL);
			classifierForPredictions = SafeClassifiersIO.load(this.teSystemEnvironment.getFeatureVectorStructureOrganizer(), modelForPredictions);
			logger.info("Loading learning models and constructing classifiers - done.");
			
			
			initScriptsAndThreadPool();
			initDone = true;
		}
		catch (OperationException e)
		{
			throw new TeEngineMlException("Failed to initialize operation-sciprt.",e);
		}
	}
	
	/**
	 * Processes the given {@link PairData}, and returns a {@link PairProcessor} object.
	 * The returned {@link PairProcessor} state is with its state after the method
	 * {@link PairProcessor#process()} has been called.
	 * 
	 * @param pairData
	 * @return
	 * @throws TeEngineMlException
	 * @throws AnnotatorException
	 * @throws TreeCoreferenceInformationException
	 * @throws OperationException
	 * @throws ClassifierException
	 * @throws ScriptException
	 * @throws RuleBaseException
	 * @throws MalformedURLException
	 * @throws LemmatizerException
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public PairResult process(PairData pairData) throws TeEngineMlException, AnnotatorException, TreeCoreferenceInformationException, OperationException, ClassifierException, ScriptException, RuleBaseException, MalformedURLException, LemmatizerException, InterruptedException, ExecutionException
	{
		if (!initDone) throw new TeEngineMlException("Initialization has not been completed properly. The method process can be called only after a successful initialization.");
		if (cleanUpHasBeenCalled) throw new TeEngineMlException("Cannot process after calling cleanUp.");
		return threadPool.submit(new Processor(pairData)).get();
	}
	

	/**
	 * The classifier used to decide entailment. To get a result for a given
	 * T-H pair, that has been processed by the {@link #process(PairData)} method,
	 * one has to call {@link Classifier#classify(java.util.Map)} on the feature
	 * vector of {@link PairProcessor#getBestTree()}.
	 * 
	 * @return
	 */
	public Classifier getClassifierForPredictions()
	{
		return classifierForPredictions;
	}

	/**
	 * Clean up of the system.
	 */
	@Override
	public void cleanUp()
	{
		cleanUpHasBeenCalled = true;
		super.cleanUp();
		logger.info("Shuting down thread pool.");
		if (threadPool != null) {
			threadPool.shutdown();
		}
		logger.info("Termination of scripts ...");
		if (scriptStack != null) {
			synchronized (scriptStack)
			{
				while (!scriptStack.empty())
				{
					scriptStack.pop().cleanUp();
				}
			}
		}
		logger.info("Termination of scripts - done.");
	}
	
	
	private void initScriptsAndThreadPool() throws ConfigurationException
	{
		int numberOfThreads = this.configurationParams.getInt(ConfigurationParametersNames.RTE_ENGINE_NUMBER_OF_THREADS_PARAMETER_NAME);
		scriptStack = new SynchronizedStack<OperationsScriptGetter>();
		for (int index=0;index<(numberOfThreads-1);++index)
		{
			scriptStack.push(new OperationsScriptGetter(new ScriptFactory(this.configurationFile, this.teSystemEnvironment.getPluginRegistry(),this.teSystemEnvironment)));
		}
		scriptStack.push(new OperationsScriptGetter(this.script));
		
		threadPool = Executors.newFixedThreadPool(numberOfThreads);
	}
	
	
	
	private class Processor implements Callable<PairResult> 
	{
		public Processor(PairData pairData)
		{
			super();
			this.pairData = pairData;
		}

		@Override
		public PairResult call() throws TeEngineMlException, AnnotatorException, TreeCoreferenceInformationException, OperationException, ClassifierException, ScriptException, RuleBaseException, MalformedURLException, LemmatizerException
		{
			OperationsScriptGetter scriptGetter = null;
			OperationsScript<Info, BasicNode> scriptForThread = null;
			try
			{
				scriptGetter = BiuteeEdaUnderlyingSystem.this.scriptStack.pop();
				scriptForThread = scriptGetter.getScript();
				logger.info("Running document-sublayer: converting PairData to ExtendedPairData...");
				PairDataToExtendedPairDataConverter converter = new PairDataToExtendedPairDataConverter(pairData,BiuteeEdaUnderlyingSystem.this.teSystemEnvironment);
				converter.convert();
				ExtendedPairData extendedPairData = converter.getExtendedPairData();
				logger.info("Converting PairData to ExtendedPairData - done.");
				logger.info("Generating entailment proof...");
				PairProcessor pairProcessor = new PairProcessor(extendedPairData,classifierForSearch,BiuteeEdaUnderlyingSystem.this.getLemmatizer(),scriptForThread,BiuteeEdaUnderlyingSystem.this.teSystemEnvironment);
				pairProcessor.process();
				logger.info("Generating entailment proof  - done.");
				PairResult ret = new PairResult(pairProcessor.getBestTree(),pairProcessor.getBestTreeSentence(),pairProcessor.getBestTreeHistory());
				return ret;
			}
			catch(EmptyStackException e)
			{
				throw new TeEngineMlException("BUG",e);
			}
			finally
			{
				if (scriptGetter!=null)
				{
					synchronized(scriptStack)
					{
						if (!cleanUpHasBeenCalled)
							scriptStack.push(scriptGetter);
						else
						{
							if (scriptForThread!=null)
							{
								scriptForThread.cleanUp();
							}
						}
					} // end of synchronized(scriptStack)
				}
			} // end of finally
		} // end of method call()

		private PairData pairData;
	}
	

	/**
	 * A subclass of java.util.Stack, which wraps the stack methods
	 * by synchronizing them.<P>
	 * This had to be done since it is not documented whether java.util.Stack
	 * is thread safe or not. 
	 * @author Asher Stern
	 * @since Jan 28, 2013
	 *
	 * @param <T>
	 */
	private static class SynchronizedStack<T> extends Stack<T>
	{
		private static final long serialVersionUID = -2312238022756467211L;
		@Override synchronized public boolean empty(){return super.empty();}
		@Override synchronized public T peek(){return super.peek();}
		@Override synchronized public T pop(){return super.pop();}
		@Override synchronized public T push(T item){return super.push(item);}
		@Override synchronized public int search(Object o){return super.search(o);}
	}
	

	protected SynchronizedStack<OperationsScriptGetter> scriptStack;
	protected ExecutorService threadPool;
	protected OperationsScript<Info, BasicNode> script = null;
	protected LinearClassifier classifierForSearch = null;
	protected Classifier classifierForPredictions = null;
	
	@SuppressWarnings("unused")
	private boolean scriptInitialized = false;
	private boolean initDone = false;
	private boolean cleanUpHasBeenCalled = false;
	
	private static final Logger logger = Logger.getLogger(BiuteeEdaUnderlyingSystem.class);
}
