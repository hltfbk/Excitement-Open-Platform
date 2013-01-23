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
 * 
 * @author Asher Stern
 * @since Jan 23, 2013
 *
 */
public class BiuteeEdaUnderlyingSystem extends SystemInitialization
{
	public BiuteeEdaUnderlyingSystem(String configurationFileName)
	{
		super(configurationFileName, ConfigurationParametersNames.RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME);
	}
	
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
	

	public Classifier getClassifierForPredictions()
	{
		return classifierForPredictions;
	}

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
