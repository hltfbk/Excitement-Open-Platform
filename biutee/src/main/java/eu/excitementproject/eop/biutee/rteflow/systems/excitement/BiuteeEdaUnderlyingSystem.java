package eu.excitementproject.eop.biutee.rteflow.systems.excitement;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

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
		script = new ScriptFactory(this.configurationFile, this.teSystemEnvironment.getPluginRegistry()).getDefaultScript();
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
		}
		catch (OperationException e)
		{
			throw new TeEngineMlException("Failed to initialize operation-sciprt.",e);
		}
		finally
		{
			
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
	 */
	public PairProcessor process(PairData pairData) throws TeEngineMlException, AnnotatorException, TreeCoreferenceInformationException, OperationException, ClassifierException, ScriptException, RuleBaseException, MalformedURLException, LemmatizerException
	{
		logger.info("Running document-sublayer: converting PairData to ExtendedPairData...");
		PairDataToExtendedPairDataConverter converter = new PairDataToExtendedPairDataConverter(pairData,this.teSystemEnvironment);
		converter.convert();
		ExtendedPairData extendedPairData = converter.getExtendedPairData();
		logger.info("Converting PairData to ExtendedPairData - done.");
		logger.info("Generating entailment proof...");
		PairProcessor pairProcessor = new PairProcessor(extendedPairData,classifierForSearch,this.getLemmatizer(),script,this.teSystemEnvironment);
		pairProcessor.process();
		logger.info("Generating entailment proof  - done.");
		return pairProcessor;
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
		super.cleanUp();
		if ( (script!=null) && (scriptInitialized) )
		{
			try
			{
				script.cleanUp();
			}
			catch(RuntimeException e)
			{
				// do nothing
				logger.error("Failed to clean up script. However, this is just a clean-up. Continuing.",e);
			}
		}
	}
	
	

	protected OperationsScript<Info, BasicNode> script = null;
	protected LinearClassifier classifierForSearch = null;
	protected Classifier classifierForPredictions = null;
	
	private boolean scriptInitialized = false;
	
	private static final Logger logger = Logger.getLogger(BiuteeEdaUnderlyingSystem.class);
}
