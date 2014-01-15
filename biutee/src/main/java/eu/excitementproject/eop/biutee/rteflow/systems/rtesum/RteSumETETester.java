package eu.excitementproject.eop.biutee.rteflow.systems.rtesum;

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
import eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl.F1ClassifierGenerator;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtesum.RteSumInstance;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtesum.RteSumProof;
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
 * @since 2013
 *
 */
public class RteSumETETester extends EndToEndTester<RteSumInstance, RteSumProof>
{
	public static void main(String[] args)
	{
		new SystemMain()
		{
			@Override
			protected void run(String[] args) throws BiuteeException
			{
				logger = Logger.getLogger(RteSumETETester.class);
				RteSumETETester tester = new RteSumETETester(configurationFileName, ConfigurationParametersNames.RTE_SUM_TRAIN_AND_TEST_MODULE_NAME);
				try
				{
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
		}.main(RteSumETETester.class, args);
	}
	
	public RteSumETETester(String configurationFileName, String configurationModuleName)
	{
		super(configurationFileName, configurationModuleName);
		if (null==logger){logger = Logger.getLogger(RteSumETETester.class);}
	}

	@Override
	protected Dataset<RteSumInstance> createDataset() throws BiuteeException
	{
		return RTESumETEFactory.createDataset(configurationParams, ConfigurationParametersNames.RTE_SERIALIZED_DATASET_FOR_TEST, ConfigurationParametersNames.RTESUM_DATASET_FOR_TEST, teSystemEnvironment);
	}

	@Override
	protected ClassifierGenerator createClassifierGenerator() throws BiuteeException
	{
		try
		{
			File searchModelFile = configurationParams.getFile(ConfigurationParametersNames.RTE_TEST_SEARCH_MODEL);
			File predictionsModelFile = configurationParams.getFile(ConfigurationParametersNames.RTE_TEST_PREDICTIONS_MODEL);
			return new F1ClassifierGenerator(teSystemEnvironment.getFeatureVectorStructureOrganizer(),searchModelFile,predictionsModelFile);
		}
		catch (ConfigurationException e)
		{
			throw new BiuteeException("Could not create ClassifierGenerator. Please see nested exception.",e);
		}
	}

	@Override
	protected Prover<RteSumInstance, RteSumProof> createProver() throws BiuteeException
	{
		return RTESumETEFactory.createProver(teSystemEnvironment, lemmatizerProvider);
	}

	@Override
	protected ResultsFactory<RteSumInstance, RteSumProof> createResultsFactory() throws BiuteeException
	{
		return RTESumETEFactory.createResultsFactory();
	}

	@Override
	protected void printAndSaveResults(Results<RteSumInstance, RteSumProof> results) throws BiuteeException
	{
		
		File xmlResultsFile = new File(BiuteeConstants.RTE_SUM_OUTPUT_ANSWER_FILE_PREFIX+BiuteeConstants.RTE_SUM_OUTPUT_ANSWER_FILE_POSTFIX);
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
		return RTESumETEFactory.retrieveNumberOfThreads(configurationParams);
	}
	
	

	private static Logger logger = null;
}
