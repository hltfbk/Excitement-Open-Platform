package eu.excitementproject.eop.biutee.rteflow.systems.rtepairs;

import java.io.File;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.rteflow.endtoend.ClassifierGenerator;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Dataset;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Prover;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Results;
import eu.excitementproject.eop.biutee.rteflow.endtoend.ResultsFactory;
import eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl.AccuracyClassifierGenerator;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs.THPairInstance;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs.THPairProof;
import eu.excitementproject.eop.biutee.rteflow.systems.EndToEndTester;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;
import eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames;
import eu.excitementproject.eop.biutee.utilities.LogInitializer;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.ExperimentManager;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;

/**
 * 
 * @author Asher Stern
 * @since Jul 16, 2013
 *
 */
public class RTEPairsETETester extends EndToEndTester<THPairInstance, THPairProof>
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			String className = RTEPairsETETester.class.getSimpleName();
			
			if (args.length<1)
				throw new BiuteeException("Need first argument as configuration file name.");
			
			String configurationFileName = args[0];
			new LogInitializer(configurationFileName).init();
			logger = Logger.getLogger(RTEPairsETETester.class);
			
			ExperimentManager.getInstance().start();
			ExperimentManager.getInstance().setConfigurationFile(configurationFileName);
			logger.info(className);
			
			RTEPairsETETester tester = new RTEPairsETETester(configurationFileName, ConfigurationParametersNames.RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME);
			Date startDate = new Date();
			tester.init();
			try
			{
				tester.test();
			}
			finally
			{
				tester.cleanUp();
			}
			
			Date endDate = new Date();
			long elapsedSeconds = (endDate.getTime()-startDate.getTime())/1000;
			logger.info("className done. Time elapsed: "+elapsedSeconds/60+" minutes and "+elapsedSeconds%60+" seconds.");
			ExperimentManager.getInstance().save();
		}
		catch(Throwable e)
		{
			ExceptionUtil.outputException(e, System.out);
			if (logger!=null)
			{
				ExceptionUtil.logException(e, logger);
			}
		}
	}

	
	public RTEPairsETETester(String configurationFileName, String configurationModuleName)
	{
		super(configurationFileName, configurationModuleName);
	}


	@Override
	protected Dataset<THPairInstance> createDataset() throws BiuteeException
	{
		return RTEPairsETEFactory.createDataset(configurationParams, ConfigurationParametersNames.RTE_SERIALIZED_DATASET_FOR_TEST, teSystemEnvironment);
	}

	@Override
	protected ClassifierGenerator createClassifierGenerator() throws BiuteeException
	{
		try
		{
			File searchModelFile = configurationParams.getFile(ConfigurationParametersNames.RTE_TEST_SEARCH_MODEL);
			File predictionsModelFile = configurationParams.getFile(ConfigurationParametersNames.RTE_TEST_PREDICTIONS_MODEL);
			return new AccuracyClassifierGenerator(teSystemEnvironment.getFeatureVectorStructureOrganizer(),searchModelFile,predictionsModelFile);
		}
		catch (ConfigurationException e)
		{
			throw new BiuteeException("Could not create ClassifierGenerator. Please see nested exception.",e);
		}
	}

	@Override
	protected Prover<THPairInstance, THPairProof> createProver() throws BiuteeException
	{
		return RTEPairsETEFactory.createProver(teSystemEnvironment, lemmatizerProvider);
	}

	@Override
	protected ResultsFactory<THPairInstance, THPairProof> createResultsFactory() throws BiuteeException
	{
		return RTEPairsETEFactory.createResultsFactory();
	}

	@Override
	protected void printAndSaveResults(Results<THPairInstance, THPairProof> results) throws BiuteeException
	{
		File xmlResultsFile = new File(BiuteeConstants.RTE_PAIRS_XML_RESULTS_FILE_NAME_PREFIX+BiuteeConstants.RTE_PAIRS_XML_RESULTS_FILE_NAME_POSTFIX);
		logger.info("Saving results to an XML file: "+xmlResultsFile.getPath());
		results.save(xmlResultsFile);
		logger.info("Results details:");
		Iterator<String> detailsIterator = results.instanceDetailsIterator();
		while (detailsIterator.hasNext())
		{
			String details = detailsIterator.next();
			logger.info(details);
		}
		
		logger.info("Results summary: "+results.print());
	}

	@Override
	protected int retrieveNumberOfThreads() throws BiuteeException
	{
		return RTEPairsETEFactory.retrieveNumberOfThreads(configurationParams);
	}


	private static Logger logger = null;
}
