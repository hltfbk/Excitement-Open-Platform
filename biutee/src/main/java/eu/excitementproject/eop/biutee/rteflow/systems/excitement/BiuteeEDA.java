package eu.excitementproject.eop.biutee.rteflow.systems.excitement;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.plugin.PluginAdministrationException;
import eu.excitementproject.eop.biutee.rteflow.systems.excitement.ExcitementToBiuConfigurationFileConverter.ExcitementToBiuConfigurationFileConverterException;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.PairData;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.PairResult;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.RTEPairsMultiThreadTrainer;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.utilities.SystemInformationLog;
import eu.excitementproject.eop.common.EDABasic;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.TEDecision;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolutionException;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lap.biu.lemmatizer.LemmatizerException;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitterException;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * BIUTEE implementation of {@link EDABasic}.
 * 
 * @author Asher Stern
 * @since Jan 23, 2013
 *
 */
public class BiuteeEDA implements EDABasic<TEDecision>
{
	public static final String TEMPORARY_CONFIGURATION_FILE_PREFIX = "biutee_configuration_file";
	public static final String TEMPORARY_CONFIGURATION_FILE_SUFFIX = ".xml";

	/*
	 * (non-Javadoc)
	 * @see eu.excitementproject.eop.common.EDABasic#initialize(eu.excitementproject.eop.common.configuration.CommonConfig)
	 */
	@Override
	public void initialize(CommonConfig config) throws ConfigurationException, EDAException, ComponentException
	{
		logger.info(BiuteeEDA.class.getSimpleName()+": initialization for interactive textual entailment recognition.");
		if (trainingMode!=null)
		{
			if (false == trainingMode.booleanValue()) throw new EDAException("The method initialize must not run twice for the same EDA object.");
			else throw new EDAException("Method initialize must not be called if startTraining was called.");
		}
		trainingMode = false;
		File biuConfigurationFile = null;
		try
		{
			biuConfigurationFile = File.createTempFile(TEMPORARY_CONFIGURATION_FILE_PREFIX, TEMPORARY_CONFIGURATION_FILE_SUFFIX);
			BiuteeEdaUtilities.convertExcitementConfigurationFileToBiuConfigurationFile(new File(config.getConfigurationFileName()), biuConfigurationFile);
			logger.info("Log file has been converted to BIU log file, and temporarily stored in "+biuConfigurationFile.getPath());
			new SystemInformationLog(biuConfigurationFile.getPath()).log();
			logger.info("Initializing BIUTEE underlying system...");
			underlyingSystem = new BiuteeEdaUnderlyingSystem(biuConfigurationFile.getPath());
			underlyingSystem.init();
			logger.info("Initializing BIUTEE underlying system - done.");
		}
		catch (IOException | TeEngineMlException | PluginAdministrationException | eu.excitementproject.eop.common.utilities.configuration.ConfigurationException | LemmatizerException | ExcitementToBiuConfigurationFileConverterException e)
		{
			throw new EDAException("Initialization failure. See nested exception.",e);
		}
		finally
		{
			if (biuConfigurationFile!=null){ if (biuConfigurationFile.exists())
			{
				try{biuConfigurationFile.delete();}catch(RuntimeException e){}
			}}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see eu.excitementproject.eop.common.EDABasic#process(org.apache.uima.jcas.JCas)
	 */
	@Override
	public TEDecision process(JCas aCas) throws EDAException, ComponentException
	{
		if (hasBeenShutDown) throw new EDAException("After calling the method shutdown(), no method should be called.");
		if (null == underlyingSystem) throw new EDAException("Method initialize must be called before calling method process.");
		try
		{
			logger.info("Building PairData from the given JCas...");
			String pairId = BiuteeEdaUtilities.getPairIdFromJCas(aCas);
			PairData pairData = BiuteeEdaUtilities.convertJCasToPairData(aCas);
			logger.info("Building PairData from the given JCas - done.");
			logger.info("Processing T-H pair...");
			PairResult pairResult = underlyingSystem.process(pairData);
			logger.info("Processing T-H pair - done.");
			return BiuteeEdaUtilities.createDecisionFromPairResult(pairId,pairResult,underlyingSystem.getClassifierForPredictions());
		}
		catch (TeEngineMlException | AnnotatorException | OperationException | ClassifierException | MalformedURLException | TreeCoreferenceInformationException | ScriptException | RuleBaseException | LemmatizerException | InterruptedException | ExecutionException e)
		{
			throw new EDAException("Failed to process given CAS. See nested exception.",e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see eu.excitementproject.eop.common.EDABasic#shutdown()
	 */
	@Override
	public void shutdown()
	{
		try
		{
			if (underlyingSystem!=null)
			{
				underlyingSystem.cleanUp();
			}
		}
		finally
		{
			hasBeenShutDown = true;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see eu.excitementproject.eop.common.EDABasic#startTraining(eu.excitementproject.eop.common.configuration.CommonConfig)
	 */
	@Override
	public void startTraining(CommonConfig config) throws ConfigurationException, EDAException, ComponentException
	{
		logger.info(BiuteeEDA.class.getSimpleName()+": training.");
		if (trainingMode!=null)
		{
			if (trainingMode.booleanValue()) throw new EDAException("The method startTraining must not run twice for the same EDA object.");
			else throw new EDAException("Method startTraining must not be called if initialize was called.");
		}
		trainingMode = true;
		try
		{
			File biuConfigurationFile = File.createTempFile(TEMPORARY_CONFIGURATION_FILE_PREFIX, TEMPORARY_CONFIGURATION_FILE_SUFFIX);
			BiuteeEdaUtilities.convertExcitementConfigurationFileToBiuConfigurationFile(new File(config.getConfigurationFileName()), biuConfigurationFile);
			logger.info("Log file has been converted to BIU log file, and temporarily stored in "+biuConfigurationFile.getPath());
			new SystemInformationLog(biuConfigurationFile.getPath()).log();
			
			RTEPairsMultiThreadTrainer trainer = new RTEPairsMultiThreadTrainer(biuConfigurationFile.getPath());
			try
			{
				logger.info("Training system - initialization...");
				trainer.init();
				logger.info("Training system - initialization - done.");
				logger.info("Training system - training...");
				trainer.train();
				logger.info("Training system - training - done.");
			}
			finally
			{
				trainer.cleanUp();
				
				if (biuConfigurationFile!=null){ if (biuConfigurationFile.exists())
				{
					try{biuConfigurationFile.delete();}catch(RuntimeException e){}
				}}
			}


		}
		catch (IOException | ClassNotFoundException | OperationException |
				TeEngineMlException |
				eu.excitementproject.eop.common.utilities.configuration.ConfigurationException |
				TreeCoreferenceInformationException | LemmatizerException |
				PluginAdministrationException |
				ClassifierException | AnnotatorException
				| ParserRunException | SentenceSplitterException
				| CoreferenceResolutionException | ScriptException
				| RuleBaseException | InterruptedException | ExcitementToBiuConfigurationFileConverterException
				e)
		{
			throw new EDAException("Training failed. See nested exception.",e);
		}
	}

	private BiuteeEdaUnderlyingSystem underlyingSystem = null;
	private Boolean trainingMode = null;
	private boolean hasBeenShutDown = false;
	
	private static final Logger logger = Logger.getLogger(BiuteeEDA.class);
}
