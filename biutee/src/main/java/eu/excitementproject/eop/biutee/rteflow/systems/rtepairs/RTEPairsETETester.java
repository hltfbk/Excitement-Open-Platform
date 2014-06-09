package eu.excitementproject.eop.biutee.rteflow.systems.rtepairs;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.plugin.PluginAdministrationException;
import eu.excitementproject.eop.biutee.rteflow.endtoend.ClassifierGenerator;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Dataset;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Prover;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Results;
import eu.excitementproject.eop.biutee.rteflow.endtoend.ResultsFactory;
import eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl.AccuracyClassifierGenerator;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs.THPairInstance;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs.THPairProof;
import eu.excitementproject.eop.biutee.rteflow.systems.EndToEndTester;
import eu.excitementproject.eop.biutee.rteflow.systems.SystemMain;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;
import eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames;
import eu.excitementproject.eop.common.utilities.ExperimentManager;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.lap.biu.lemmatizer.LemmatizerException;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

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
		new TesterSystemMain().main(RTEPairsETETester.class, args);
	}

	/**
	 * Same as main() but is more suitable to calling programmatically:
	 * - Explicit arguments, not via args[]
	 * - Doesn't "swallow" exceptions
	 * 
	 * @param configurationFileName
	 * @throws Throwable
	 */
	public static void initAndRun(String configurationFileName) throws Throwable {
		new TesterSystemMain().mainCanThrowExceptions(RTEPairsETETester.class, new String[] {configurationFileName,});
	}
	
	private static class TesterSystemMain extends SystemMain {
		@Override
		protected void run(String[] args) throws BiuteeException
		{
			logger=Logger.getLogger(RTEPairsETETester.class);
			try
			{
				RTEPairsETETester tester = new RTEPairsETETester(configurationFileName);
				tester.init();
				try
				{
					tester.test();
				}
				finally
				{
					tester.cleanUp();
				}
			} catch (TeEngineMlException
					| PluginAdministrationException
					| ConfigurationException | LemmatizerException
					| IOException e)
			{
				throw new BiuteeException("Failed to run",e);
			}
		}
	}

	public RTEPairsETETester(String configurationFileName)
	{
		super(configurationFileName, ConfigurationParametersNames.RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME);
		if (logger==null){logger=Logger.getLogger(RTEPairsETETester.class);}
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
			return new AccuracyClassifierGenerator(teSystemEnvironment.getClassifierFactory(), teSystemEnvironment.getFeatureVectorStructureOrganizer(),searchModelFile,predictionsModelFile);
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
		ExperimentManager.getInstance().register(xmlResultsFile);
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
